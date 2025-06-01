package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.Pose;

/**
 * Represents a 2D blend space that interpolates between poses based on two float parameters (x, y).
 * The general implementation relies on triangulation
 */
public interface BlendSpace2D {
    /**
     * Sets the pose at the specified sampler index.
     *
     * @param index the index of the sample point, need to be positive.
     * @param pose the pose to set
     * @throws IndexOutOfBoundsException if the index is negative number
     */
    void setSamplerPose(int index, Pose pose);

    /**
     * Sets the sample point at the given index with the specified coordinates.
     *
     * @param index the index of the sample point, need to be positive.
     * @param x the X coordinate
     * @param y the Y coordinate
     * @throws IllegalStateException if the sampler point is no longer editable
     * @throws IndexOutOfBoundsException if the index is negative number
     */
    void setSamplerPosition(int index, float x, float y);

    /**
     * Computes and returns the blended pose corresponding to the given 2D input coordinates.
     *
     * @param x the X coordinate of the blend input
     * @param y the Y coordinate of the blend input
     * @return the interpolated pose at the given (x, y) position
     */
    Pose blend(float x, float y);
}
