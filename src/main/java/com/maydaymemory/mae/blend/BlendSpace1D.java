package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.Pose;

/**
 * Represents a 1D blend space that interpolates between poses based on a single float parameter.
 */
public interface BlendSpace1D {
    /**
     * Sets the pose at the specified sampler index.
     *
     * @param index the index of the sample pose to set, need to be positive.
     * @param pose the pose to associate with the given index
     * @throws IndexOutOfBoundsException if the index is negative number
     */
    void setSamplerPose(int index, Pose pose);

    /**
     * Sets the position value associated with the sampler at the specified index.
     *
     * @param index the index of the sample point, need to be positive.
     * @param position the 1D position parameter for the sampler
     * @throws IllegalStateException if the sampler point is no longer editable
     * @throws IndexOutOfBoundsException if the index is negative number
     */
    void setSamplerPosition(int index, float position);

    /**
     * Computes and returns the blended pose corresponding to the given position parameter.
     *
     * @param position the position to perform blending
     * @return the blended pose
     */
    Pose blend(float position);
}
