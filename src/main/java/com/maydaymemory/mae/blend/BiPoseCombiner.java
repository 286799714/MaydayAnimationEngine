package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.BoneTransform;
import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.basic.PoseBuilder;

import java.util.Iterator;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class BiPoseCombiner {
    private final Supplier<PoseBuilder> poseBuilderSupplier;

    public BiPoseCombiner(Supplier<PoseBuilder> poseBuilderSupplier) {
        this.poseBuilderSupplier = poseBuilderSupplier;
    }

    public Pose combine(Pose pose1, Pose pose2,
                         BiFunction<BoneTransform, BoneTransform, BoneTransform> combiner) {
        PoseBuilder poseBuilder = poseBuilderSupplier.get();

        Iterator<BoneTransform> it1 = pose1.getBoneTransforms().iterator();
        Iterator<BoneTransform> it2 = pose2.getBoneTransforms().iterator();

        BoneTransform t1 = it1.hasNext() ? it1.next() : null;
        BoneTransform t2 = it2.hasNext() ? it2.next() : null;

        while (t1 != null || t2 != null) {
            if (t1 != null && t2 != null) {
                int cmp = t1.compareTo(t2);
                if (cmp == 0) {
                    poseBuilder.addBoneTransform(combiner.apply(t1, t2));
                    t1 = it1.hasNext() ? it1.next() : null;
                    t2 = it2.hasNext() ? it2.next() : null;
                } else if (cmp < 0) {
                    poseBuilder.addBoneTransform(combiner.apply(t1, BoneTransform.IDENTITY_TRANSFORM));
                    t1 = it1.hasNext() ? it1.next() : null;
                } else {
                    poseBuilder.addBoneTransform(combiner.apply(BoneTransform.IDENTITY_TRANSFORM, t2));
                    t2 = it2.hasNext() ? it2.next() : null;
                }
            } else if (t1 != null) {
                poseBuilder.addBoneTransform(combiner.apply(t1, BoneTransform.IDENTITY_TRANSFORM));
                t1 = it1.hasNext() ? it1.next() : null;
            } else {
                poseBuilder.addBoneTransform(combiner.apply(BoneTransform.IDENTITY_TRANSFORM, t2));
                t2 = it2.hasNext() ? it2.next() : null;
            }
        }

        return poseBuilder.toPose();
    }
}
