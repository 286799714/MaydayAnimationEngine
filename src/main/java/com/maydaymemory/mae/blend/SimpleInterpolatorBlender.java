package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.BoneTransform;
import com.maydaymemory.mae.basic.BoneTransformFactory;
import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.basic.PoseBuilder;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.function.Supplier;

/**
 * Rotation interpolating is nlerp, scale is lerp, for higher performance.
 */
public class SimpleInterpolatorBlender implements InterpolatorBlender{
    private final BoneTransformFactory boneTransformFactory;
    private final BiPoseCombiner combiner;

    public SimpleInterpolatorBlender(BoneTransformFactory boneTransformFactory,
                                 Supplier<PoseBuilder> poseBuilderSupplier) {
        this.boneTransformFactory = boneTransformFactory;
        this.combiner = new BiPoseCombiner(poseBuilderSupplier);
    }

    @Override
    public Pose blend(Pose basePose, Pose inputPose, float weight) {
        return combiner.combine(basePose, inputPose, (baseTransform, inputTransform) -> {
            int boneIndex = Math.max(baseTransform.boneIndex(), inputTransform.boneIndex()); // identity transform's bone index is -1
            return leanerLerpTransforms(baseTransform, inputTransform, weight, boneIndex, boneTransformFactory);
        });
    }

    public static BoneTransform leanerLerpTransforms(BoneTransform baseTransform, BoneTransform inputTransform,
                                                     float weight, int boneIndex, BoneTransformFactory transformFactory) {
        Vector3fc newTranslation = baseTransform.translation().lerp(inputTransform.translation(), weight, new Vector3f());
        Quaternionfc newRotation = baseTransform.rotation().asQuaternion()
                .nlerp(inputTransform.rotation().asQuaternion(), weight, new Quaternionf());
        Vector3fc newScale = baseTransform.scale().lerp(inputTransform.scale(), weight, new Vector3f());
        return transformFactory.createBoneTransform(boneIndex, newTranslation, newRotation, newScale);
    }
}
