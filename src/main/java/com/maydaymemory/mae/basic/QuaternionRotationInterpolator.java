package com.maydaymemory.mae.basic;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;

/**
 * Interpolator for Rotation in Quaternion form.
 * Internally uses slerp for interpolation.
 */
public class QuaternionRotationInterpolator implements Interpolator<Rotation>{
    @Override
    public Rotation interpolate(InterpolatableChannel<Rotation> channel, int indexFrom, int indexTo, float alpha) {
        InterpolatableKeyframe<Rotation> k1 = channel.getKeyFrame(indexFrom);
        InterpolatableKeyframe<Rotation> k2 = channel.getKeyFrame(indexTo);
        Rotation r1 = k1.getPost();
        Rotation r2 = k2.getPre();
        if (!r1.isQuaternion() || !r2.isQuaternion()) {
            throw new IllegalArgumentException("Rotation must be quaternion when using quaternion interpolator");
        }
        Quaternionfc q1 = r1.getQuaternion();
        Quaternionfc q2 = r2.getQuaternion();
        return new Rotation(q1.slerp(q2, alpha, new Quaternionf()));
    }

    @Override
    public Priority getPriority() {
        return Priority.MEDIUM;
    }
}
