package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.Pose;

public interface BlendSpace2D {
    void setSamplerPose(int index, Pose pose);

    Pose blend(float x, float y);
}
