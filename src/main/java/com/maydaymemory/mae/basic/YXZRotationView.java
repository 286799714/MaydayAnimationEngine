package com.maydaymemory.mae.basic;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * A simple rotation view class that supports YXZ Tait–Bryan angles.
 * <p>
 * It lazily converts between Euler angles and quaternions as needed.
 */
public class YXZRotationView implements RotationView {
    /**
     * The Euler angles in YXZ order, in radians.
     */
    private Vector3fc angle;

    /**
     * The corresponding quaternion representing the rotation.
     */
    private Quaternionfc quaternion;

    /**
     * Creates a rotation view from YXZ Euler angles (in radians).
     *
     * @param angle Euler angles in YXZ order
     */
    public YXZRotationView(Vector3fc angle) {
        this.angle = angle;
    }

    /**
     * Creates a rotation view from a quaternion.
     *
     * @param quaternion the quaternion representing the rotation
     */
    public YXZRotationView(Quaternionfc quaternion) {
        this.quaternion = quaternion;
    }

    /**
     * Returns the rotation represented as Euler angles in YXZ order.
     * Converts from quaternion if necessary (lazy evaluation).
     *
     * @return the YXZ Euler angles (in radians)
     */
    @Override
    public Vector3fc asEulerAngle() {
        if (angle == null) {
            Vector3f angle = quaternion.getEulerAnglesYXZ(new Vector3f());

            // Handle gimbal lock: when pitch ≈ ±90°
            if (Math.abs(angle.x) > Math.PI / 2d - 1E-5d) {
                // Approximate fix: resolve ambiguity by setting roll to 0
                angle.z = 0.0f;
                angle.y = 2.0f * (float) Math.atan2(quaternion.z(), quaternion.w());
            }

            this.angle = angle;
        }
        return angle;
    }

    /**
     * Returns the rotation represented as a quaternion.
     * Converts from Euler angles if necessary (lazy evaluation).
     *
     * @return the quaternion representing the rotation
     */
    @Override
    public Quaternionfc asQuaternion() {
        if (quaternion == null) {
            quaternion = new Quaternionf().rotateYXZ(angle.y(), angle.x(), angle.z());
        }
        return quaternion;
    }

    @Override
    public String toString() {
        if (angle != null) {
            return "YXZRotationView{(radians)" + angle + '}';
        }
        if (quaternion != null) {
            return "YXZRotationView{(radians)=" + asEulerAngle() + '}';
        }
        return null;
    }
}

