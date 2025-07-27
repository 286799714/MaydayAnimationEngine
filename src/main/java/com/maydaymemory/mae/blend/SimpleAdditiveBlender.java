package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.BoneTransformFactory;
import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.basic.PoseBuilder;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Supplier;

/**
 * A simple implementation of AdditiveBlender that combines two poses using addition for translation and rotation (Euler angles),
 * and multiplication for scale. Used for basic additive animation blending.
 */
public class SimpleAdditiveBlender implements AdditiveBlender{
    private final BoneTransformFactory boneTransformFactory;
    private final BiPoseCombiner combiner;

    public SimpleAdditiveBlender(BoneTransformFactory boneTransformFactory,
                                 Supplier<PoseBuilder> poseBuilderSupplier) {
        this.boneTransformFactory = boneTransformFactory;
        this.combiner = new BiPoseCombiner(poseBuilderSupplier);
    }

    @Override
    public Pose blend(Pose pose1, Pose pose2) {
        return combiner.combine(pose1, pose2, (transform1, transform2) -> {
            if (transform1.boneIndex() == -1) { // means it is identity transform
                return transform2;
            }
            if (transform2.boneIndex() == -1) {
                return transform1;
            }
            Vector3fc newTranslation = transform1.translation().add(transform2.translation(), new Vector3f());
            Vector3fc newRotation = transform1.rotation().asEulerAngle().add(transform2.rotation().asEulerAngle(), new Vector3f());
            Vector3fc newScale = transform1.scale().mul(transform2.scale(), new Vector3f());
            return boneTransformFactory.createBoneTransform(transform1.boneIndex(), newTranslation, newRotation, newScale);
        });
    }
}
