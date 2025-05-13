package com.maydaymemory.mae.basic;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Vector3fLinearInterpolator implements Interpolator<Vector3fc>{
    private Vector3fLinearInterpolator() {}

    public static Vector3fLinearInterpolator INSTANCE = new Vector3fLinearInterpolator();

    /**
     * Interpolates between two keyframes in the given channel using linear interpolation.
     *
     * @param channel the channel containing the keyframes to interpolate
     * @param indexFrom the index of the starting keyframe
     * @param indexTo the index of the ending keyframe
     * @param alpha the interpolation factor, typically between 0 (start) and 1 (end)
     * @return a {@link Vector3fc} representing the interpolated value at the given alpha factor
     * @throws IndexOutOfBoundsException if the provided indices are out of bounds of the keyframe list
     */
    @Override
    public Vector3fc interpolate(InterpolatableChannel<Vector3fc> channel, int indexFrom, int indexTo, float alpha) {
        InterpolatableKeyframe<Vector3fc> keyFrameFrom = channel.getKeyFrame(indexFrom);
        InterpolatableKeyframe<Vector3fc> keyFrameTo = channel.getKeyFrame(indexTo);
        Vector3fc vecFrom = keyFrameFrom.getPost();
        Vector3fc vecTo = keyFrameTo.getPre();
        return vecFrom.lerp(vecTo, alpha, new Vector3f());
    }

    /**
     * Returns the priority level of this interpolator.
     * <p>
     * For standard linear interpolation, the default priority is set to {@code Priority.MEDIUM}.
     * Override this default when necessary.
     *
     * @return the default priority level for linear interpolation, which is {@code Priority.MEDIUM}
     */
    @Override
    public Priority getPriority() {
        return Priority.MEDIUM;
    }
}
