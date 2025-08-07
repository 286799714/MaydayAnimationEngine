package com.maydaymemory.mae.control.montage;

import com.maydaymemory.mae.basic.Animation;

/**
 * Animation segment, representing a segment extracted from an animation.
 *
 * @author MaydayMemory
 * @since 1.0.4
 */
public class AnimationSegment{
    /** Animation instance */
    private final Animation animation;
    
    /** Start time of the animation segment in the animation (seconds) */
    private final float startTime;
    
    /** End time of the animation segment in the animation (seconds) */
    private final float endTime;

    /**
     * Construct a new animation segment.
     * 
     * @param animation Animation instance
     * @param startTime Start time of the animation segment in the animation (seconds)
     * @param endTime End time of the animation segment in the animation (seconds)
     */
    public AnimationSegment(Animation animation, float startTime, float endTime) {
        this.animation = animation;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    /**
     * Get the animation instance.
     * 
     * @return Animation instance
     */
    public Animation getAnimation() {
        return animation;
    }

    /**
     * Get the start time of the animation segment in the animation.
     * 
     * @return Start time (seconds)
     */
    public float getStartTime() {
        return startTime;
    }

    /**
     * Get the end time of the animation segment in the animation.
     * 
     * @return End time (seconds)
     */
    public float getEndTime() {
        return endTime;
    }

    /**
     * Get the segment length.
     * 
     * @return Segment length (seconds)
     */
    public float getLength() {
        return endTime - startTime;
    }
}
