package com.maydaymemory.mae.basic;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * An interface for representing a 3D rotation that can be viewed
 * as either Euler angles or a unit quaternion.
 * <p>
 * This abstraction allows consumers to work with either representation
 * interchangeably, depending on use case.
 * <p>
 * The specific Euler angle rotation order (e.g., ZYX, XYZ) is not defined by this interface
 * and is left to the discretion of the implementing class.
 */
public interface RotationView {
    /**
     * Returns the rotation represented as Euler angles.
     * <p>
     * The rotation order and axis conventions (e.g., XYZ, ZYX) are implementation-defined.
     * The returned angles are in radians.
     *
     * @return a {@link Vector3fc} containing Euler angles in a specific order defined by the implementation
     */
    Vector3fc asEulerAngle();

    /**
     * Returns the rotation represented as a unit quaternion.
     *
     * @return a {@link Quaternionfc} representing the same rotation
     */
    Quaternionfc asQuaternion();

    RotationView IDENTITY = new RotationView() {
        private final Vector3f angles = new Vector3f();
        private final Quaternionf quaternion = new Quaternionf();

        @Override
        public Vector3fc asEulerAngle() {
            return angles;
        }

        @Override
        public Quaternionfc asQuaternion() {
            return quaternion;
        }
    };
}
