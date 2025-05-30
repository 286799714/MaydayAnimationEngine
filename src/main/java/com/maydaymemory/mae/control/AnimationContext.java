package com.maydaymemory.mae.control;

import it.unimi.dsi.fastutil.longs.LongLongImmutablePair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.Queue;

public class AnimationContext implements IAnimationContext{
    private long progress;
    private long lastUpdateTime;
    private long maxProgress;
    private AnimationState state;
    private final Queue<LongLongImmutablePair> clipPlanQueue = new LinkedList<>();

    public AnimationContext(AnimationState state) {
        this.state = state;
    }

    @Override
    public long getProgress() {
        return progress;
    }

    @Override
    public void setProgress(long progress) {
        this.progress = progress;
    }

    @Override
    public long getLastUpdateTime() {
        return lastUpdateTime;
    }

    @Override
    public void setLastUpdateTime(long lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public long getMaxProgress() {
        return maxProgress;
    }

    @Override
    public void setMaxProgress(long maxProgress) {
        if (maxProgress < 0) {
            throw new IllegalArgumentException("maxProgress must be a positive number");
        }
        this.maxProgress = maxProgress;
    }

    @Override
    public void enqueueClipPlan(LongLongImmutablePair plan) {
        clipPlanQueue.offer(plan);
    }

    @Nullable
    @Override
    public LongLongImmutablePair pollClipPlan() {
        return clipPlanQueue.poll();
    }

    @Override
    public void setState(@Nonnull AnimationState state) {
        this.state = state;
    }

    @Override
    public void update() {
        clipPlanQueue.clear();
        state = state.update(this);
    }
}
