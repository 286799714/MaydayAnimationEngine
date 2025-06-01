package com.maydaymemory.mae.control.blend;

import com.maydaymemory.mae.util.Easing;

public class EasingBlendCurve implements IBlendCurve {
    private final Easing easing;

    public EasingBlendCurve(Easing easing) {
        this.easing = easing;
    }

    @Override
    public float evaluate(float alpha) {
        return easing.ease(alpha, 0, 1, 1);
    }
}
