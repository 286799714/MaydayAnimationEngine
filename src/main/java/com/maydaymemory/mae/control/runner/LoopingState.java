package com.maydaymemory.mae.control.runner;

import com.maydaymemory.mae.util.LongSupplier;
import it.unimi.dsi.fastutil.longs.LongLongImmutablePair;

/**
 * Animation state that represents looping animation playback.
 * 
 * <p>This state implements continuous animation looping, automatically wrapping
 * the progress around when it reaches the animation boundaries. The state
 * handles both forward and backward looping, maintaining proper clip plan
 * generation for seamless loop transitions.</p>
 * 
 * <p>The looping state is designed for animations that should play continuously,
 * such as idle animations, walking cycles, or other repetitive motion patterns.</p>
 * 
 * @author MaydayMemory
 * @since 1.0.1
 */
public class LoopingState implements IAnimationState {
    /** Supplier for current time in nanoseconds */
    private final LongSupplier currentNanosSupplier;
    
    /** Speed multiplier for animation playback (1.0 = normal speed) */
    private float speed = 1f;

    /**
     * Constructs a new LoopingState with the specified time supplier.
     * 
     * @param currentNanosSupplier supplier for current time in nanoseconds
     */
    public LoopingState(final LongSupplier currentNanosSupplier) {
        this.currentNanosSupplier = currentNanosSupplier;
    }

    /**
     * Gets the current playback speed.
     * 
     * @return the speed multiplier (1.0 = normal speed)
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Sets the playback speed.
     * 
     * @param speed the speed multiplier (1.0 = normal speed)
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    @Override
    public IAnimationState update(IAnimationContext ctx) {
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

    @Override
    public void onEnter(IAnimationContext ctx) {
        ctx.setLastUpdateTime(currentNanosSupplier.getAsLong());
    }

    @Override
    public boolean isEndPoint() {
        return false;
    }
}
