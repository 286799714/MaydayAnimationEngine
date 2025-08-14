package com.maydaymemory.mae.control.montage;

import com.maydaymemory.mae.basic.*;
import com.maydaymemory.mae.blend.*;
import com.maydaymemory.mae.control.OutputPort;
import com.maydaymemory.mae.control.PoseSlot;
import com.maydaymemory.mae.control.Tickable;
import com.maydaymemory.mae.util.Iterables;
import com.maydaymemory.mae.util.LongSupplier;
import com.maydaymemory.mae.util.MathUtil;
import com.maydaymemory.mae.util.MergedSortedIterable;
import it.unimi.dsi.fastutil.floats.FloatFloatImmutablePair;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Animation Montage Runner, responsible for executing and managing animation montage playback.
 * 
 * <p>Animation Montage Runner is the core execution component of the animation montage system, responsible for:</p>
 * <ul>
 *   <li>Animation segment playback control</li>
 *   <li>Multi-track animation blending</li>
 *   <li>Animation notification triggering</li>
 *   <li>Animation state updates</li>
 *   <li>Section transitions</li>
 * </ul>
 *
 * <p><b>Important: </b>
 * Montage Runner is not thread-safe, please do not call it asynchronously
 * </p>
 * 
 * @param <T> Context type
 * 
 * @author MaydayMemory
 * @since 1.0.4
 */
public class AnimationMontageRunner<T> implements Tickable {
    /** Base pose slot, for inputting base pose */
    private final PoseSlot basePoseSlot = new PoseSlot();
    
    /** Output port, for outputting blended pose */
    private final OutputPort<Pose> outputPort = this::getPose;

    /** Layered blender, for animation layered blending */
    private final LayeredBlender layeredBlender;
    
    /** Additional blender, for handling additive animation blending */
    private final AdditiveBlender additiveBlender;
    
    /** Merge blender, for merging multiple poses */
    private final MergeBlender mergeBlender;

    /** Animation montage instance */
    private final AnimationMontage<T> montage;

    private final ArrayList<FloatFloatImmutablePair> clipPlans = new ArrayList<>();
    
    /** Context object */
    private final T context;
    
    /** Nanosecond time supplier, for time calculation */
    private final LongSupplier nanoTimeSupplier;

    /**
     * Notification state control block mapping table.
     * Uses array map since notify states usually less than 100, in this case array map is more efficient.
     */
    private final Object2ObjectArrayMap<IAnimationNotifyState<T>, NotifyStateControlBlock> notifyStates = new Object2ObjectArrayMap<>();

    /** Whether animation is playing */
    private boolean isPlaying = false;
    
    /** Current playback progress (nanoseconds) */
    private long progress;
    
    /** Last update time (nanoseconds) */
    private long lastUpdateTime;
    
    /** Current playing section */
    private AnimationMontageSection section;
    
    /** Playback speed, 1.0 is normal speed. Cannot be negative. */
    private float speed = 1.0f;

    /**
     * Construct a new animation montage runner.
     * 
     * @param montage Animation montage to run
     * @param context Context object
     * @param boneTransformFactory Bone transform factory
     * @param poseBuilderSupplier Pose builder supplier
     * @param nanoTimeSupplier Nanosecond time supplier
     */
    public AnimationMontageRunner(AnimationMontage<T> montage, T context, BoneTransformFactory boneTransformFactory,
                                  Supplier<PoseBuilder> poseBuilderSupplier, LongSupplier nanoTimeSupplier) {
        this.montage = montage;
        this.context = context;
        this.layeredBlender = new SimpleLayeredBlender(boneTransformFactory, poseBuilderSupplier);
        this.additiveBlender = new SimpleAdditiveBlender(boneTransformFactory, poseBuilderSupplier);
        this.mergeBlender = new NoAllocMergeBlender();
        this.nanoTimeSupplier = nanoTimeSupplier;
    }

    /**
     * Get the animation montage instance.
     * 
     * @return Animation montage instance
     */
    public AnimationMontage<T> getMontage() {
        return montage;
    }

    /**
     * Get the context object.
     * 
     * @return Context object
     */
    public T getContext() {
        return context;
    }

    /**
     * Get the output port.
     * 
     * @return Output port
     */
    public OutputPort<Pose> getOutputPort() {
        return outputPort;
    }

    /**
     * Get the base pose slot, for inputting base pose.
     * 
     * @return Base pose slot
     */
    public PoseSlot getBasePoseSlot() {
        return basePoseSlot;
    }

    /**
     * Set playback speed.
     * 
     * @param speed Playback speed, negative values will be set to 0
     */
    public void setSpeed(float speed) {
        if (speed < 0) {
            this.speed = 0;
            return;
        }
        this.speed = speed;
    }

