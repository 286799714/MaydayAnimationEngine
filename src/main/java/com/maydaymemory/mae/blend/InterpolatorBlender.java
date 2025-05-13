package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.Pose;

public interface InterpolatorBlender {
    Pose blend(Pose basePose, Pose inputPose, float weight);
}
