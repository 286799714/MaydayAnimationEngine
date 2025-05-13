package com.maydaymemory.mae.basic;

import java.util.Collections;

public final class DummyPose implements Pose {
    public static final DummyPose INSTANCE = new DummyPose();

    private DummyPose() {}

    @Override
    public Iterable<BoneTransform> getBoneTransforms() {
        return Collections.emptyList();
    }
}