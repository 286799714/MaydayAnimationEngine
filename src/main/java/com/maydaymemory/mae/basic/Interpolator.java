package com.maydaymemory.mae.basic;

/**
 * Defines an interpolation strategy for computing intermediate values between keyframes.
 * 
 * <p>This interface provides a mechanism for interpolating between two keyframes in an
 * animation channel. Different interpolators can implement various interpolation algorithms
 * such as linear, cubic, or custom mathematical functions.</p>
 * 
 * @param <T> the type of value to interpolate
 */
public interface Interpolator<T> {
    /**
     * Interpolates between two keyframes in the specified channel.
     * 
     * @param channel the channel containing the keyframes
     * @param indexFrom the index of the starting keyframe
     * @param indexTo the index of the ending keyframe
     * @param alpha the interpolation factor (0.0 to 1.0)
     * @return the interpolated value
     */
    T interpolate(InterpolatableChannel<T> channel, int indexFrom, int indexTo, float alpha);

    /**
     * Get the priority of the current interpolator. When interpolating between two keyframes,
     * each keyframe specifies an interpolator, and the one with the higher priority will be used first.
     * If the priorities are equal, the interpolator of the earlier keyframe will be used
     *
     * @return the priority of interpolator.
     */
    Priority getPriority();

    /**
     * Defines the priority levels for interpolators when multiple interpolators are available.
     * Higher priority interpolators are preferred over lower priority ones.
     */
    enum Priority{
        /** Very low priority - used as fallback */
        VERY_LOW, 
        /** Low priority */
        LOW, 
        /** Medium priority */
        MEDIUM, 
        /** High priority */
        HIGH, 
        /** Very high priority - preferred over others */
        VERY_HIGH;
    }
}
