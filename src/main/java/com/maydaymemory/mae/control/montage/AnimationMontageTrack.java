package com.maydaymemory.mae.control.montage;

import com.maydaymemory.mae.basic.ArrayAnimationChannelBase;
import com.maydaymemory.mae.basic.Keyframe;
import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.blend.DummyLayerBlend;
import com.maydaymemory.mae.blend.LayerBlend;
import com.maydaymemory.mae.util.Iterables;

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
     * Evaluate the track at the specified progress time.
     * If there is no segment playing at the specified time point, returns null.
     *
     * @param progressTimeS the progress time (seconds)
     * @return evaluated pose, or null if there is no segment playing at the specified time point
     */
    @Nullable
    public Pose evaluate(float progressTimeS) {
        Keyframe<AnimationSegment> segmentKeyframe = this.getSegmentKeyframe(progressTimeS);
        if (segmentKeyframe == null) {
            return null;
        }
        float localProgress = progressTimeS - segmentKeyframe.getTimeS();
        AnimationSegment segment = segmentKeyframe.getValue();
        return segment.getAnimation().evaluate(segment.getStartTime() + localProgress);
    }

    /**
     * Evaluate the track's curve at the specified progress time.
     *
     * <p><b>Important: </b>Type safety is not checked internally.
     * Please ensure that the curve type specified is the same when setting and getting.</p>
     *
     * @param curveName the name of the curve to evaluate
     * @param progressTimeS the progress time (seconds)
     * @return evaluated curve value, or null if the curve is not found or there is no segment at the specified time point
     * @param <T> the type of the curve value
     */
    @Nullable
    public <T> T evaluateCurve(String curveName, float progressTimeS) {
        Keyframe<AnimationSegment> segmentKeyframe = this.getSegmentKeyframe(progressTimeS);
        if (segmentKeyframe == null) {
            return null;
        }
        float localProgress = progressTimeS - segmentKeyframe.getTimeS();
        AnimationSegment segment = segmentKeyframe.getValue();
        return segment.getAnimation().evaluateCurve(curveName, segment.getStartTime() + localProgress);
    }

    /**
     * Clip the track's channel.
     *
     * <p><b>Important: </b>Type safety is not enforced internally.
     * Make sure that all segments’ channels named {@code channelName} are of the same type.</p>
     *
     * @param channelName the name of the channel to extract
     * @param fromTimeS the start time (seconds)
     * @param toTimeS the end time (seconds)
     * @return an iterable of keyframes, or {@code null} if no segment with a channel named {@code channelName} exists within the specified time period.
     * @param <T> the type of the channel value
     */
    @Nullable
    public <T> Iterable<Keyframe<T>> clip(String channelName, float fromTimeS, float toTimeS) {
        int fromIndex = findIndexBefore(fromTimeS, false);
        int toIndex = findIndexBefore(toTimeS, true);
        if (fromIndex != -1) {
            Keyframe<AnimationSegment> keyframe = get(fromIndex);
            if (keyframe.getTimeS() + keyframe.getValue().getLength() < fromTimeS) {
                fromIndex++;
            }
        } else {
            fromIndex = 0;
        }
        if (fromIndex == toIndex) {
            Keyframe<AnimationSegment> keyframe = get(fromIndex);
            float keyframeTime = keyframe.getTimeS();
            AnimationSegment segment = keyframe.getValue();
            float segmentEndTime = segment.getEndTime();
            float clipStartTime = fromTimeS > keyframeTime ? segment.getStartTime() + fromTimeS - keyframeTime : segment.getStartTime();
            float clipEndTime = toTimeS <= keyframeTime + segment.getLength() ? segment.getStartTime() + toTimeS - keyframeTime : segmentEndTime + Math.ulp(segmentEndTime);
            return segment.getAnimation().clip(channelName, clipStartTime, clipEndTime);
        }
        if (fromIndex < toIndex) {
            Keyframe<AnimationSegment> firstKeyframe = get(fromIndex);
            float firstKeyframeTime = firstKeyframe.getTimeS();
            AnimationSegment firstSegment = firstKeyframe.getValue();
            float firstClipLeft = fromTimeS > firstKeyframeTime ? firstSegment.getStartTime() + fromTimeS - firstKeyframeTime : firstSegment.getStartTime();
            float firstClipRight = firstSegment.getEndTime();
            Iterable<Keyframe<T>> clip = firstSegment.getAnimation().clip(channelName, firstClipLeft, firstClipRight + Math.ulp(firstClipRight));
            for (int i = fromIndex + 1; i <= toIndex - 1; i++) {
                AnimationSegment segment = get(i).getValue();
                float endTime = segment.getEndTime();
                Iterable<Keyframe<T>> newClip = segment.getAnimation().clip(channelName, segment.getStartTime(), endTime + Math.ulp(endTime));
                if (newClip != null) {
                    clip = clip == null ? newClip : Iterables.concat(clip, newClip);
                }
            }
            Keyframe<AnimationSegment> lastKeyframe = get(toIndex);
            AnimationSegment lastSegment = lastKeyframe.getValue();
            float lastSegmentStartTime = lastSegment.getStartTime();
            Iterable<Keyframe<T>> lastClip = lastSegment.getAnimation().clip(channelName, lastSegmentStartTime, lastSegmentStartTime + toTimeS - lastKeyframe.getTimeS());
            if (lastClip != null) {
                clip = clip == null ? lastClip : Iterables.concat(clip, lastClip);
            }
            return clip;
        }
        return null;
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
