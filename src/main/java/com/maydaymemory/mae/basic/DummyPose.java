package com.maydaymemory.mae.basic;

import java.util.Collections;

/**
 * A singleton implementation of the Pose interface that represents an empty or dummy pose.
 */
public final class DummyPose implements Pose {
    /**
     * The singleton instance of DummyPose.
     */
    public static final DummyPose INSTANCE = new DummyPose();

    /**
     * Private constructor to prevent instantiation.
     */
    private DummyPose() {}

    /**
     * Returns an empty list of bone transforms, as this pose contains no data.
     *
     * @return an empty iterable of BoneTransform
     */
    @Override
    public Iterable<BoneTransform> getBoneTransforms() {
        return Collections.emptyList();
    }
}