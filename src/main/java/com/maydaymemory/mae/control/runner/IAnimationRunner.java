package com.maydaymemory.mae.control.runner;

import com.maydaymemory.mae.basic.Keyframe;
import com.maydaymemory.mae.basic.Pose;

import javax.annotation.Nullable;

/**
 * Interface representing an animation runner that manages animation execution.
 * 
 * <p>This interface provides the core functionality for running animations,
 * including pose evaluation, clip extraction, and curve evaluation. It serves
 * as the main entry point for animation playback, coordinating between the
 * animation data and the execution context.</p>
 * 
 * <p>The runner manages the relationship between the animation and its context,
 * providing convenient access to animation state and progress information
 * through delegate methods to the underlying context.</p>
 * 
 * @author MaydayMemory
 * @since 1.0.1
 */
public interface IAnimationRunner {
    /**
     * Evaluates the animation at the current progress to produce a pose.
     * 
     * <p>This method evaluates the animation data at the current time position
     * and returns the resulting pose that should be applied to the target
     * skeleton or object.</p>
     * 
     * @return the evaluated pose at the current animation progress
     */
    Pose evaluate();

    /**
     * Extracts clips for a specific channel based on the current clip plans in animation context.
     *
     * <p>The clip plan is refreshed on each tick, so you need to make sure to call clip once per tick
     * to avoid losing clips.</p>
     *
     * <p><b>Important: </b>Type safety is not checked internally.
     * Please ensure that the curve type specified is the same when setting and getting.</p>
     * 
     * @param channelName the name of the channel to extract
     * @return an iterable containing the extracted clips for the specified channel, or null if the channel doesn't exist
     */
    @Nullable <T> Iterable<Keyframe<T>> clip(String channelName);

    /**
     * Evaluate the curve identified by the given name at the current progress.
     *
     * <p><b>Important: </b>Type safety is not checked internally.
     * Please ensure that the curve type specified is the same when setting and getting.</p>
     * 
     * @param curveName the name of the curve to evaluate
     * @return the evaluated curve value for the specified channel, or null if the channel doesn't exist
     */
    @Nullable <T> T evaluateCurve(String curveName);

    /**
     * Gets the animation context associated with this runner.
     * 
     * @return the animation context
     */
    IAnimationContext getAnimationContext();

    /**
     * Gets the current animation progress in nanoseconds.
     * 
     * <p>This is a convenience method that delegates to the animation context.</p>
     * 
     * @return the current progress in nanoseconds
     */
    default long getProgress() {
        return getAnimationContext().getProgress();
    }

    /**
     * Gets the current animation progress in seconds.
     * 
     * <p>This is a convenience method that delegates to the animation context.</p>
     * 
     * @return the current progress in seconds
     */
    default float getProgressInSecond() {
        return getAnimationContext().getProgressInSecond();
    }

    /**
     * Sets the current animation progress in nanoseconds.
     * 
     * <p>This is a convenience method that delegates to the animation context.</p>
     * 
     * @param progress the new progress value in nanoseconds
     */
    default void setProgress(long progress) {
        getAnimationContext().setProgress(progress);
    }

    /**
     * Sets the current animation progress in seconds.
     * 
     * <p>This is a convenience method that delegates to the animation context.</p>
     * 
     * @param timeS the new progress value in seconds
     */
    default void setProgress(float timeS) {
        getAnimationContext().setProgress(timeS);
    }

    /**
     * Gets the maximum animation progress in nanoseconds.
     * 
     * <p>This is a convenience method that delegates to the animation context.</p>
     * 
     * @return the maximum progress in nanoseconds
     */
    default long getMaxProgress() {
        return getAnimationContext().getMaxProgress();
    }

    /**
     * Gets the maximum animation progress in seconds.
     * 
     * <p>This is a convenience method that delegates to the animation context.</p>
     * 
     * @return the maximum progress in seconds
     */
    default float getMaxProgressInSecond() {
        return getAnimationContext().getMaxProgressInSecond();
    }

    /**
     * Gets the current animation state.
     * 
     * <p>This is a convenience method that delegates to the animation context.</p>
     * 
     * @return the current animation state, or null if no state is set
     */
    default @Nullable IAnimationState getState() {
        return getAnimationContext().getState();
    }

    /**
     * Sets the current animation state.
     * 
     * <p>This is a convenience method that delegates to the animation context.</p>
     * 
     * @param state the new animation state to set
     */
    default void setState(IAnimationState state) {
        getAnimationContext().setState(state);
    }
}
