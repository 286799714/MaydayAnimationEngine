package com.maydaymemory.mae.basic;

import org.joml.Quaternionfc;
import org.joml.Vector3fc;

/**
 * A factory for creating BoneTransform instances using the ZYX rotation convention.
 */
public class ZYXBoneTransformFactory implements BoneTransformFactory {
    /**
     * Creates a BoneTransform using Euler angles for rotation (ZYX order).
     *
     * @param boneIndex the index of the bone
     * @param translation the translation vector
     * @param rotation the rotation vector (Euler angles)
     * @param scale the scale vector
     * @return a new BoneTransform instance
     */
    @Override
    public BoneTransform createBoneTransform(int boneIndex, Vector3fc translation, Vector3fc rotation, Vector3fc scale) {
        return new BoneTransform(boneIndex, translation, new ZYXRotationView(rotation), scale);
    }

    /**
     * Creates a BoneTransform using a quaternion for rotation.
     *
     * @param boneIndex the index of the bone
     * @param translation the translation vector
     * @param rotation the rotation quaternion
     * @param scale the scale vector
     * @return a new BoneTransform instance
     */
    @Override
    public BoneTransform createBoneTransform(int boneIndex, Vector3fc translation, Quaternionfc rotation, Vector3fc scale) {
        return new BoneTransform(boneIndex, translation, new ZYXRotationView(rotation), scale);
    }
}
