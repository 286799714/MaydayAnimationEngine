package com.maydaymemory.mae.control.blend;

public class FloorBlendCurve implements IBlendCurve {
    private final float value;

    public FloorBlendCurve(float value) {
        this.value = value;
    }

    @Override
    public float evaluate(float alpha) {
        return value;
    }
}
