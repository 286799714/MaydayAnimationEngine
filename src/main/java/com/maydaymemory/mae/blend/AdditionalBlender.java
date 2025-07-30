package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.Pose;

/**
 * Defines a blending strategy that performs *additional blending* of two {@link Pose} instances.
 * <p>
 * The blending rules are as follows:
 * <ul>
 *     <li>Translations are combined by adding the values of each axis.</li>
 *     <li>Rotations are combined using <b>quaternion multiplication</b>.</li>
 *     <li>Scales are combined by multiplying the values of each axis.</li>
 * </ul>
 * <p>
 * Note: Since quaternion multiplication is <b>not commutative</b>, the order in which poses are blended
 * matters. That means:
 * <pre>{@code
 * blend(basePose, additionalPose) ≠ blend(additionalPose, basePose)
 * }</pre>
 * This blending mode is suitable for scenarios where one pose should be applied as an offset or delta on top of another.
 *
 * @see AdditiveBlender for a commutative variant using Euler-angle addition.
 */
public interface AdditionalBlender {
    /**
     * Perform additional blending of two poses.
     *
     * <p>The blending rules are as follows:</p>
     * <ul>
     *     <li>Translations are combined by adding the values of each axis.</li>
     *     <li>Rotations are combined using <b>quaternion multiplication</b>.</li>
     *     <li>Scales are combined by multiplying the values of each axis.</li>
     * </ul>
     * @param basePose the base pose
     * @param additionalPose the additional pose
     * @return the blended pose
     */
    Pose blend(Pose basePose, Pose additionalPose);
}
