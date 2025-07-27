package com.maydaymemory.mae.control.runner;

import it.unimi.dsi.fastutil.longs.LongLongImmutablePair;

import javax.annotation.Nullable;

import com.maydaymemory.mae.util.MathUtil;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Implementation of {@link IAnimationContext} that manages animation execution state.
 * 
 * <p>This class provides the concrete implementation of the animation context,
 * maintaining the current progress, timing information, animation state, and
 * clip plan queue. It serves as the central coordination point for animation
 * execution.</p>
 * 
 * @author MaydayMemory
 * @since 1.0.1
 */
public class AnimationContext implements IAnimationContext {
    /** Current animation progress in nanoseconds */
    private long progress;
    
    /** Timestamp of the last update in nanoseconds */
    private long lastUpdateTime;
    
    /** Maximum animation progress in nanoseconds */
    private final long maxProgress;
    
    /** Current animation state */
    private IAnimationState state;
    
    /** Queue for storing clip plans (time range pairs) */
    private final Queue<LongLongImmutablePair> clipPlanQueue = new LinkedList<>();

    /**
     * Constructs a new AnimationContext with the specified maximum progress.
     * 
     * @param maxProgress the maximum animation progress in nanoseconds
     */
    public AnimationContext(long maxProgress) {
        this.maxProgress = maxProgress;
    }

    /**
     * Constructs a new AnimationContext with the specified maximum progress.
     * 
     * @param maxProgressInSecond the maximum animation progress in second
     */
    public AnimationContext(float maxProgressInSecond) {
        this(MathUtil.toNanos(maxProgressInSecond));
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

    @Override
    public Iterator<? extends LongLongImmutablePair> clipPlanIterator() {
        return clipPlanQueue.iterator();
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
    @Nullable
    public IAnimationState getState() {
        return state;
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
