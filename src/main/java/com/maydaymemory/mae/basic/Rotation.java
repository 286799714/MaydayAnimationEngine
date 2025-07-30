package com.maydaymemory.mae.basic;

import org.joml.Quaternionfc;
import org.joml.Vector3fc;

/**
 * Represents a rotation, which can be in quaternion form or Euler angle form.
 * This is specifically used in animation, not in poses.
 *
 * <p>
 * <b>Note:</b> Unless you know exactly what you are doing,it is recommended that all Rotation Keyframes
 * in the same animation channel are either all Euler Angles or all Quaternion Rotations.
 * </p>
 *
 * @see RotationKeyframe for the keyframe representation of a rotation
 */
public class Rotation {
    private final Vector3fc eulerAngles;
    private final Quaternionfc quaternion;

    /**
     * Construct a rotation from Euler angles.
     *
     * @param eulerAngles the angles
     */
    public Rotation(Vector3fc eulerAngles) {
        this.eulerAngles = eulerAngles;
        this.quaternion = null;
    }

    /**
     * Construct a rotation from a quaternion.
     *
     * @param quaternion the quaternion
     */
    public Rotation(Quaternionfc quaternion) {
        this.eulerAngles = null;
        this.quaternion = quaternion;
    }

    /**
     * Get the euler angles.
     *
     * @return the euler angles
     */
    public Vector3fc getEulerAngles() {
        return eulerAngles;
    }

    /**
     * Get the quaternion.
     *
     * @return the quaternion
     */
    public Quaternionfc getQuaternion() {
        return quaternion;
    }

    /**
     * Check if this rotation is an euler angles rotation.
     *
     * @return true if this rotation is an euler angles rotation, false otherwise
     */
    public boolean isEulerAngles() {
        return eulerAngles != null;
    }

    /**
     * Check if this rotation is a quaternion.
     *
     * @return true if this rotation is a quaternion,
     */
    public boolean isQuaternion() {
        return quaternion != null;
    }
}
