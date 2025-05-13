package com.maydaymemory.mae.basic;

import java.util.LinkedList;

public class LinkedListPoseBuilder implements PoseBuilder{
    private LinkedList<BoneTransform> list = new LinkedList<>();

    @Override
    public void addBoneTransform(BoneTransform boneTransform) {
        if (list == null) {
            throw new IllegalStateException("Cannot add BoneTransform: the builder has already been finalized via toPose().");
        }
        if (!list.isEmpty() && list.getLast().boneIndex() >= boneTransform.boneIndex()) {
            throw new IllegalArgumentException("Bone index must be in strictly ascending order");
        }
        list.addLast(boneTransform);
    }

    @Override
    public Pose toPose() {
        if (list != null) {
            LinkedListPose pose = new LinkedListPose(list);
            list = null;
            return pose;
        }
        throw new IllegalStateException("ArrayPoseBuilder is single-use. Please create a new builder instance.");
    }
}
