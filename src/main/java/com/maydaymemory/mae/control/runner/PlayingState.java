package com.maydaymemory.mae.control.runner;

import com.maydaymemory.mae.util.LongSupplier;
import it.unimi.dsi.fastutil.longs.LongLongImmutablePair;

import java.util.function.Supplier;

public class PlayingState implements IAnimationState {
    private final LongSupplier currentNanosSupplier;
    private final Supplier<IAnimationState> finishingStateSupplier;
    private float speed = 1f;

    public PlayingState(LongSupplier currentNanosSupplier, Supplier<IAnimationState> finishingStateSupplier) {
        this.currentNanosSupplier = currentNanosSupplier;
        this.finishingStateSupplier = finishingStateSupplier;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public IAnimationState update(IAnimationContext ctx) {
        long currentNanos = currentNanosSupplier.getAsLong();
        long alpha = (long) ((currentNanos - ctx.getLastUpdateTime()) * speed);
        long progress = ctx.getProgress() + alpha;
        long maxProgress = ctx.getMaxProgress();
        boolean flag = false;

        if (progress > maxProgress) {
            progress = maxProgress;
            flag = true;
        } else if (progress < 0) {
            progress = 0;
            flag = true;
        }

        ctx.enqueueClipPlan(new LongLongImmutablePair(ctx.getProgress(), progress));
        ctx.setProgress(progress);
        ctx.setLastUpdateTime(currentNanos);

        if (flag) {
            return finishingStateSupplier.get();
        } else {
            return this;
        }
    }

    @Override
    public void onEnter(IAnimationContext ctx) {
        ctx.setLastUpdateTime(currentNanosSupplier.getAsLong());
    }

    @Override
    public boolean isEndPoint() {
        return false;
    }
}
