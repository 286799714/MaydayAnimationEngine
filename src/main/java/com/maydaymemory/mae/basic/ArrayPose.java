package com.maydaymemory.mae.basic;

import com.maydaymemory.mae.util.DirtyTrackingArrayList;

import java.util.ArrayList;
import java.util.Collections;

public class ArrayPose extends DirtyTrackingArrayList<BoneTransform> implements Pose{
    public ArrayPose(ArrayList<BoneTransform> boneTransforms) {
        super(boneTransforms);
    }

    @Override
    public Iterable<BoneTransform> getBoneTransforms() {
        return innerList;
    }

    @Override
    protected boolean onRefresh() {
        Collections.sort(innerList);
        return true;
    }
}
