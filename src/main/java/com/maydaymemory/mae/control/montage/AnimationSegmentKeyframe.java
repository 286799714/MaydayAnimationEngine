package com.maydaymemory.mae.control.montage;

import com.maydaymemory.mae.basic.BaseKeyframe;

/**
 * Animation segment keyframe, used for positioning animation segments on animation montage tracks.
 * 
 * @author MaydayMemory
 * @since 1.0.4
 */
public class AnimationSegmentKeyframe extends BaseKeyframe<AnimationSegment> {
    /** Animation segment instance */
    private final AnimationSegment segment;

    /**
     * Constructs an animation segment keyframe with the specified time.
     *
     * @param timeS Time when the animation segment starts on the montage track
     * @param segment Animation segment
     */
    public AnimationSegmentKeyframe(float timeS, AnimationSegment segment) {
        super(timeS);
        this.segment = segment;
    }

    /**
     * Get the keyframe value (animation segment).
     * 
     * @return Animation segment
     */
    @Override
    public AnimationSegment getValue() {
        return segment;
    }
}
