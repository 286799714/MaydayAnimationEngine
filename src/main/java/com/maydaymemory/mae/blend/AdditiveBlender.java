package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.Pose;

public interface AdditiveBlender {
    /**
     * Performing additive blending of two {@link Pose} following these rules:
     * <ul>
     *     <li> The translations are combined by adding the values of each axis.</li>
     *     <li> The rotations are combined by adding their values in Euler angles form.</li>
     *     <li> The scales is combined by multiplying the values of each axis.</li>
     * </ul>
     * <p>
     * This blending operation is commutative, meaning the order of blending does not affect the result.
     *
     * @param pose1 the first {@link Pose} to blend.
     * @param pose2 the second {@link Pose} to blend.
     * @return the resulting blended {@link Pose}.
     */
    Pose blend(Pose pose1, Pose pose2);
}
