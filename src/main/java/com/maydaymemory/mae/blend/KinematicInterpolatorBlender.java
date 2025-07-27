package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.RotationView;
import com.maydaymemory.mae.basic.Pose;

public interface KinematicInterpolatorBlender {
    /**
     * Perform kinematic interpolation.
     *
     * <p>Internally, a mathematical curve will be used to ensure that the first and second derivatives of the interpolated trajectory
     * are continuous. The boundary conditions are the initial and final positions and the initial and final velocities (the first derivative of the position),
     * and the implicit condition is that the initial and final accelerations (the second derivative of the position) are 0.</p>
     *
     * <p>The goal of this method is to produce a smooth interpolated pose that accounts for both position and velocity-like contributions
     * from both ends, providing smoother transitions than standard linear or spherical interpolation. </p>
     *
     * <ul>
     *     <li>{@code base}: The starting pose of the transition. Represents the initial position, rotation, and scale of each bone.</li>
     *     <li>{@code deltaBaseContribution}: The total contribution of the initial velocity over the entire transition duration. It is a pose whose translation, rotation (as a rotation vector), and scale components represent the velocity at {@code base} scaled by total transition time.</li>
     *     <li>{@code target}: The target pose at the end of the transition. Represents the desired final transform of each bone.</li>
     *     <li>{@code deltaTargetContribution}: The total contribution of the final velocity over the entire transition duration. Analogous to {@code deltaBaseContribution}, but relative to {@code target}.</li>
     *     <li>{@code weight}: A scalar in [0, 1] representing normalized transition progress. {@code weight = 0} corresponds to {@code base}, and {@code weight = 1} corresponds to {@code target}.</li>
     * </ul>
     *
     * <p>
     *  <b>Notes:</b>
     *  <ul>
     *    <li>Rotation in {@code base} pose and {@code target} pose transform must be <b>unit quaternion</b>.
     *        In other words, you need to make sure that the result returned by {@link RotationView#asQuaternion()} is a unit quaternion.
     *        This is usually only necessary when you initialize {@link RotationView} with your own quaternion instance.</li>
     *    <li>Rotation in {@code deltaBaseContribution} pose and {@code deltaTargetContribution} pose is
     *        obtained through {@link RotationView#asEulerAngle()}, but their mathematical meaning is pure imaginary quaternion,
     *        that is, the quaternion is mapped to the rotation vector of Euclidean space through log operation.</li>
     *  </ul>
     * </p>
     *
     * @param base the starting pose.
     * @param deltaBaseContribution the velocity (or motion derivative) contribution at the starting pose.
     * @param target the ending pose.
     * @param deltaTargetContribution the velocity (or motion derivative) contribution at the ending pose.
     * @param weight interpolation factor between 0 and 1.
     * @return the interpolated pose.
     * @throws IllegalArgumentException if {@code deltaBaseContribution} does not match with base
     *                                  or {@code deltaTargetContribution} does not match with target.
     *                                  There must be the same number of BoneTransforms, and their bone indexes must correspond one to one.
     */
    Pose blend(Pose base, Pose deltaBaseContribution, Pose target, Pose deltaTargetContribution, float weight);
}
