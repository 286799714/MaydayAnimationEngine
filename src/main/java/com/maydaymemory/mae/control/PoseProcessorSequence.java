package com.maydaymemory.mae.control;

import com.maydaymemory.mae.basic.DummyPose;
import com.maydaymemory.mae.basic.Pose;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface PoseProcessorSequence {
    void setProcessor(int i, PoseProcessor processor);
    @Nullable Pose getPose(int i);
    static @Nonnull Pose getPoseSafe(int i, PoseProcessorSequence sequence) {
        Pose pose;
        return i < 0
                ? DummyPose.INSTANCE
                : (pose = sequence.getPose(i)) == null
                ? DummyPose.INSTANCE
                : pose;
    }
}
