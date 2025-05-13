package com.maydaymemory.mae.basic;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Vector3fCubicSplineInterpolator implements Interpolator<Vector3fc> {
    private Vector3fCubicSplineInterpolator() {}

    public static final Vector3fCubicSplineInterpolator INSTANCE = new Vector3fCubicSplineInterpolator();

    /**
     * Interpolates between two keyframes in the given channel using cubic spline interpolation.
     * The interpolation considers the previous and next keyframes to ensure a smooth transition.
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
        int size = channel.getKeyFrameCount();
        int prev = indexFrom == 0 ? 0 : indexFrom - 1;
        int next = indexTo == (size - 1) ? indexTo : indexTo + 1;
        Vector3fc vecPrev = channel.getKeyFrame(prev).getPost();
        Vector3fc vecFrom = channel.getKeyFrame(indexFrom).getPre();
        Vector3fc vecTo = channel.getKeyFrame(indexTo).getPre();
        Vector3fc vecNext = channel.getKeyFrame(next).getPre();
        float x = cubicSpline(vecPrev.x(), vecFrom.x(), vecTo.x(), vecNext.x(), alpha);
        float y = cubicSpline(vecPrev.y(), vecFrom.y(), vecTo.y(), vecNext.y(), alpha);
        float z = cubicSpline(vecPrev.z(), vecFrom.z(), vecTo.z(), vecNext.z(), alpha);
        return new Vector3f(x, y, z);
    }

    /**
     * Returns the priority level of this interpolator.
     * <p>
     * Spline interpolation's priority is set to
     * {@code Priority.HIGH} by default to ensure it is selected over less sophisticated methods when available.
     *
     * @return the priority level for spline interpolation, which is {@code Priority.HIGH}
     */
    @Override
    public Priority getPriority() {
        return Priority.HIGH;
    }

    private static float cubicSpline(float p, float x, float y, float n, float alpha) {
        float v0 = (y - p) * 0.5f;
        float v1 = (n - x) * 0.5f;
        float t2 = alpha * alpha;
        float t3 = alpha * t2;
        float h1 = 2f * t3 - 3f * t2 + 1f;
        float h2 = -2f * t3 + 3f * t2;
        float h3 = t3 - 2f * t2 + alpha;
        float h4 = t3 - t2;
        return h1 * x + h2 * y + h3 * v0 + h4 * v1;
    }
}
