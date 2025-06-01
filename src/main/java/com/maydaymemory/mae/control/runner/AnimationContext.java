package com.maydaymemory.mae.control.runner;

import com.maydaymemory.mae.basic.Animation;
import com.maydaymemory.mae.util.MathUtil;
import it.unimi.dsi.fastutil.longs.LongLongImmutablePair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.Queue;

public class AnimationContext implements IAnimationContext {
    private long progress;
    private long lastUpdateTime;
    private final long maxProgress;
    private IAnimationState state;
    private final Queue<LongLongImmutablePair> clipPlanQueue = new LinkedList<>();

    public AnimationContext(IAnimationState state, long maxProgress) {
        this.state = state;
        this.maxProgress = maxProgress;
        state.onEnter(this);
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
    public void setState(@Nonnull IAnimationState state) {
        if (this.state != state) {
            this.state = state;
            state.onEnter(this);
        }
    }

    @Override
    public boolean isEnd() {
        return state.isEndPoint();
    }

    @Override
    public void update() {
        clipPlanQueue.clear();
        IAnimationState state = this.state.update(this);
        if (this.state != state) {
            this.state = state;
            state.onEnter(this);
        }
    }

    public static AnimationContext createWithAnimation(Animation animation, IAnimationState initialState) {
        return new AnimationContext(initialState, MathUtil.toNanos(animation.getEndTimeS()));
    }
}
