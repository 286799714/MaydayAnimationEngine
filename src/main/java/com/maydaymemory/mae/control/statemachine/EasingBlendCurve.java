package com.maydaymemory.mae.control.statemachine;

import com.maydaymemory.mae.util.Easing;

/**
 * Implementation of {@link IBlendCurve} that uses an easing function.
 * 
 * <p>This class wraps an {@link Easing} function to provide blend curve
 * functionality. Easing functions provide various interpolation patterns
 * such as ease-in, ease-out, ease-in-out, and other smooth transitions.</p>
 * 
 * <p>The easing blend curve is commonly used for creating natural-looking
 * animation transitions that follow established animation principles
 * and provide pleasing visual feedback.</p>
 * 
 * @author MaydayMemory
 * @since 1.0.1
 */
public class EasingBlendCurve implements IBlendCurve {
    /** The easing function used for interpolation */
    private final Easing easing;

    /**
     * Constructs a new EasingBlendCurve with the specified easing function.
     * 
     * @param easing the easing function to use for interpolation
     */
    public EasingBlendCurve(Easing easing) {
        this.easing = easing;
    }

    @Override
    public float evaluate(float alpha) {
        return easing.ease(alpha, 0, 1, 1);
    }
}
