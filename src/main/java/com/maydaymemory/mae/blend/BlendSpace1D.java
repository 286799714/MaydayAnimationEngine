package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.Pose;

public interface BlendSpace1D {
    void setSamplerPose(int index, Pose pose);

    Pose blend(float position);
}
