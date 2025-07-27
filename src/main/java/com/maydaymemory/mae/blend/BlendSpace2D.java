package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.Pose;

/**
 * Represents a 2D blend space that interpolates between poses based on two float parameters (x, y).
 * The general implementation relies on triangulation.
 *
 * <p>Example usage (walking animation):</p>
 * <pre>{@code
 * // Create the blend space
 * BlendSpace2D blendSpace = new ClampToEdgeBlendSpace2D(boneTransformFactory, poseBuilderSupplier);
 * // Set sample points
 * blendSpace.setSamplerPosition(0, 0, 0);      // Idle
 * blendSpace.setSamplerPosition(1, 0, 1);      // Walk Forward
 * blendSpace.setSamplerPosition(2, 0, -1);     // Walk Backward
 * blendSpace.setSamplerPosition(3, -1, 0);     // Walk Left
 * blendSpace.setSamplerPosition(4, 1, 0);      // Walk Right
 * // Triangulate before blending
 * ((ClampToEdgeBlendSpace2D) blendSpace).triangulate();
 * // Set sample poses
 * blendSpace.setSamplerPose(0, idlePose);          // Idle pose from the idle animation
 * blendSpace.setSamplerPose(1, walkForwardPose);   // Walk forward pose from the walk animation
 * blendSpace.setSamplerPose(2, walkBackwardPose);  // Walk backward pose from the walk animation
 * blendSpace.setSamplerPose(3, walkLeftPose);      // Walk left pose from the walk animation
 * blendSpace.setSamplerPose(4, walkRightPose);     // Walk right pose from the walk animation
 * // Use player direction as blend input
 * Pose blendedPose = blendSpace.blend(playerDirection.x, playerDirection.z);
 * }</pre>
 *
 * <p>ClampToEdgeBlendSpace2D is a typical implementation for this scenario.</p>
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
