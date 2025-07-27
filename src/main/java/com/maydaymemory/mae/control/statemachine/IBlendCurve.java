package com.maydaymemory.mae.control.statemachine;

/**
 * Interface representing a blend curve for animation transitions.
 * 
 * <p>This interface defines the contract for blend curves that control
 * how animation transitions progress over time. Blend curves take a
 * normalized input value and return a corresponding normalized output
 * value, allowing for various interpolation patterns such as linear,
 * ease-in, ease-out, or custom curves.</p>
 * 
 * <p>Blend curves are used to create smooth and natural-looking transitions
 * between animation states, providing control over the timing and feel
 * of the interpolation process.</p>
 * 
 * @author MaydayMemory
 * @since 1.0.1
 */
public interface IBlendCurve {
    /**
     * Evaluates the blend curve at the given alpha value.
     * 
     * <p>This method takes a normalized input value {@code alpha} in the range [0.0, 1.0]
     * and returns a corresponding normalized output value in the same range, according
     * to the shape of the blend curve implementation.</p>
     * 
     * <p>The output value is used to control the interpolation progress during
     * animation transitions, allowing for various easing functions and timing
     * curves to be applied.</p>
     *
     * @param alpha a normalized input value between 0.0 and 1.0 (inclusive)
     * @return a normalized output value between 0.0 and 1.0 (inclusive)
     * @throws IllegalArgumentException if {@code alpha} is outside the [0.0, 1.0] range
     */
    float evaluate(float alpha);
}
