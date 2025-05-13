package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.BoneTransformFactory;
import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.basic.PoseBuilder;

import java.util.function.Supplier;

public class SimpleLayeredBlender implements LayeredBlender{
    private final BiPoseCombiner combiner;
    private final BoneTransformFactory transformFactory;

    public SimpleLayeredBlender(BoneTransformFactory transformFactory,
                                Supplier<PoseBuilder> poseBuilderSupplier) {
        this.transformFactory = transformFactory;
        this.combiner = new BiPoseCombiner(poseBuilderSupplier);
    }

    @Override
    public Pose blend(Pose basePose, Pose inputPose, LayerBlend layer) {
        return combiner.combine(basePose, inputPose, (t1, t2) -> {
            int boneIndex = Math.max(t1.boneIndex(), t2.boneIndex()); // identity transform's bone index is -1
            float weight = layer.getWeight(boneIndex);
            return SimpleInterpolatorBlender.leanerLerpTransforms(t1, t2, weight, boneIndex, transformFactory);
        });
    }
}
