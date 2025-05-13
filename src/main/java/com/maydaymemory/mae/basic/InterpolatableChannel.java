package com.maydaymemory.mae.basic;

import java.util.RandomAccess;

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
