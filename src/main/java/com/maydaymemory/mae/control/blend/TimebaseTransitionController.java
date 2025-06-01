package com.maydaymemory.mae.control.blend;

import com.maydaymemory.mae.util.LongSupplier;
import org.joml.Math;

public class TimebaseTransitionController implements ITransitionController{
    private final LongSupplier currentNanosSupplier;
    private long startNanos = -1;
    private final long duration;
    private final IBlendCurve curve;

    public TimebaseTransitionController(LongSupplier currentNanosSupplier, long duration, IBlendCurve curve) {
        this.currentNanosSupplier = currentNanosSupplier;
        this.duration = duration;
        this.curve = curve;
    }

    @Override
    public void start() {
        this.startNanos = currentNanosSupplier.getAsLong();
    }

    @Override
    public float getTransitionProgress() {
        if (startNanos == -1) {
            return 0f;
        }
        long currentNanos = currentNanosSupplier.getAsLong();
        float alpha = (float) ((double) (currentNanos - startNanos) / (double) duration);
        return curve.evaluate(Math.clamp(0, 1, alpha));
    }

    @Override
    public boolean isFinished() {
        if (startNanos == -1) {
            return false;
        }
        long currentNanos = currentNanosSupplier.getAsLong();
        return (currentNanos - startNanos) >= duration;
    }
}
