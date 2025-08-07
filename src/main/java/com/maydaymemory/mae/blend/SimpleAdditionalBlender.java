package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.BoneTransformFactory;
import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.basic.PoseBuilder;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Supplier;

public class SimpleAdditionalBlender implements AdditionalBlender {
    private final BoneTransformFactory boneTransformFactory;
    private final BiPoseCombiner combiner;

    public SimpleAdditionalBlender(BoneTransformFactory boneTransformFactory,
                                 Supplier<PoseBuilder> poseBuilderSupplier) {
        this.boneTransformFactory = boneTransformFactory;
        this.combiner = new BiPoseCombiner(poseBuilderSupplier);
    }

    @Override
    public Pose blend(Pose basePose, Pose additionalPose) {
        return combiner.combine(basePose, additionalPose, (transform1, transform2) -> {
            if (transform1.boneIndex() == -1) { // means it is identity transform
                return transform2;
            }
            if (transform2.boneIndex() == -1) {
                return transform1;
            }
            Vector3fc newTranslation = transform1.translation().add(transform2.translation(), new Vector3f());
            Quaternionf newRotation = transform2.rotation().asQuaternion().mul(transform1.rotation().asQuaternion(), new Quaternionf());
            Vector3fc newScale = transform1.scale().mul(transform2.scale(), new Vector3f());
            return boneTransformFactory.createBoneTransform(transform1.boneIndex(), newTranslation, newRotation, newScale);
        });
    }
}