    /**
     * Get current playback speed.
     * 
     * @return Playback speed
     */
    public float getSpeed() {
        return speed;
    }

    /**
     * Check if animation is playing.
     * 
     * @return Returns true if playing, false otherwise
     */
    public boolean isPlaying() {
        return isPlaying;
    }

    /**
     * Get current playback progress (seconds).
     * 
     * @return Playback progress (seconds), returns 0 if not playing
     */
    public float getProgress() {
        if (!isPlaying) {
            return 0;
        }
        return MathUtil.toSecond(progress);
    }

    /**
     * Start playing the specified section.
     * 
     * @param sectionName Section name to play
     */
    public void start(String sectionName) {
        if (isPlaying) {
            return;
        }
        setSection(sectionName);
        if (section != null) {
            this.isPlaying = true;
        }
    }

    /**
     * Stop playing animation.
     */
    public void stop() {
        if (!isPlaying) {
            return;
        }
        isPlaying = false;
    }

    /**
     * Set progress directly without checking section limits.
     *
     * @param progress progress in nanoseconds.
     */
    public void setProgressUnsafe(long progress) {
        this.progress = progress;
        this.lastUpdateTime = nanoTimeSupplier.getAsLong();
    }

    /**
     * Set progress and clamp to the range allowed by the current section.
     * If no section is currently specified, do nothing.
     *
     * @param progress progress in nanoseconds.
     */
    public void setProgress(long progress) {
        if (section != null) {
            this.lastUpdateTime = nanoTimeSupplier.getAsLong();
            long sectionStartTime = MathUtil.toNanos(section.getStartTime());
            long sectionEndTime = MathUtil.toNanos(section.getEndTime());
            if (sectionStartTime > progress) {
                this.progress = sectionStartTime;
            } else {
                this.progress = Math.min(sectionEndTime, progress);
            }
        }
    }

    /**
     * Set the runner's current section and set the progress to the start of the section.
     *
     * @param sectionName the name of section.
     */
    public void setSection(String sectionName) {
        AnimationMontageSection section = montage.getSection(sectionName);
        if (section == null) {
            return;
        }
        this.progress = MathUtil.toNanos(section.getStartTime());
        this.lastUpdateTime = nanoTimeSupplier.getAsLong();
        this.section = section;
    }

    /**
     * Push forward the runner progress.
     *
     * <p>This method is similar to {@link #tick()}, but allows you to control the progress yourself.</p>
     *
     * @param progressForward progress to advance, in nanosecond
     */
    public void tickForward(long progressForward) {
        if (!isPlaying) {
            return;
        }
        long newProgress = progress + progressForward;
        lastUpdateTime = nanoTimeSupplier.getAsLong();
        handleProgressChange(newProgress);
    }

    /**
     * Update animation playback state.
     *
     * <p>This method updates playback progress, handles section transitions, and triggers corresponding notifications.</p>
     */
    @Override
    public void tick() {
        tickForward((long) ((nanoTimeSupplier.getAsLong() - lastUpdateTime) * speed));
    }

    /**
     * Get current pose.
     * 
     * <p>This method calculates the blended pose of all enabled tracks, including additive animation processing.</p>
     * 
     * @return Current blended pose
     */
    public Pose getPose() {
        Pose basePose = basePoseSlot.get();
        List<AnimationMontageTrack> tracks = montage.getTracks();
        if (tracks == null) {
            return basePose;
        }
        List<Pose> blendedPoses = new ArrayList<>();
        for (AnimationMontageTrack track : tracks) {
            if (!track.isEnabled()) {
                continue;
            }
            Pose animationPose = track.evaluate(MathUtil.toSecond(progress));
            if (animationPose == null) {
                continue;
            }
            if (track.isAdditive()) {
                Pose addtivePose = layeredBlender.blend(DummyPose.INSTANCE, animationPose, track.getLayer());
                blendedPoses.add(additiveBlender.blend(basePose, addtivePose));
            } else {
                blendedPoses.add(layeredBlender.blend(basePose, animationPose, track.getLayer()));
            }
        }
        if (blendedPoses.isEmpty()) {
            return basePose;
        }
        return mergeBlender.blend(blendedPoses);
    }

