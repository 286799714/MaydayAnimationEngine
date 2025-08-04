package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.basic.PoseBuilder;

import java.util.function.Supplier;

/**
 * Concrete implementation of OverwriteMergeBlender.
 *
 * <p>
 * When the bone index is the same, transform from the {@code input} pose will be preserved.
 * </p>
 */
public class RightOverwriteMergeBlender implements OverwriteMergeBlender{
    private final BiPoseCombiner combiner;

    public RightOverwriteMergeBlender(Supplier<PoseBuilder> poseBuilderSupplier) {
        this.combiner = new BiPoseCombiner(poseBuilderSupplier);
    }

    @Override
    public Pose blend(Pose base, Pose input) {
        return combiner.combine(base, input, (transform1, transform2) -> {
            if (transform2.boneIndex() != -1) {
                return transform2;
            } else {
                return transform1;
            }
        });
    }
}
