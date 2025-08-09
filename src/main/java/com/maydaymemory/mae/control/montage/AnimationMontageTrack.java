package com.maydaymemory.mae.control.montage;

import com.maydaymemory.mae.basic.ArrayAnimationChannelBase;
import com.maydaymemory.mae.basic.Keyframe;
import com.maydaymemory.mae.blend.DummyLayerBlend;
import com.maydaymemory.mae.blend.LayerBlend;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * Animation Montage Track, for managing an animation track in animation montage.
 * 
 * <p>Animation track is responsible for managing a series of animation segments.
 * Each track can independently control blending layers, whether enabled, and whether it's additive animation.</p>
 * 
 * @author MaydayMemory
 * @since 1.0.4
 */
public class AnimationMontageTrack extends ArrayAnimationChannelBase<Keyframe<AnimationSegment>> {
    /** Blending layer */
    private LayerBlend layer = DummyLayerBlend.ONE_WEIGHT_DUMMY;
    
    /** Whether it's an additive animation track */
    private boolean isAdditive = false;
    
    /** Whether the track is enabled */
    private boolean isEnabled = true;
    
    /**
     * Constructs a montage track with specified initial list,
     * this list will be wrapped (or, so called, enhanced), not copied, which means that the outside holding this list
     * can still access and change the list elements.
     *
     * <p>
     * <b>Important:</b> The program does not check whether the keyframes in the initial list are in the correct order
     * (in ascending time order). Please make sure you know the order of the elements in the list.
     * Otherwise you should pass an empty list to the constructor and add keyframes one by one,
     * then call {@link #refresh()} to sort them.
     * </p>
     *
     * @param initialList the initial list of keyframes
     */
    public AnimationMontageTrack(@Nonnull ArrayList<Keyframe<AnimationSegment>> initialList) {
        super(initialList);
    }

    /**
     * Constructs an empty montage track.
     */
    public AnimationMontageTrack() {
        super(new ArrayList<>());
    }

    /**
     * Get the end time of this track.
     * <p>
     * This method calculates the time point of the last animation segment plus its own length to get the final end time.
     * </p>
     * @return the end time (in seconds) of this track, or 0 if there is no animation segment in this track.
     */
    @Override
    public float getEndTimeS() {
        if (isEmpty()) {
            return 0;
        }
        Keyframe<AnimationSegment> keyframe = get(size() - 1);
        AnimationSegment segment = keyframe.getValue();
        return keyframe.getTimeS() + segment.getLength();
    }

    /**
     * Get animation segment keyframe at the specified time point.
     * 
     * <p>This method finds the animation segment playing at the specified time point, returns null if no segment is playing at that time.</p>
     * 
     * @param timeS Query time (seconds)
     * @return Animation segment keyframe at the specified time point, returns null if no segment is playing
     */
    @Nullable
    public Keyframe<AnimationSegment> getSegmentKeyframe(float timeS) {
        int index = findIndexBefore(timeS, false);
        if (index == -1) {
            return null;
        }
        Keyframe<AnimationSegment> keyframe = get(index);
        float timePassed = timeS - keyframe.getTimeS();
        if (timePassed < keyframe.getValue().getLength()) {
            return keyframe;
        } else {
            return null;
        }
    }

    /**
     * Get the track's blending layer.
     * 
     * @return Blending layer
     */
    public LayerBlend getLayer() {
        return layer;
    }

    /**
     * Set the track's blending layer.
     * 
     * @param layer Blending layer
     */
    public void setLayer(LayerBlend layer) {
        this.layer = layer;
    }

    /**
     * Check if the track is an additive animation track.
     * 
     * @return Returns true if it's an additive animation track, false otherwise
     */
    public boolean isAdditive() {
        return isAdditive;
    }

    /**
     * Set whether the track is an additive animation track.
     * 
     * @param additive Whether it's an additive animation track
     */
    public void setAdditive(boolean additive) {
        isAdditive = additive;
    }

    /**
     * Check if the track is enabled.
     * 
     * @return Returns true if the track is enabled, false otherwise
     */
    public boolean isEnabled() {
        return isEnabled;
    }

    /**
     * Set whether the track is enabled.
     * 
     * @param enabled Whether to enable the track
     */
    public void setEnabled(boolean enabled) {
        isEnabled = enabled;
    }
}
