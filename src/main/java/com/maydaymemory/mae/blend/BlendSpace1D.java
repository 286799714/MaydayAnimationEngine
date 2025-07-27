package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.Pose;

/**
 * Represents a 1D blend space that interpolates between poses based on a single float parameter.
 *
 * <p>Example usage (idle-run blend):</p>
 * <pre>{@code
 * // Create the blend space
 * BlendSpace1D blendSpace = new SimpleBlendSpace1D(boneTransformFactory, poseBuilderSupplier);
 * // Set sample points
 * blendSpace.setSamplerPosition(0, 0.0f);   // Idle (speed = 0)
 * blendSpace.setSamplerPosition(1, 1.0f);   // Run (speed = 1)
 * // Set sample poses
 * blendSpace.setSamplerPose(0, idlePose);   // Idle pose
 * blendSpace.setSamplerPose(1, runPose);    // Run pose
 * // Blend based on current speed (0.0 = idle, 1.0 = run)
 * Pose blendedPose = blendSpace.blend(currentSpeed);
 * }</pre>
 *
 * <p>SimpleBlendSpace1D is a typical implementation for this scenario.</p>
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
