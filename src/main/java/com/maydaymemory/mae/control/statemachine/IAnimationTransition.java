package com.maydaymemory.mae.control.statemachine;

import com.maydaymemory.mae.basic.Pose;

/**
 * Interface representing a transition between animation states.
 *
 * <p>This interface defines the contract for transitions that can occur
 * between animation states in a state machine. Each transition specifies
 * the target state, blending behavior, duration, and conditions for
 * when the transition should be triggered.</p>
 *
 * <b>Design Note:</b> <br>
 * <b>State and transition instances should be stateless.</b> All data required for interpolation,
 * blending, or animation playback (such as interpolators, animation runners, etc.)
 * should be stored in the <code>context</code> object. This ensures that {@code AnimationState}
 * and {@code AnimationTransition} instances are reusable, and allows
 * multiple state machines to run concurrently by simply creating new state machine
 * instances and new context objects.
 *
 * @param <T> the type of context used by this transition
 * @author MaydayMemory
 * @since 1.0.1
 */
public interface IAnimationTransition<T> {
    /**
     * Gets the target state for this transition.
     *
     * @return the state that this transition leads to
     */
    IAnimationState<T> targetState();

    /**
     * Gets the blend curve used for this transition.
     *
     * <p>The blend curve determines how the interpolation between poses
     * progresses over time during the transition.</p>
     *
     * @return the blend curve for this transition
     */
    IBlendCurve curve();

    /**
     * Gets the duration of this transition in seconds.
     *
     * @return the transition duration in seconds
     */
    float duration();

    /**
     * Gets the transfer out strategy for this transition.
     *
     * <p>The transfer out strategy determines whether this transition
     * can be interrupted by other transitions and from which states.</p>
     *
     * @return the transfer out strategy for this transition
     */
    TransferOutStrategy transferOutStrategy();

    /**
     * Determines if this transition can be triggered in the current context.
     *
     * <b>Design Note:</b> <br>
     * All data required for transition triggering should be accessed via the <code>context</code> parameter.
     * Transition instances should not store any internal state.
     *
     * @param context the context containing state information
     * @return true if this transition can be triggered, false otherwise
     */
    boolean canTrigger(T context);

    /**
     * Interpolates between two poses based on the transition progress.
     *
     * <b>Design Note:</b> <br>
     * All data required for pose interpolation (such as interpolators, etc.) should be accessed via the <code>context</code> parameter.
     * Transition instances should not store any internal state.
     *
     * @param context the context containing state information
     * @param fromPose the start pose for interpolation
     * @param toPose the target pose for interpolation
     * @param alpha the interpolation factor (0.0 to 1.0)
     * @return the interpolated pose
     */
    Pose getInterpolatedPose(T context, Pose fromPose, Pose toPose, float alpha);
}
