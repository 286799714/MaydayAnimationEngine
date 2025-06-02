package com.maydaymemory.mae.control.runner;

import it.unimi.dsi.fastutil.longs.LongLongImmutablePair;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.Queue;

public class AnimationContext implements IAnimationContext {
    private long progress;
    private long lastUpdateTime;
    private final long maxProgress;
    private IAnimationState state;
    private final Queue<LongLongImmutablePair> clipPlanQueue = new LinkedList<>();

    public AnimationContext(long maxProgress) {
        this.maxProgress = maxProgress;
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
    public void enqueueClipPlan(LongLongImmutablePair plan) {
        clipPlanQueue.offer(plan);
    }

    @Nullable
    @Override
    public LongLongImmutablePair pollClipPlan() {
        return clipPlanQueue.poll();
    }

    @Override
    public void setState(IAnimationState state) {
        if (this.state != state) {
            this.state = state;
            if (state != null) {
                state.onEnter(this);
            }
        }
    }

    @Override
    public boolean isEnd() {
        if (state == null) {
            return true;
        }
        return state.isEndPoint();
    }

    @Override
    public void update() {
        clipPlanQueue.clear();
        if (state != null) {
            setState(state.update(this));
        }
    }
}
