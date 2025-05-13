package com.maydaymemory.mae.basic;

import java.util.LinkedList;

public class LinkedListPose implements Pose{
    private final LinkedList<BoneTransform> list;

    public LinkedListPose(LinkedList<BoneTransform> list) {
        this.list = list;
    }

    /**
     * Appends a {@link BoneTransform} to the end of the list.
     * <p>
     * The given transform's bone index must be strictly greater than the last bone index
     * currently in the list to maintain ascending order.
     *
     * @param transform the bone transform to append
     * @throws IllegalArgumentException if the bone index is not strictly greater than the last one
     */
    public void pushBack(BoneTransform transform) {
        if (!list.isEmpty() && list.getLast().boneIndex() >= transform.boneIndex()) {
            throw new IllegalArgumentException("Bone index must be in strictly ascending order");
        }
        list.addLast(transform);
    }

    /**
     * Inserts a {@link BoneTransform} at the beginning of the list.
     * <p>
     * The given transform's bone index must be strictly less than the first bone index
     * currently in the list to maintain ascending order.
     *
     * @param transform the bone transform to insert at the front
     * @throws IllegalArgumentException if the bone index is not strictly less than the first one
     */
    public void pushFront(BoneTransform transform) {
        if (!list.isEmpty() && list.getFirst().boneIndex() <= transform.boneIndex()) {
            throw new IllegalArgumentException("Bone index must be in strictly ascending order");
        }
        list.addFirst(transform);
    }

    @Override
    public Iterable<BoneTransform> getBoneTransforms() {
        return list;
    }
}
