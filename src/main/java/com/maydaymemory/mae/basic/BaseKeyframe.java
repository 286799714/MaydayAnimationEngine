package com.maydaymemory.mae.basic;

import javax.annotation.Nonnull;

/**
 * An abstract base class for keyframes, providing a time stamp and comparison logic.
 *
 * @param <T> the type of value stored in the keyframe
 */
public abstract class BaseKeyframe<T> implements Keyframe<T> {
    /**
     * The time in seconds at which this keyframe occurs.
     */
    protected final float timeS;

    /**
     * Constructs a BaseKeyframe with the specified time.
     *
     * @param timeS the time in seconds
     */
    public BaseKeyframe(float timeS) {
        this.timeS = timeS;
    }

    /**
     * Compares this keyframe to another by their time stamps.
     *
     * @param other the other keyframe
     * @return a negative integer, zero, or a positive integer as this keyframe is less than, equal to, or greater than the specified keyframe
     */
    @Override
    public int compareTo(@Nonnull Keyframe<T> other) {
        return Float.compare(this.timeS, other.getTimeS());
    }

    /**
     * Returns the time in seconds of this keyframe.
     *
     * @return the time in seconds
     */
    @Override
    public float getTimeS() {
        return timeS;
    }
}
