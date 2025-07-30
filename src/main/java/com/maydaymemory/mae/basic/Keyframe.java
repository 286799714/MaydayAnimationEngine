package com.maydaymemory.mae.basic;

import javax.annotation.Nonnull;

public interface Keyframe<T> extends Comparable<Keyframe<T>>{
    /**
     * Get the time point corresponding to the keyframe.
     *
     * @return the time point corresponding to the keyframe.
     */
    float getTimeS();

    /**
     * Keyframes are compared by their time stamp in seconds (timeS).
     *
     * @param other the other keyframe to compare to.
     * @return
     * -1 if this keyframe is before the other keyframe,
     * 0 if they are at the same time,
     * and 1 if this keyframe is after the other keyframe.
     */
    @Override
    int compareTo(@Nonnull Keyframe<T> other);

    /**
     * Get the value of this keyframe.
     *
     * @return the value of this keyframe.
     */
    T getValue();
}
