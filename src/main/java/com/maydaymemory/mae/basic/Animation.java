package com.maydaymemory.mae.basic;

import org.joml.Vector3fc;

import javax.annotation.Nullable;

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
     * <p><b>Important: </b>Type safety is not checked internally.
     * Please ensure that the channel type specified is the same when setting and getting.</p>
     *
     * @param channelName name to identify this channel
     * @param channel the clip channel to be assigned. May be {@code null} to clear the slot.
     */
    void setClipChannel(String channelName, @Nullable ClipChannel<?> channel);

    /**
     * <p>Returns a clip result for the channel with the given name.</p>
     *
     * <p>Note that the returned result may be {@code null} if there is no channel with the given name.</p>
     *
     * <p><b>Important: </b>Type safety is not checked internally.
     * Please ensure that the channel type specified is the same when setting and getting.</p>
     *
     * @param channelName name of the channel to extract.
     * @param fromTimeS the start time of the clip period (inclusive), in seconds.
     * @param toTimeS the end time of the clip period (exclusive), in seconds.
     * @return the clip result.
     */
    <T> @Nullable Iterable<Keyframe<T>> clip(String channelName, float fromTimeS, float toTimeS);

    /**
     * Set a curve(interpolatable channel) to this animation.
     *
     * <p><b>Important: </b>Type safety is not checked internally.
     * Please ensure that the curve type specified is the same when setting and getting.</p>
     *
     * @param curveName name to identify this curve.
     * @param curve the curve to be assigned. May be {@code null} to clear the slot.
     */
    void setCurve(String curveName , @Nullable InterpolatableChannel<?> curve);

    /**
     * Returns the interpolated result of the interpolatable channel with the given name.
     *
     * <p>Note that the returned result may be {@code null} if there is no curve with the given name.</p>
     *
     * <p><b>Important: </b>Type safety is not checked internally.
     * Please ensure that the curve type specified is the same when setting and getting.</p>
     *
     * @param curveName name of the curve to evaluate.
     * @param timeS the time to evaluate the curve at (in seconds)
     * @return the interpolated result of the curve with the given name
     * @throws ClassCastException if the keyframe type in the curve with the given name is not of the expected type.
     */
    <T> @Nullable T evaluateCurve(String curveName, float timeS);

    /**
     * Returns the end time of the animation.
     * Usually it should be the maximum value of the end time of all channels.
     *
     * @return the end time of the animation.
     */
    float getEndTimeS();
}
