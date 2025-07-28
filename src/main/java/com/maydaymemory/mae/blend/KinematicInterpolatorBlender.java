package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.RotationView;
import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.control.misc.AnimationVelocityEstimatorNode;
import com.maydaymemory.mae.control.misc.RealtimeVelocityEstimatorNode;

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
     * <p>
     *  <b>Notes:</b>
     *  <ul>
     *    <li>Rotation in {@code base} pose and {@code target} pose transform must be <b>unit quaternion</b>.
     *        In other words, you need to make sure that the result returned by {@link RotationView#asQuaternion()} is a unit quaternion.
     *        This is usually only necessary when you initialize {@link RotationView} with your own quaternion instance.</li>
     *    <li>Rotation in {@code baseVelocity} pose and {@code targetVelocity} pose is
     *        obtained through {@link RotationView#asEulerAngle()}, but their mathematical meaning is pure imaginary quaternion,
     *        that is, the quaternion is mapped to the rotation vector of Euclidean space through log operation.
     *        You can use {@link com.maydaymemory.mae.basic.RotationVelocityRotationView RotationVelocityRotationView} to conveniently express</li>
     *  </ul>
     * </p>
     *
     * @param base the starting pose.
     * @param baseVelocity the velocity at the starting pose. The time unit of speed must also be the same as {@code duration} and {@code time}
     * @param target the ending pose.
     * @param targetVelocity the velocity at the ending pose. The time unit of speed must also be the same as {@code duration} and {@code time}
     * @param time current time, unit is the same as {@code duration}
     * @param duration total duration time, unit is the same as {@code time}
     * @return the interpolated pose.
     * @see AnimationVelocityEstimatorNode Util to compute velocity of Animation
     * @see RealtimeVelocityEstimatorNode Util to compute velocity in realtime
     */
    Pose blend(Pose base, Pose baseVelocity, Pose target, Pose targetVelocity, float time, float duration);
}
