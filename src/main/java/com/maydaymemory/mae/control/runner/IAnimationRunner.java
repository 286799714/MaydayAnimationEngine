package com.maydaymemory.mae.control.runner;

import com.maydaymemory.mae.basic.Pose;

import javax.annotation.Nullable;
import java.util.List;

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
     * Extracts clips for all channels based on the current clip plans in animation context.
     * 
     * <p>This method processes the clip plans in the context and extracts
     * the corresponding clips for all clipable channels. </p>
     * 
     * @return a list of iterables containing the extracted clips for each channel
     */
    List<Iterable<?>> clip();

    /**
     * Extracts clips for a specific channel based on the current clip plans in animation context.
     * 
     * <p>This method processes the clip plans in the context and extracts
     * the corresponding animation clips for the specified channel index.</p>
     * 
     * @param i the index of the channel to extract clips for
     * @return an iterable containing the extracted clips for the specified channel, or null if the channel doesn't exist
     */
    @Nullable Iterable<?> clip(int i);

    /**
     * Evaluates curves for all channels at the current progress.
     * 
     * <p>This method evaluates the animation curves at the current time position
     * and returns the resulting values for all channels.</p>
     * 
     * @return a list of objects containing the evaluated curve values for each channel
     */
    List<Object> evaluateCurve();

    /**
     * Evaluates curves for a specific channel at the current progress.
     * 
     * <p>This method evaluates the animation curve at the current time position
     * for the specified channel index.</p>
     * 
     * @param i the index of the channel to evaluate curves for
     * @return the evaluated curve value for the specified channel, or null if the channel doesn't exist
     */
    @Nullable Object evaluateCurve(int i);

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
