package com.maydaymemory.mae.basic;

import org.joml.Vector3fc;

import javax.annotation.Nullable;
import java.util.List;

/***
 * Animation interface.
 */
public interface Animation {
    /**
     * Get the name of the animation.
     * This can be used to identify or reference the animation clip.
     *
     * @return the name of the animation.
     */
    String getName();

    /**
     * Set a translation animation channel to this animation.
     *
     * @param boneIndex typically representing a specific bone.
     * @param channel the animation channel
     */
    void setTranslationChannel(int boneIndex, @Nullable InterpolatableChannel<? extends Vector3fc> channel);

    /**
     * Set a scale animation channel to this animation.
     *
     * @param boneIndex typically representing a specific bone.
     * @param channel the animation channel
     */
    void setScaleChannel(int boneIndex, @Nullable InterpolatableChannel<? extends Vector3fc> channel);

    /**
     * Set a rotation animation channel to this animation.
     *
     * @param boneIndex typically representing a specific bone.
     * @param channel the animation channel
     */
    void setRotationChannel(int boneIndex, @Nullable InterpolatableChannel<? extends Rotation> channel);

    /**
     * Evaluates the animation at the given time and returns the corresponding pose.
     * <p>
     * This method samples the animation at the specified time (in seconds) and returns a {@link Pose}
     * representing the skeletal transform state at that moment. The returned pose contains bone transforms
     * such as translation, rotation, and scale for each affected bone.
     * </p>
     *
     * @param timeS The time (in seconds) at which to evaluate the animation.
     * @return A {@link Pose} representing the skeletal pose at the given time.
     */
    Pose evaluate(float timeS);

    /**
     * Set a clip channel to this animation.
     *
     * @param i the slot index of the clip channel. The result of {@link #clip(float, float)}
     *          will return the result at the same index.The index space is isolated from the curves(interpolatable channels),
     *          so it is ok to {@link #setCurve(int, InterpolatableChannel) set a curve} with same index.
     * @param channel the clip channel to be assigned. May be {@code null} to clear the slot.
     */
    void setClipChannel(int i, @Nullable ClipChannel<?> channel);

    /**
     * Get the results of all clip channels over the specified time range.
     *
     * <p>The returned collection's slot order corresponds to the index specified in
     * {@link #setClipChannel(int, ClipChannel)}. If a clip channel was never set at a given index,
     * the corresponding element in the returned collection will be {@code null}.</p>
     *
     * <p>{@code fromTimeS} can be greater than {@code toTimeS}, in which case the results are
     * returned in reverse order. See the declaration of {@link ClipChannel#clip(float, float)}} for details. </p>
     *
     * @param fromTimeS the start time of the clip period (inclusive), in seconds.
     * @param toTimeS the end time of the clip period (exclusive), in seconds.
     * @return a collection of clip results; each element corresponds to the slot index.
     *         If no channel is set at an index, the result at that index will be {@code null}.
     * @see ClipChannel#clip(float, float)
     */
    List<Iterable<? extends Keyframe<?>>> clip(float fromTimeS, float toTimeS);

    /**
     * <p>Returns a clip result for the channel at the given index.</p>
     *
     * <p>This method is equivalent to {@link #clip(float, float)} except that it only returns a clip result for the
     * channel at the given index.</p>
     *
     * <p>Note that the returned result may be {@code null} if no channel is set at the given index.</p>
     *
     * @param i the index of the clip channel.
     * @param fromTimeS the start time of the clip period (inclusive), in seconds.
     * @param toTimeS the end time of the clip period (exclusive), in seconds.
     * @return the clip result for the channel at the given index.
     */
    @Nullable Iterable<? extends Keyframe<?>> clip(int i, float fromTimeS, float toTimeS);

    /**
     * Set a curve(interpolatable channel) to this animation.
     *
     * @param i the slot index of the curve. The result of {@link #evaluateCurve(float)}
     *          will return the result at the same index. The index space is isolated from the clip channels,
     *          so it is ok to {@link #setClipChannel(int, ClipChannel) set a clip channel} with same index.
     * @param curve the curve to be assigned. May be {@code null} to clear the slot.
     */
    void setCurve(int i , @Nullable InterpolatableChannel<?> curve);

    /**
     * Get the result of all curves over the specified time.
     *
     * <p>The returned collection's slot order corresponds to the index specified in
     * {@link #setCurve(int, InterpolatableChannel)}. If a curve was never set at a given index,
     * the corresponding element in the returned collection will be {@code null}.</p>
     *
     * @param timeS time to evaluate
     * @return a collection of interpolated results; each element corresponds to the slot index.
     *         If no curve is set at an index, the result at that index will be {@code null}.
     */
    List<Object> evaluateCurve(float timeS);

    /**
     * Returns the interpolated result of the interpolatable channel at the given index.
     *
     * <p>This method is equivalent to {@code evaluateCurve(i, getEndTimeS())}
     * except that it returns a single result instead of a collection.</p>
     *
     * <p>Note that the returned result may be {@code null} if no curve is set at the given index.</p>
     *
     * @param i the index of the curve to evaluate
     * @param timeS the time to evaluate the curve at (in seconds)
     * @return the interpolated result of the curve at the given index
     */
    @Nullable Object evaluateCurve(int i, float timeS);

    /**
     * Returns the end time of the animation.
     * Usually it should be the maximum value of the end time of all channels.
     *
     * @return the end time of the animation.
     */
    float getEndTimeS();
}
