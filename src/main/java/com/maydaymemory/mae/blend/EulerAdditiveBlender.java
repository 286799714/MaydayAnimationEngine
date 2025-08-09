package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.Pose;

/**
 * Defines a blending strategy that performs *additive blending* of two {@link Pose} instances.
 * <p>The blending rules are as follows:</p>
 * <ul>
 *     <li>Translations are combined by adding the values of each axis.</li>
 *     <li>Rotations are combined in the form of Euler angles, adding values of each angle.</li>
 *     <li>Scales are combined by multiplying the values of each axis.</li>
 * </ul>
 * This blending operation is <b>commutative</b>, meaning the order of blending does not affect the result:
 * <pre>{@code
 * blend(pose1, pose2) == blend(pose2, pose1)
 * }</pre>
 * <p>
 * Compared to {@link AdditiveBlender}, which uses quaternion multiplication for rotation blending
 * and is sensitive to blending order, this approach prioritizes simplicity and symmetry.
 * </p>
 *
 * @see AdditiveBlender for a non-commutative blending mode using quaternion multiplication.
 */
public interface EulerAdditiveBlender {
    /**
     * Blends two poses together using additive blending.
     *
     * <p>The blending rules are as follows:</p>
     * <ul>
     *     <li>Translations are combined by adding the values of each axis.</li>
     *     <li>Rotations are combined in the form of Euler angles, adding values of each angle.</li>
     *     <li>Scales are combined by multiplying the values of each axis.</li>
     * </ul>
     *
     * @param pose1 the first pose
     * @param pose2 the second pose
     * @return the blended pose
     */
    Pose blend(Pose pose1, Pose pose2);
}
