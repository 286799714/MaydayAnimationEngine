package com.maydaymemory.mae.control.statemachine;

/**
 * Implementation of {@link IBlendCurve} that returns a constant value.
 * 
 * <p>This class provides a simple blend curve that always returns the same
 * value regardless of the input alpha. This is useful for creating
 * transitions with constant interpolation factors or for testing purposes.</p>
 * 
 * <p>The floor blend curve can be used when you need a fixed blending
 * amount throughout the entire transition duration.</p>
 * 
 * @author MaydayMemory
 * @since 1.0.1
 */
public class FloorBlendCurve implements IBlendCurve {
    /** The constant value to return for all inputs */
    private final float value;

    /**
     * Constructs a new FloorBlendCurve with the specified constant value.
     * 
     * @param value the constant value to return for all inputs
     */
    public FloorBlendCurve(float value) {
        this.value = value;
    }

    @Override
    public float evaluate(float alpha) {
        return value;
    }
}
