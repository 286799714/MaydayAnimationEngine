package com.maydaymemory.mae.control.montage;

import com.maydaymemory.mae.basic.ClipChannel;
import com.maydaymemory.mae.basic.Keyframe;
import com.maydaymemory.mae.util.MergedSortedIterable;

import javax.annotation.Nullable;
import java.util.*;

/**
 * Animation Montage for managing complex animation sequences.
 * 
 * <p>Animation Montage allows combining multiple animation segments into a complex animation sequence.
 * It contains multiple tracks, each track can play different animation segments, and supports animation notifications and state management.</p>
 * 
 * <p>Main features include:</p>
 * <ul>
 *   <li>Multi-track animation playback</li>
 *   <li>Animation segment splicing</li>
 *   <li>Animation notification system</li>
 *   <li>Animation notification state management</li>
 *   <li>Section playback control</li>
 * </ul>
 * 
 * @param <T> Context type
 * 
 * @author MaydayMemory
 * @since 1.0.4
 */
public class AnimationMontage<T> {
    /** Animation track list, each track contains multiple animation segments, can set their own Layer for layered blending */
    private @Nullable List<AnimationMontageTrack> tracks;
    
    /** Animation notification channel list, for triggering one-time notification events */
    private @Nullable List<ClipChannel<IAnimationNotify<T>>> notifyChannels;
    
    /** Animation state notification channel list, for managing persistent notification states */
    private @Nullable List<ClipChannel<AnimationNotifyStateMarker<T>>> notifyStateChannels;
    
    /** Animation section mapping table, for managing different sections of animation */
    private @Nullable Map<String, AnimationMontageSection> sections;

    /**
     * Get the animation track list.
     * 
     * @return Animation track list, returns null if not set
     */
    @Nullable
    public List<AnimationMontageTrack> getTracks() {
        return tracks;
    }

    /**
     * Set the animation track list.
     * 
     * @param tracks Animation track list, can be null
     */
    public void setTracks(@Nullable List<AnimationMontageTrack> tracks) {
        this.tracks = tracks;
    }

    /**
     * Get the animation notification channel list.
     * 
     * @return Animation notification channel list, returns null if not set
     */
    @Nullable
    public List<ClipChannel<IAnimationNotify<T>>> getNotifyChannels() {
        return notifyChannels;
    }

    /**
     * Set the animation notification channel list.
     * 
     * @param notifyChannels Animation notification channel list, can be null
     */
    public void setNotifyChannels(@Nullable List<ClipChannel<IAnimationNotify<T>>> notifyChannels) {
        this.notifyChannels = notifyChannels;
    }

    /**
     * Get the animation state notification channel list.
     * 
     * @return Animation state notification channel list, returns null if not set
     */
    @Nullable
    public List<ClipChannel<AnimationNotifyStateMarker<T>>> getNotifyStateChannels() {
        return notifyStateChannels;
    }

    /**
     * Set the animation state notification channel list.
     * 
     * @param notifyStateChannels Animation state notification channel list, can be null
     */
    public void setNotifyStateChannels(@Nullable List<ClipChannel<AnimationNotifyStateMarker<T>>> notifyStateChannels) {
        this.notifyStateChannels = notifyStateChannels;
    }

    /**
     * Get the animation section mapping table.
     * 
     * @return Animation section mapping table, returns null if not set
     */
    @Nullable
    public Map<String, AnimationMontageSection> getSections() {
        return sections;
    }

    /**
     * Set the animation section mapping table.
     * 
     * @param sections Animation section mapping table, can be null
     */
    public void setSections(@Nullable Map<String, AnimationMontageSection> sections) {
        this.sections = sections;
    }

    /**
     * Get the specified animation section by name.
     * 
     * @param name Section name
     * @return Specified animation section, returns null if not found
     */
    @Nullable
    public AnimationMontageSection getSection(String name) {
        if (sections == null) {
            return null;
        }
        return sections.get(name);
    }

    /**
     * Get animation notification keyframes within the specified time range.
     * 
     * <p>This method extracts notification keyframes from all notification channels within the specified time range,
     * and returns them merged in chronological order.</p>
     * 
     * @param fromTimeS Start time (seconds)
     * @param toTimeS End time (seconds)
     * @return Merged notification keyframe iterator, returns empty list if no notifications
     */
    public Iterable<Keyframe<IAnimationNotify<T>>> clipNotify(float fromTimeS, float toTimeS) {
        if (notifyChannels == null || notifyChannels.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Iterable<Keyframe<IAnimationNotify<T>>>> list = new ArrayList<>();
        for (ClipChannel<IAnimationNotify<T>> channel : notifyChannels) {
            list.add(channel.clip(fromTimeS, toTimeS));
        }
        return new MergedSortedIterable<>(list, Keyframe::compareTo);
    }

    /**
     * Get animation state notification keyframes within the specified time range.
     * 
     * <p>This method extracts state notification keyframes from all state notification channels within the specified time range,
     * and returns them merged in chronological order.</p>
     * 
     * @param fromTimeS Start time (seconds)
     * @param toTimeS End time (seconds)
     * @return Merged state notification keyframe iterator, returns empty list if no state notifications
     */
    public Iterable<Keyframe<AnimationNotifyStateMarker<T>>> clipNotifyState(float fromTimeS, float toTimeS) {
        if (notifyStateChannels == null || notifyStateChannels.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Iterable<Keyframe<AnimationNotifyStateMarker<T>>>> list = new ArrayList<>();
        for (ClipChannel<AnimationNotifyStateMarker<T>> channel : notifyStateChannels) {
            list.add(channel.clip(fromTimeS, toTimeS));
        }
        return new MergedSortedIterable<>(list, Keyframe::compareTo);
    }
}
