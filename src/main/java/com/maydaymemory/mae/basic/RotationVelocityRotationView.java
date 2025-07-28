package com.maydaymemory.mae.basic;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;

public class RotationVelocityRotationView implements RotationView {
    private final Vector3fc velocity;
    private Quaternionf quaternionf;

    public RotationVelocityRotationView(Vector3fc velocity) {
        this.velocity = velocity;
    }

    @Override
    public Vector3fc asEulerAngle() {
        return velocity;
    }

    @Override
    public Quaternionfc asQuaternion() {
        if (quaternionf == null) {
            quaternionf = new Quaternionf(velocity.x(), velocity.y(), velocity.z(), 0);
        }
        return quaternionf;
    }
}