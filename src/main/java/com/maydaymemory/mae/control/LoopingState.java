package com.maydaymemory.mae.control;

import com.maydaymemory.mae.util.LongSupplier;
import it.unimi.dsi.fastutil.longs.LongLongImmutablePair;

public class LoopingState implements AnimationState{
    private final LongSupplier currentNanosSupplier;
    private float speed = 1f;

    public LoopingState(final LongSupplier currentNanosSupplier) {
        this.currentNanosSupplier = currentNanosSupplier;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public AnimationState update(IAnimationContext ctx) {
        long currentNanos = currentNanosSupplier.getAsLong();
        long maxProgress = ctx.getMaxProgress();
        if (maxProgress == 0) {
            ctx.setProgress(0);
            ctx.setLastUpdateTime(currentNanos);
            return this;
        }
        long progress = ctx.getProgress() + (long) ((currentNanos - ctx.getLastUpdateTime()) * speed);
        if (progress >= maxProgress) {
            progress = progress % maxProgress;
            long times = progress / maxProgress;
            times--;
            ctx.enqueueClipPlan(new LongLongImmutablePair(ctx.getProgress(), maxProgress));
            for (int i = 0; i < times; i++) {
                ctx.enqueueClipPlan(new LongLongImmutablePair(0, maxProgress));
            }
            ctx.enqueueClipPlan(new LongLongImmutablePair(0, progress));
        } else if (progress < 0) {
            progress = progress % maxProgress + maxProgress;
            long times = progress / maxProgress;
            times = -times;
            ctx.enqueueClipPlan(new LongLongImmutablePair(ctx.getProgress(), 0));
            for (int i = 0; i < times; i++) {
                ctx.enqueueClipPlan(new LongLongImmutablePair(maxProgress, 0));
            }
            ctx.enqueueClipPlan(new LongLongImmutablePair(maxProgress, progress));
        } else {
            ctx.enqueueClipPlan(new LongLongImmutablePair(ctx.getProgress(), progress));
        }
        ctx.setProgress(progress);
        ctx.setLastUpdateTime(currentNanos);
        return this;
    }
}
