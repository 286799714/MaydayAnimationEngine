package com.maydaymemory.mae.basic;

import org.joml.Quaternionfc;
import org.joml.Vector3fc;

public class ZXYBoneTransformFactory implements BoneTransformFactory{
    @Override
    public BoneTransform createBoneTransform(int boneIndex, Vector3fc translation, Vector3fc rotation, Vector3fc scale) {
        return new BoneTransform(boneIndex, translation, new ZXYRotationView(rotation), scale);
    }

    @Override
    public BoneTransform createBoneTransform(int boneIndex, Vector3fc translation, Quaternionfc rotation, Vector3fc scale) {
        return new BoneTransform(boneIndex, translation, new ZXYRotationView(rotation), scale);
    }
}
