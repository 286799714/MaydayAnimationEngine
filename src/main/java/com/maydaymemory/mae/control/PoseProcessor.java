package com.maydaymemory.mae.control;

import com.maydaymemory.mae.basic.Pose;

public interface PoseProcessor {
    Pose process(PoseProcessorSequence sequence);
}
