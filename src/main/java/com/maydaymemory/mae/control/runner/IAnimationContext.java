package com.maydaymemory.mae.control.runner;

import com.maydaymemory.mae.util.MathUtil;
import it.unimi.dsi.fastutil.longs.LongLongImmutablePair;

import javax.annotation.Nullable;
import java.util.Iterator;

/**
 * Interface representing the context for animation execution.
 * 
 * <p>This interface provides the core functionality for managing animation state,
 * progress tracking, and clip planning. It serves as the central coordination
 * point for animation execution, maintaining the current state, progress,
 * and timing information.</p>
 * 
 * <p>The context manages both the animation state machine and the clip plan
 * queue, which allows for precise control over animation playback and
 * enables features like clip extraction and event handling.</p>
 * 
 * @author MaydayMemory
 * @since 1.0.1
 */
public interface IAnimationContext {
    /**
     * Gets the current animation progress in nanoseconds.
     * 
     * @return the current progress in nanoseconds
     */
    long getProgress();

    /**
     * Sets the current animation progress in nanoseconds.
     * 
     * @param progress the new progress value in nanoseconds
     */
    void setProgress(long progress);

    /**
     * Gets the timestamp of the last update in nanoseconds.
     * 
     * @return the last update time in nanoseconds
     */
    long getLastUpdateTime();

    /**
     * Sets the timestamp of the last update in nanoseconds.
     * 
     * @param lastUpdateTime the new last update time in nanoseconds
     */
    void setLastUpdateTime(long lastUpdateTime);

    /**
     * Gets the maximum progress value in nanoseconds.
     * 
     * @return the maximum progress in nanoseconds
     */
    long getMaxProgress();

    /**
     * Enqueues a clip plan for processing.
     * 
     * <p>Clip plans represent time ranges that should be processed for
     * clip extraction. They are stored as pairs of
     * start and end times in nanoseconds.</p>
     * 
     * @param plan the clip plan to enqueue
     */
    void enqueueClipPlan(LongLongImmutablePair plan);

    /**
     * Gets an iterator over the current clip plans.
     * 
     * @return an iterator for the clip plan queue
     */
    Iterator<? extends LongLongImmutablePair> clipPlanIterator();

    /**
     * Sets the current animation state.
     * 
     * @param state the new animation state to set
     */
    void setState(IAnimationState state);

    /**
     * Gets the current animation state.
     * 
     * @return the current animation state, or null if no state is set
     */
    @Nullable IAnimationState getState();

    /**
     * Checks if the animation has reached an end point.
     * 
     * @return true if the animation has ended, false otherwise
     */
    boolean isEnd();

    /**
     * Updates the animation context.
     * 
     * <p>This method should be called regularly to advance the animation
     * state and process any pending updates.</p>
     */
    void update();

    /**
     * Sets the current animation progress in seconds.
     * 
     * <p>This is a convenience method that converts seconds to nanoseconds
     * before setting the progress.</p>
     * 
     * @param timeS the new progress value in seconds
     */
    default void setProgress(float timeS) {
        setProgress(MathUtil.toNanos(timeS));
    }

    /**
     * Gets the current animation progress in seconds.
     * 
     * <p>This is a convenience method that converts nanoseconds to seconds
     * for easier human-readable progress values.</p>
     * 
     * @return the current progress in seconds
     */
    default float getProgressInSecond() {
        return MathUtil.toSecond(getProgress());
    }

    /**
     * Gets the maximum animation progress in seconds.
     * 
     * <p>This is a convenience method that converts nanoseconds to seconds
     * for easier human-readable progress values.</p>
     * 
     * @return the maximum progress in seconds
     */
    default float getMaxProgressInSecond() {
        return MathUtil.toSecond(getMaxProgress());
    }
}
