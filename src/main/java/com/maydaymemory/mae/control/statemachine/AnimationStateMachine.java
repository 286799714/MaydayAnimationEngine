package com.maydaymemory.mae.control.statemachine;

import com.maydaymemory.mae.basic.DummyPose;
import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.control.OutputPort;
import com.maydaymemory.mae.control.Tickable;
import com.maydaymemory.mae.util.LongSupplier;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * Implementation of an animation state machine that manages transitions between states.
 * 
 * <p>This class provides a complete state machine implementation for animation
 * control, managing state transitions, pose evaluation, and transition timing.
 * It implements the {@link Tickable} interface for integration with game loops
 * and provides an output port for pose evaluation.</p>
 * 
 * <p>The state machine supports complex transition behaviors including
 * interruption handling, smooth pose interpolation, and flexible state
 * management. It can handle both active states and transitions in progress,
 * providing seamless animation control.</p>
 * 
 * @param <T> the type of context used by the states and transitions. If it is {@link Tickable},
 *           statemachine will also automatically tick it when statemachine itself is ticked.
 * @author MaydayMemory
 * @since 1.0.1
 */
public class AnimationStateMachine<T> implements Tickable {
    /** The current active state, or null if a transition is in progress */
    private IAnimationState<T> state;
    
    /** The current transition control block, or null if no transition is active */
    private TransitionControlBlock<T> transitionControlBlock;
    
    /**
     * The context used by states and transitions.
     * If it is {@link Tickable},statemachine will also automatically tick it when statemachine itself is ticked.
     */
    private final T context;
    
    /** Supplier for current time in nanoseconds */
    private final LongSupplier currentNanosSupplier;
    
    /** Output port for pose evaluation */
    private final OutputPort<Pose> outputPort = this::getPose;

    /**
     * Constructs a new AnimationStateMachine with the specified initial state and context.
     * 
     * @param initialState the initial state for the state machine
     * @param context the context to be used by states and transitions
     * @param currentNanosSupplier supplier for current time in nanoseconds
     */
    public AnimationStateMachine(IAnimationState<T> initialState, T context, LongSupplier currentNanosSupplier) {
        this.state = initialState;
        this.context = context;
        this.currentNanosSupplier = currentNanosSupplier;
    }

    @Override
    public void tick() {
        if (context instanceof Tickable) {
            ((Tickable)context).tick();
        }
        if (state != null) {
            state.onUpdate(context);
            TransitionControlBlock<T> tcb = tryTransfer(state, () -> state.evaluatePose(context));
            if (tcb != null) {
                this.transitionControlBlock = tcb;
                state.onExit(context, tcb.getTransition());
                state = null;
                tcb.getTransition().afterTrigger(context);
            }
        } else if (transitionControlBlock != null) {
            if (transitionControlBlock.getController().isFinished()) {
                IAnimationState<T> targetState = transitionControlBlock.getTransition().targetState();
                targetState.onEnter(context, transitionControlBlock.getFromState());
                this.state = targetState;
                transitionControlBlock = null;
                return;
            }
            IAnimationTransition<T> transition = transitionControlBlock.getTransition();
            IAnimationState<T> fromState = transitionControlBlock.getFromState();
            IAnimationState<T> targetState = transition.targetState();
            Supplier<Pose> cachedPoseSupplier = () -> transition.getInterpolatedPose(
                    context,
                    transitionControlBlock.getCachedPose(),
                    targetState.evaluatePose(context),
                    transitionControlBlock.getController().getTransitionProgress()
            );
            TransitionControlBlock<T> tcb = null;
            switch (transition.transferOutStrategy()) {
                case NONE:
                    break;
                case ANY_STATE:
                    tcb = (tcb = tryTransfer(fromState, cachedPoseSupplier)) != null ? tcb : tryTransfer(targetState, cachedPoseSupplier);
                    break;
                case FROM_STATE:
                    tcb = tryTransfer(fromState, cachedPoseSupplier);
                    break;
                case TO_STATE:
                    tcb= tryTransfer(targetState, cachedPoseSupplier);
                    break;
            }
            if (tcb != null) {
                this.transitionControlBlock = tcb;
                tcb.getTransition().afterTrigger(context);
            }
        }
    }

    /**
     * Gets the current active state.
     * 
     * @return the current state, or null if a transition is in progress
     */
    public @Nullable IAnimationState<T> getCurrentState() {
        return state;
    }

    /**
     * Gets the current transition control block.
     * 
     * @return the current transition control block, or null if no transition is active
     */
    public @Nullable TransitionControlBlock<T> getCurrentTransition() {
        return transitionControlBlock;
    }

    /**
     * Evaluates the current pose based on the state machine's current state.
     * 
     * <p>This method returns the appropriate pose based on whether the state
     * machine is in an active state or transitioning between states. During
     * transitions, it interpolates between the source and target poses.</p>
     * 
     * @return the evaluated pose
     */
    public Pose getPose() {
        if (state != null) {
            return state.evaluatePose(context);
        } else if (transitionControlBlock != null) {
            IAnimationTransition<T> transition = transitionControlBlock.getTransition();
            Pose fromPose = transitionControlBlock.getCachedPose();
            Pose toPose = transition.targetState().evaluatePose(context);
            float progress = transitionControlBlock.getController().getTransitionProgress();
            return transition.getInterpolatedPose(context, fromPose, toPose, progress);
        }
        // This should not be reached.
        return DummyPose.INSTANCE;
    }

    /**
     * Gets the output port for pose evaluation.
     * 
     * @return the output port that provides evaluated poses
     */
    public OutputPort<Pose> getOutputPort() {
        return outputPort;
    }

    /**
     * Gets context instance combined with this statemachine
     *
     * @return context instance combined with this statemachine
     */
    public T getContext() {
        return context;
    }

    /**
     * Attempts to trigger a transition from the specified state. This is an internal method.
     * 
     * <p>This method evaluates all transitions from the given state and
     * creates a transition control block for the first transition that
     * can be triggered.</p>
     * 
     * @param state the state to check for transitions
     * @param cachedPoseSupplier supplier for the pose to cache from the current state
     * @return a transition control block if a transition was triggered, null otherwise
     */
    private TransitionControlBlock<T> tryTransfer(IAnimationState<T> state, Supplier<Pose> cachedPoseSupplier) {
        for (IAnimationTransition<T> transition : state.transitions()) {
            if (transition.canTrigger(context)) {
                TransitionControlBlock<T> tcb = new TransitionControlBlock<>(state, transition, cachedPoseSupplier.get(), currentNanosSupplier);
                tcb.getController().start();
                return tcb;
            }
        }
        return null;
    }
}
