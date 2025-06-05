package com.maydaymemory.mae.basic;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * A simple rotation view class that supports ZYX Tait–Bryan angles.
 * <p>
 * It lazily converts between the two representations as needed.
 */
public class ZXYRotationView implements RotationView{
    /**
     * The Euler angles in ZXY order, in radians.
     */
    private Vector3fc angle;

    /**
     * The corresponding quaternion representing the rotation.
     */
    private Quaternionfc quaternion;

    /**
     * Creates a rotation view from ZXY Euler angles (in radians).
     * @param angle Euler angles in ZXY order
     */
    public ZXYRotationView(Vector3fc angle) {
        this.angle = angle;
    }

    /**
     * Creates a rotation view from a quaternion.
     * @param quaternion the quaternion representing the rotation
     */
    public ZXYRotationView(Quaternionfc quaternion) {
        this.quaternion = quaternion;
    }

    /**
     * Returns the rotation represented as Euler angles in ZXY order.
     * Converts from quaternion if necessary (lazy evaluation).
     * @return the ZXY Euler angles (in radians)
     */
    @Override
    public Vector3fc asEulerAngle() {
        if (angle == null) {
            Vector3f angle = new Vector3f();
            float x = quaternion.x();
            float y = quaternion.y();
            float z = quaternion.z();
            float w = quaternion.w();
            angle.x = org.joml.Math.safeAsin(2.0f * (w * x + y * z));

            // Handle gimbal lock: when pitch ≈ ±90°
            if (angle.x > Math.PI / 2d - 1e-3) {
                angle.y = -2.0f * (float) Math.atan2(y, w);
                angle.z = 0.0f;
            } else if (angle.x < -(Math.PI / 2d - 1e-3)) {
                angle.y = 2.0f * (float) Math.atan2(y, w);
                angle.z = 0.0f;
            } else {
                angle.y = (float) Math.atan2(w * y - x * z, 0.5f - y * y - x * x);
                angle.z = (float) Math.atan2(w * z - x * y, 0.5f - z * z - x * x);
            }

            this.angle = angle;
        }
        return angle;
    }

    /**
     * Returns the rotation represented as a quaternion.
     * Converts from Euler angles if necessary (lazy evaluation).
     * @return the quaternion representing the rotation
     */
    @Override
    public Quaternionfc asQuaternion() {
        if (quaternion == null) {
            quaternion = new Quaternionf().rotateZ(angle.z()).rotateX(angle.x()).rotateY(angle.y());
        }
        return quaternion;
    }

    @Override
    public String toString() {
        if (angle != null) {
            return "ZXYRotationView{(radians)" + angle + '}';
        }
        if (quaternion != null) {
            return "ZXYRotationView{(radians)=" + asEulerAngle() + '}';
        }
        return null;
    }
}
