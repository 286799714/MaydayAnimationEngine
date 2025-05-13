package com.maydaymemory.mae.basic;

public interface InterpolatableKeyframe<T> extends Keyframe<T> {
    /**
     * This value will be used to interpolate with the previous keyframe.
     */
    T getPre();

    /**
     * This value will be used to interpolate with the subsequent keyframe.
     */
    T getPost();

    /**
     * Get interpolator this keyframe using.
     * WHen interpolating, this interpolator may not be used.
     * Which interpolator will be used is decided by their priority.
     *
     * @see Interpolator#getPriority()
     */
    Interpolator<T> getInterpolator();
}
