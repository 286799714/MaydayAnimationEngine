package com.maydaymemory.mae.basic;

/**
 * Animation channel interface.
 *
 * <p>
 * An AnimationChannel is a "track" in animation data that stores a certain type of keyframe
 * (such as position, rotation, or scale) and is used to drive the animation properties of a
 * specific target over time.
 * </p>
 */
public interface AnimationChannel {
    /**
     * Get the end time of this channel
     *
     * @return the end time in second.
     */
    float getEndTimeS();
}
