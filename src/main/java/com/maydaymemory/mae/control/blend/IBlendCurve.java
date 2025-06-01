package com.maydaymemory.mae.control.blend;

public interface IBlendCurve {
    /**
     * Evaluates the blend curve at the given alpha value.
     * <p>
     * This method takes a normalized input value {@code alpha} in the range [0.0, 1.0]
     * and returns a corresponding normalized output value in the same range, according
     * to the shape of the blend curve implementation.
     * </p>
     *
     * @param alpha a normalized input value between 0.0 and 1.0 (inclusive)
     * @return a normalized output value between 0.0 and 1.0 (inclusive)
     * @throws IllegalArgumentException if {@code alpha} is outside the [0.0, 1.0] range
     */
    float evaluate(float alpha);
}
