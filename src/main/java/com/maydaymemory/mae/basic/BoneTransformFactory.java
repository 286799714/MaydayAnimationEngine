package com.maydaymemory.mae.basic;

import org.joml.Quaternionfc;
import org.joml.Vector3fc;

public interface BoneTransformFactory {
    BoneTransform createBoneTransform(int boneIndex,
                                      Vector3fc translation,
                                      Vector3fc rotation,
                                      Vector3fc scale);

    BoneTransform createBoneTransform(int boneIndex,
                                      Vector3fc translation,
                                      Quaternionfc rotation,
                                      Vector3fc scale);
}
