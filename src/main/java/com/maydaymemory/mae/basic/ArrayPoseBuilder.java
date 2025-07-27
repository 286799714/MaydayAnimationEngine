package com.maydaymemory.mae.basic;

import java.util.ArrayList;

/**
 * A builder implementation for creating ArrayPose instances.
 * This builder enforces strictly ascending bone indices and is single-use.
 */
public class ArrayPoseBuilder implements PoseBuilder {
    private ArrayList<BoneTransform> list;

    /**
     * Constructs a new ArrayPoseBuilder with default capacity.
     */
    public ArrayPoseBuilder() {
        list = new ArrayList<>();
    }

    /**
     * Constructs a new ArrayPoseBuilder with the specified initial capacity.
     *
     * @param initialCapacity the initial capacity of the builder
     */
    public ArrayPoseBuilder(int initialCapacity) {
        list = new ArrayList<>(initialCapacity);
    }

    @Override
    public void addBoneTransform(BoneTransform boneTransform) {
        if (list == null) {
            throw new IllegalStateException("Cannot add BoneTransform: the builder has already been finalized via toPose().");
        }
        if (!list.isEmpty()) {
            // boneIndex must be in strictly ascending order
            if (list.get(list.size() - 1).boneIndex() >= boneTransform.boneIndex()) {
                throw new IllegalArgumentException("Bone index must be in strictly ascending order");
            }
        }
        list.add(boneTransform);
    }

    @Override
    public Pose toPose() {
        if (list != null) {
            ArrayPose pose = new ArrayPose(list);
            list = null;
            return pose;
        }
        throw new IllegalStateException("ArrayPoseBuilder is single-use. Please create a new builder instance.");
    }
}
