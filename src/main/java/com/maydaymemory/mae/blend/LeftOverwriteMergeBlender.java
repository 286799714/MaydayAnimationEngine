package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.basic.PoseBuilder;

import java.util.function.Supplier;

/**
 * Concrete implementation of OverwriteMergeBlender.
 *
 * <p>
 * When the bone index is the same, transform from the {@code base} pose will be preserved.
 * </p>
 */
public class LeftOverwriteMergeBlender implements OverwriteMergeBlender{
    private final BiPoseCombiner combiner;

    public LeftOverwriteMergeBlender(Supplier<PoseBuilder> poseBuilderSupplier) {
        this.combiner = new BiPoseCombiner(poseBuilderSupplier);
    }

    @Override
    public Pose blend(Pose base, Pose input) {
        return combiner.combine(base, input, (transform1, transform2) -> {
            if (transform1.boneIndex() != -1) {
                return transform1;
            } else {
                return transform2;
            }
        });
    }
}
