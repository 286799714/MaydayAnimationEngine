package com.maydaymemory.mae.basic;

import java.util.RandomAccess;

/**
 * Represents an animation channel that supports interpolation between keyframes.
 * 
 * <p>This interface extends {@link AnimationChannel} to provide interpolation capabilities
 * and implements {@link RandomAccess} to allow efficient indexed access to keyframes.
 * Interpolatable channels are used for smooth animation playback by computing
 * intermediate values between keyframes.</p>
 * 
 * @param <T> the type of value stored in this channel
 */
public interface InterpolatableChannel<T> extends AnimationChannel, RandomAccess {
    /**
     * Compute the interpolated value at the given time.
     *
     * @param timeS the time at which to compute the animation output.
     * @return animation output.
     */
    T compute(float timeS);

    /**
     * Returns the keyframe at the specified index. The interpolatable channel must support indexed access
     * to allow efficient interpolation between keyframes.
     *
     * @param index the zero-based index of the keyframe to retrieve
     * @return the {@link InterpolatableKeyframe} at the specified index
     * @throws IndexOutOfBoundsException if the index is out of range
     */
    InterpolatableKeyframe<T> getKeyFrame(int index);

    /**
     * Returns the total number of keyframes in this channel.
     *
     * @return the number of keyframes
     */
    int getKeyFrameCount();
}
