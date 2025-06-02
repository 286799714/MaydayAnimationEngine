package com.maydaymemory.mae.control.runner;

import it.unimi.dsi.fastutil.longs.LongLongImmutablePair;

import javax.annotation.Nullable;

public interface IAnimationContext {
    long getProgress();
    void setProgress(long progress);
    long getLastUpdateTime();
    void setLastUpdateTime(long lastUpdateTime);
    long getMaxProgress();
    void enqueueClipPlan(LongLongImmutablePair plan);
    @Nullable LongLongImmutablePair pollClipPlan();
    void setState(IAnimationState state);
    boolean isEnd();
    void update();
}
