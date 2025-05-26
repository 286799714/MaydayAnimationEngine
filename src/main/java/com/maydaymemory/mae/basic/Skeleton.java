package com.maydaymemory.mae.basic;

import java.util.Collection;

public interface Skeleton {
    Collection<Integer> getChildren(int i);

    int getFather(int i);

    void applyPose(Pose pose);

    void applyAdditivePose(Pose pose);
}
