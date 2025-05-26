package com.maydaymemory.mae.basic;

import org.joml.Vector3fc;

import javax.annotation.Nullable;

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
     * @param i typically representing a specific bone.
     * @param channel the animation channel
     */
    void setTranslationChannel(int i, @Nullable InterpolatableChannel<? extends Vector3fc> channel);

    /**
     * Set a scale animation channel to this animation.
     *
     * @param i typically representing a specific bone.
     * @param channel the animation channel
     */
    void setScaleChannel(int i, @Nullable InterpolatableChannel<? extends Vector3fc> channel);

    /**
     * Set a rotation animation channel to this animation.
     *
     * @param i typically representing a specific bone.
     * @param channel the animation channel
     */
    void setRotationChannel(int i, @Nullable InterpolatableChannel<? extends Vector3fc> channel);

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
     * Returns the end time of the animation.
     * Usually it should be the maximum value of the end time of all channels.
     *
     * @return the end time of the animation.
     */
    float getEndTimeS();
}
