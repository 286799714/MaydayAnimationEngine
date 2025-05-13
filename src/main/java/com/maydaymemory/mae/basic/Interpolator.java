package com.maydaymemory.mae.basic;

public interface Interpolator<T> {
    T interpolate(InterpolatableChannel<T> channel, int indexFrom, int indexTo, float alpha);

    /**
     * Get the priority of the current interpolator. When interpolating between two keyframes,
     * each keyframe specifies an interpolator, and the one with the higher priority will be used first.
     * If the priorities are equal, the interpolator of the earlier keyframe will be used
     * @return the priority of interpolator.
     */
    Priority getPriority();

    enum Priority{
        VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH;
    }
}
