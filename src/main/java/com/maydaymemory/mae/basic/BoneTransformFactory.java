package com.maydaymemory.mae.basic;

import org.joml.Quaternionfc;
import org.joml.Vector3fc;

/**
 * An interface for factories that create BoneTransform instances.
 * Implementations may use different conventions for representing rotation.
 */
public interface BoneTransformFactory {
    /**
     * Creates a BoneTransform using Euler angles for rotation.
     *
     * @param boneIndex the index of the bone
     * @param translation the translation vector
     * @param rotation the rotation vector (Euler angles)
     * @param scale the scale vector
     * @return a new BoneTransform instance
     */
    BoneTransform createBoneTransform(int boneIndex,
                                      Vector3fc translation,
                                      Vector3fc rotation,
                                      Vector3fc scale);

    /**
     * Creates a BoneTransform using a quaternion for rotation.
     *
     * @param boneIndex the index of the bone
     * @param translation the translation vector
     * @param rotation the rotation quaternion
     * @param scale the scale vector
     * @return a new BoneTransform instance
     */
    BoneTransform createBoneTransform(int boneIndex,
                                      Vector3fc translation,
                                      Quaternionfc rotation,
                                      Vector3fc scale);
}
