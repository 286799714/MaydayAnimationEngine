package com.maydaymemory.mae.basic;

import org.joml.Quaternionfc;
import org.joml.Vector3fc;

public class XYZBoneTransformFactory implements BoneTransformFactory {
    @Override
    public BoneTransform createBoneTransform(int boneIndex, Vector3fc translation, Vector3fc rotation, Vector3fc scale) {
        return new BoneTransform(boneIndex, translation, new XYZRotationView(rotation), scale);
    }

    @Override
    public BoneTransform createBoneTransform(int boneIndex, Vector3fc translation, Quaternionfc rotation, Vector3fc scale) {
        return new BoneTransform(boneIndex, translation, new XYZRotationView(rotation), scale);
    }
}
