package com.maydaymemory.mae.control.runner;

import com.maydaymemory.mae.util.LongSupplier;
import it.unimi.dsi.fastutil.longs.LongLongImmutablePair;

import java.util.function.Supplier;

/**
 * Animation state that represents playing animation with forward progression.
 * 
 * <p>This state implements both forward and backward animation playback, advancing the animation
 * progress based on elapsed time and a configurable speed multiplier. When the
 * animation reaches its boundaries (0 or max progress), it transitions to a
 * finishing state specified by the supplier.</p>
 *
 * @author MaydayMemory
 * @since 1.0
 */
public class PlayingState implements IAnimationState {
    /** Supplier for current time in nanoseconds */
    private final LongSupplier currentNanosSupplier;
    
    /** Supplier for the state to transition to when animation finishes */
    private final Supplier<IAnimationState> finishingStateSupplier;
    
    /** Speed multiplier for animation playback (1.0 = normal speed) */
    private float speed = 1f;

    /**
     * Constructs a new PlayingState with the specified time supplier and finishing state supplier.
     * 
     * @param currentNanosSupplier supplier for current time in nanoseconds
     * @param finishingStateSupplier supplier for the state to transition to when animation finishes
     */
    public PlayingState(LongSupplier currentNanosSupplier, Supplier<IAnimationState> finishingStateSupplier) {
        this.currentNanosSupplier = currentNanosSupplier;
        this.finishingStateSupplier = finishingStateSupplier;
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
     * @param speed the speed multiplier (1.0 = normal speed), 
     * setting it to a negative number will reverse the animation
     */
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

        if (progress >= maxProgress) {
            progress = maxProgress;
            flag = true;
        } else if (progress <= 0) {
            progress = 0;
            flag = true;
        }

        ctx.enqueueClipPlan(new LongLongImmutablePair(ctx.getProgress(), progress == maxProgress ? Long.MAX_VALUE : progress));
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
