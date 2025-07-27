package com.maydaymemory.mae.control.statemachine;

import com.maydaymemory.mae.basic.Pose;

/**
 * Interface representing a state in the animation state machine.
 *
 * <p>This interface defines the contract for animation states that can be used
 * within an animation state machine. Each state represents a different behavior
 * or phase of animation execution, such as idle, walking, running, or other
 * animation states.</p>
 *
 * <b>Design Note:</b> <br>
 * <b>State and transition instances should be stateless.</b> All data required for pose evaluation,
 * interpolation, or animation playback (such as animation runners, interpolators, etc.)
 * should be stored in the <code>context</code> object. This ensures that {@code AnimationState}
 * and {@code AnimationTransition} instances are reusable and thread-safe, and allows
 * multiple state machines to run concurrently by simply creating new state machine
 * instances and new context objects.
 *
 * @param <T> the type of context used by this state
 * @author MaydayMemory
 * @since 1.0.1
 */
public interface IAnimationState<T> {
    /**
     * Gets all possible transitions from this state.
     *
     * <p>This method returns an iterable collection of transitions that can
     * be triggered from this state. The state machine will evaluate these
     * transitions to determine if any should be activated.</p>
     *
     * @return an iterable of transitions from this state
     */
    Iterable<IAnimationTransition<T>> transitions();

    /**
     * Called when entering this state.
     *
     * <b>Design Note:</b> <br>
     * All data required for state entry should be accessed via the <code>context</code> parameter.
     * State instances should not store any internal state.
     *
     * @param context the context containing state information
     * @param fromState the state that was active before this transition
     */
    void onEnter(T context, IAnimationState<T> fromState);

    /**
     * Called when exiting this state.
     *
     * <b>Design Note:</b> <br>
     * All data required for state exit should be accessed via the <code>context</code> parameter.
     * State instances should not store any internal state.
     *
     * @param context the context containing state information
     * @param triggeredTransition the transition that caused this state to exit
     */
    void onExit(T context, IAnimationTransition<T> triggeredTransition);

    /**
     * Called during each update cycle while this state is active.
     *
     * <b>Design Note:</b> <br>
     * All data required for state update should be accessed via the <code>context</code> parameter.
     * State instances should not store any internal state.
     *
     * @param context the context containing state information
     */
    void onUpdate(T context);

    /**
     * Evaluates the pose for this state at the current time.
     *
     * <b>Design Note:</b> <br>
     * All data required for pose evaluation (such as animation runners, etc.) should be accessed via the <code>context</code> parameter.
     * State instances should not store any internal state.
     *
     * @param context the context containing state information
     * @return the evaluated pose for this state, used for rendering or transition interpolation.
     */
    Pose evaluatePose(T context);
}
