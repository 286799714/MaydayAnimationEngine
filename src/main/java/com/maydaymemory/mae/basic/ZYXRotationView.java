package com.maydaymemory.mae.basic;

import com.maydaymemory.mae.util.MathUtil;
import org.joml.*;

/**
 * A simple rotation view class that supports ZYX Tait–Bryan angles.
 * <p>
 * It lazily converts between the two representations as needed.
 */
public class ZYXRotationView implements RotationView {
    /**
     * The Euler angles in ZYX order, in radians.
     */
    private Vector3fc angle;

    /**
     * The corresponding quaternion representing the rotation.
     */
    private Quaternionfc quaternion;

    /**
     * Creates a rotation view from ZYX Euler angles (in radians).
     *
     * @param angle Euler angles in ZYX order
     */
    public ZYXRotationView(Vector3fc angle) {
        this.angle = angle;
    }

    /**
     * Creates a rotation view from a quaternion.
     *
     * @param quaternion the quaternion representing the rotation
     */
    public ZYXRotationView(Quaternionfc quaternion) {
        this.quaternion = quaternion;
    }

    /**
     * Returns the rotation represented as Euler angles in ZYX order.
     * Converts from quaternion if necessary (lazy evaluation).
     *
     * @return the ZYX Euler angles (in radians)
     */
    @Override
    public Vector3fc asEulerAngle() {
        if (angle == null) {
            Matrix3f matrix = new Matrix3f().set(quaternion);
            this.angle = MathUtil.extractEulerAnglesZYX(matrix);
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
            quaternion = new Quaternionf().rotateZYX(angle.z(), angle.y(), angle.x()); // Quaternion mul order is reversed
        }
        return quaternion;
    }

    @Override
    public String toString() {
        if (angle != null) {
            return "ZYXRotationView{(radians)" + angle + '}';
        }
        if (quaternion != null) {
            return "ZYXRotationView{(radians)=" + asEulerAngle() + '}';
        }
        return null;
    }
}
