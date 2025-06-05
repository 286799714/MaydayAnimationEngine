package com.maydaymemory.mae.basic;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * A simple rotation view class that supports XYZ Tait–Bryan angles.
 * <p>
 * It lazily converts between Euler angles and quaternions as needed.
 */
public class XYZRotationView implements RotationView {
    /**
     * The Euler angles in XYZ order, in radians.
     */
    private Vector3fc angle;

    /**
     * The corresponding quaternion representing the rotation.
     */
    private Quaternionfc quaternion;

    /**
     * Creates a rotation view from XYZ Euler angles (in radians).
     *
     * @param angle Euler angles in XYZ order
     */
    public XYZRotationView(Vector3fc angle) {
        this.angle = angle;
    }

    /**
     * Creates a rotation view from a quaternion.
     *
     * @param quaternion the quaternion representing the rotation
     */
    public XYZRotationView(Quaternionfc quaternion) {
        this.quaternion = quaternion;
    }

    /**
     * Returns the rotation represented as Euler angles in XYZ order.
     * Converts from quaternion if necessary (lazy evaluation).
     *
     * @return the XYZ Euler angles (in radians)
     */
    @Override
    public Vector3fc asEulerAngle() {
        if (angle == null) {
            Vector3f angle = new Vector3f();
            float x = quaternion.x();
            float y = quaternion.y();
            float z = quaternion.z();
            float w = quaternion.w();
            angle.y = org.joml.Math.safeAsin(2.0f * (x * z + y * w));

            // Handle gimbal lock: when pitch ≈ ±90°
            if (angle.y > Math.PI / 2d - 1E-5d) {
                angle.z = 0.0f;
                angle.x = -2.0f * (float) Math.atan2(z, w);
            } else if (angle.y < -(Math.PI / 2d - 1E-5d)) {
                angle.z = 0.0f;
                angle.x = 2.0f * (float) Math.atan2(z, w);
            } else {
                angle.z = (float) Math.atan2(z * w - x * y, 0.5f - y * y - z * z);
                angle.x = (float) Math.atan2(x * w - y * z, 0.5f - x * x - y * y);
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
            quaternion = new Quaternionf().rotateXYZ(angle.x(), angle.y(), angle.z());
        }
        return quaternion;
    }

    @Override
    public String toString() {
        if (angle != null) {
            return "XYZRotationView{(radians)" + angle + '}';
        }
        if (quaternion != null) {
            return "XYZRotationView{(radians)=" + asEulerAngle() + '}';
        }
        return null;
    }
}
