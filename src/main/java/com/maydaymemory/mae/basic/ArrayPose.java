package com.maydaymemory.mae.basic;

import com.maydaymemory.mae.util.DirtyTrackingArrayList;

import java.util.ArrayList;
import java.util.Collections;

/**
 * An implementation of the Pose interface backed by an ArrayList of BoneTransform objects.
 * This class provides efficient access and sorting for bone transforms.
 */
public class ArrayPose extends DirtyTrackingArrayList<BoneTransform> implements Pose{
    /**
     * Constructs an ArrayPose with the given list(sorted) of bone transforms.
     * To avoid extra overhead, the constructor does not check whether the list is sorted,
     * the user must clearly know what he/she is doing.
     * 
     * @param boneTransforms the list(sorted) of bone transforms
     */
    public ArrayPose(ArrayList<BoneTransform> boneTransforms) {
        super(boneTransforms);
    }

    /**
     * Get an iterable of bone transforms in this pose.
     * The order is: bone index ascending
     *
     * @return an iterable of BoneTransform
     */
    @Override
    public Iterable<BoneTransform> getBoneTransforms() {
        return innerList;
    }

    /**
     * Sorts the internal list of bone transforms and clear the dirty flag.
     *
     * @return true after sorting
     */
    @Override
    protected boolean onRefresh() {
        Collections.sort(innerList);
        return true;
    }
}
