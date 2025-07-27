package com.maydaymemory.mae.control.statemachine;

import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.util.LongSupplier;
import com.maydaymemory.mae.util.MathUtil;

/**
 * Container class that manages the execution of a transition between animation states.
 * 
 * <p>This class encapsulates all the information needed to
 * execute a transition between two animation states. It holds the source state,
 * the transition definition, the transition controller for timing, and a cached
 * pose as the starting pose of interpolation, which avoids pose mutations</p>
 * 
 * <p>The transition control block is created when a transition is triggered and
 * manages the entire transition process until completion or interruption.</p>
 * 
 * @param <T> the type of context used by the states and transitions
 * @author MaydayMemory
 * @since 1.0.1
 */
public class TransitionControlBlock<T> {
    /** The source state that the transition is coming from */
    private final IAnimationState<T> fromState;
    
    /** The transition being executed */
    private final IAnimationTransition<T> transition;
    
    /** The controller managing the transition timing and progress */
    private final TransitionController controller;
    
    /** The cached pose from the source state for interpolation */
    private final Pose cachedPose;

    /**
     * Constructs a new TransitionControlBlock with the specified parameters.
     * 
     * @param fromState the source state that the transition is coming from
     * @param transition the transition to execute
     * @param cachedPose the pose from the source state to use for interpolation
     * @param currentNanosSupplier supplier for current time in nanoseconds
     */
    public TransitionControlBlock(IAnimationState<T> fromState, IAnimationTransition<T> transition,
                                  Pose cachedPose, LongSupplier currentNanosSupplier) {
        this.fromState = fromState;
        this.transition = transition;
        this.controller = new TransitionController(currentNanosSupplier, MathUtil.toNanos(transition.duration()), transition.curve());
        this.cachedPose = cachedPose;
    }

    /**
     * Gets the transition controller managing timing and progress.
     * 
     * @return the transition controller
     */
    public TransitionController getController() {
        return controller;
    }

    /**
     * Gets the source state that the transition is coming from.
     * 
     * @return the source state
     */
    public IAnimationState<T> getFromState() {
        return fromState;
    }

    /**
     * Gets the transition being executed.
     * 
     * @return the transition
     */
    public IAnimationTransition<T> getTransition() {
        return transition;
    }

    /**
     * Gets the cached pose from the source state.
     * 
     * @return the cached pose for interpolation
     */
    public Pose getCachedPose() {
        return cachedPose;
    }
}
