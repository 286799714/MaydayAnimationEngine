package com.maydaymemory.mae.basic;

import javax.annotation.Nonnull;

public interface Keyframe<T> extends Comparable<Keyframe<T>>{
    /**
     * Get the time point corresponding to the keyframe.
     */
    float getTimeS();

    /**
     * Keyframes are compared by their time stamp in seconds (timeS).
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
