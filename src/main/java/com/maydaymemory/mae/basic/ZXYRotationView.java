package com.maydaymemory.mae.basic;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * A simple rotation view class that supports ZYX Tait–Bryan angles.
 * <p>
 * The reason for this RotationView is that the camera rotation of
 * Minecraft (and most game engines) uses ZXY Euler angles.
 * </p>
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
            Vector3f angle = quaternion.getEulerAnglesZXY(new Vector3f());
            // Solve the Gimbal Lock problem when converting quaternion to Euler angle
            // Detect pitch ≈ ±90° and fix roll = 0
            if (Math.abs(angle.x) > Math.PI / 2d - 1E-5d) {
                angle.y = 2.0f * (float) Math.atan2(quaternion.y(), quaternion.w());
                angle.z = 0.0f;
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
