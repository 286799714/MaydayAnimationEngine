package com.maydaymemory.mae.control;

import it.unimi.dsi.fastutil.longs.LongLongImmutablePair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface IAnimationContext {
    long getProgress();
    void setProgress(long progress);
    long getLastUpdateTime();
    void setLastUpdateTime(long lastUpdateTime);
    long getMaxProgress();
    void setMaxProgress(long maxProgress);
    void enqueueClipPlan(LongLongImmutablePair plan);
    @Nullable LongLongImmutablePair pollClipPlan();
    void setState(@Nonnull AnimationState state);
    void update();
}