    public <E> List<E> evaluateCurves(String curveName) {
        List<AnimationMontageTrack> tracks = montage.getTracks();
        if (tracks == null || tracks.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<E> arrayList = new ArrayList<>();
        for (AnimationMontageTrack track : tracks) {
            if (!track.isEnabled()) {
                arrayList.add(null);
            } else {
                arrayList.add(track.evaluateCurve(curveName, MathUtil.toSecond(progress)));
            }
        }
        return arrayList;
    }

    @Nonnull
    public <E> Iterable<Keyframe<E>> clip(String channelName) {
        List<AnimationMontageTrack> tracks = montage.getTracks();
        if (tracks == null || tracks.isEmpty()) {
            return Collections.emptyList();
        }
        Iterable<Keyframe<E>> result = null;
        for (FloatFloatImmutablePair clipPlan : clipPlans) {
            float first = clipPlan.firstFloat();
            float second = clipPlan.secondFloat();
            ArrayList<Iterable<Keyframe<E>>> clips = new ArrayList<>();
            for (AnimationMontageTrack track : tracks) {
                if (track.isEnabled()) {
                    Iterable<Keyframe<E>> clip = track.clip(channelName, first, second);
                    if (clip != null && clip.iterator().hasNext()) {
                        clips.add(clip);
                    }
                }
            }
            if (!clips.isEmpty()) {
                Iterable<Keyframe<E>> mergedClip = new MergedSortedIterable<>(clips, Keyframe<E>::compareTo);
                result = result == null ? mergedClip : Iterables.concat(result, mergedClip);
            }
        }
        return result == null ? Collections.emptyList() : result;
    }

    private void handleProgressChange(long newProgress) {
        long sectionEndNanos = MathUtil.toNanos(section.getEndTime());
        if (newProgress >= sectionEndNanos) {
            tickRange(MathUtil.toSecond(progress), section.getEndTime() + Math.ulp(section.getEndTime()));
            String nextSectionName = section.getNextSection();
            section = nextSectionName == null ? null : montage.getSection(nextSectionName);
            long overshootNanos = newProgress - sectionEndNanos;
            while (section != null) {
                long sectionLengthNanos = MathUtil.toNanos(section.getLength());
                if (overshootNanos < sectionLengthNanos) {
                    break;
                }
                sectionEndNanos = MathUtil.toNanos(section.getEndTime());
                tickRange(section.getStartTime(), section.getEndTime() + Math.ulp(section.getEndTime()));
                nextSectionName = section.getNextSection();
                section = nextSectionName == null ? null : montage.getSection(nextSectionName);
                overshootNanos -= sectionLengthNanos;
            }
            if (section != null) {
                newProgress = MathUtil.toNanos(section.getStartTime()) + overshootNanos;
                tickRange(section.getStartTime(), MathUtil.toSecond(newProgress));
                progress = newProgress;
            } else {
                progress = sectionEndNanos;
                isPlaying = false;
            }
        } else {
            tickRange(MathUtil.toSecond(progress), MathUtil.toSecond(newProgress));
            progress = newProgress;
        }
    }

    /**
     * Trigger notifications and update notification states, update clip plan.
     * 
     * @param fromTime Start time (seconds)
     * @param toTime End time (seconds)
     */
    private void tickRange(float fromTime, float toTime) {
        clipPlans.clear();
        clipPlans.add(new FloatFloatImmutablePair(fromTime, toTime));
        triggerNotifies(fromTime, toTime);
        updateNotifyStateSet(fromTime, toTime);
        for (Map.Entry<IAnimationNotifyState<T>, NotifyStateControlBlock> entry : notifyStates.entrySet()) {
            entry.getKey().onUpdate(context);
            entry.getValue().isUpdated = true;
        }
    }

    /**
     * Trigger notifications within the specified time range.
     * 
     * @param fromTime Start time (seconds)
     * @param toTime End time (seconds)
     */
    private void triggerNotifies(float fromTime, float toTime) {
        for (Keyframe<IAnimationNotify<T>> keyframe : montage.clipNotify(fromTime, toTime)) {
            keyframe.getValue().onNotify(context);
        }
    }

    /**
     * Update notification state set within the specified time range.
     * 
     * @param fromTime Start time (seconds)
     * @param toTime End time (seconds)
     */
    private void updateNotifyStateSet(float fromTime, float toTime) {
        for (Keyframe<AnimationNotifyStateMarker<T>> keyframe : montage.clipNotifyState(fromTime, toTime)) {
            AnimationNotifyStateMarker<T> marker = keyframe.getValue();
            if (marker.getMarkerType() == AnimationNotifyStateMarker.MarkerType.START) {
                IAnimationNotifyState<T> notify = marker.getNotify();
                notify.onStart(context);
                notifyStates.put(notify, new NotifyStateControlBlock());
            } else if (marker.getMarkerType() == AnimationNotifyStateMarker.MarkerType.END) {
                notifyStates.compute(marker.getNotify(), (notify, controlBlock) -> {
                    if (controlBlock != null) {
                        if (!controlBlock.isUpdated) {
                            // At lease update notify state once
                            notify.onUpdate(context);
                        }
                        notify.onEnd(context);
                    }
                    return null;
                });
            }
        }
    }

    /**
     * Notification state control block, for tracking notification state update status.
     */
    private static class NotifyStateControlBlock {
        /** Update flag */
        boolean isUpdated = false;
    }
}
