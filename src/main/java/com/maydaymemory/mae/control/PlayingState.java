package com.maydaymemory.mae.control;

import com.maydaymemory.mae.util.LongSupplier;
import it.unimi.dsi.fastutil.longs.LongLongImmutablePair;

public class PlayingState implements AnimationState{
    private final LongSupplier currentNanosSupplier;
    private float speed = 1f;

    public PlayingState(final LongSupplier currentNanosSupplier) {
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
            return new PauseState(currentNanosSupplier);
        } else {
            return this;
        }
    }
}
