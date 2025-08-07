package com.maydaymemory.mae.control.statemachine;

import com.maydaymemory.mae.control.blend.IBlendCurve;
import com.maydaymemory.mae.util.LongSupplier;
import org.joml.Math;

/**
 * Controller class that manages the timing and progress of a transition.
 * 
 * <p>This class handles the temporal aspects of a transition, including
 * start time tracking, duration management, and progress calculation.
 * It uses a blend curve to calculate final interpolation progress and
 * provides methods to check if the transition has completed.</p>
 * 
 * @author MaydayMemory
 * @since 1.0.1
 */
public class TransitionController {
    /** Supplier for current time in nanoseconds */
    private final LongSupplier currentNanosSupplier;
    
    /** Start time of the transition in nanoseconds */
    private long startNanos;

    private boolean isStarted = false;
    
    /** Duration of the transition in nanoseconds */
    private final long duration;
    
    /** The blend curve used for progress calculation */
    private final IBlendCurve curve;

    /**
     * Constructs a new TransitionController with the specified parameters.
     * 
     * @param currentNanosSupplier supplier for current time in nanoseconds
     * @param duration the duration of the transition in nanoseconds
     * @param curve the blend curve to use for progress calculation
     */
    public TransitionController(LongSupplier currentNanosSupplier, long duration, IBlendCurve curve) {
        this.currentNanosSupplier = currentNanosSupplier;
        this.duration = duration;
        this.curve = curve;
    }

    /**
     * Starts the transition timing.
     * 
     * <p>This method records the current time as the start time of the
     * transition. It should be called when the transition begins.</p>
     */
    public void start() {
        this.startNanos = currentNanosSupplier.getAsLong();
        this.isStarted = true;
    }

    /**
     * Gets the current progress of the transition.
     * 
     * <p>This method calculates the current progress through the transition
     * based on elapsed time and applies the blend curve to determine
     * the interpolation factor.</p>
     * 
     * @return the transition progress as a value between 0.0 and 1.0
     */
    public float getTransitionProgress() {
        if (!isStarted) {
            return 0f;
        }
        long currentNanos = currentNanosSupplier.getAsLong();
        float alpha = (float) ((double) (currentNanos - startNanos) / (double) duration);
        return curve.evaluate(Math.clamp(0, 1, alpha));
    }

    /**
     * Checks if the transition has finished.
     * 
     * <p>This method determines if the transition has completed based on
     * the elapsed time since the transition started.</p>
     * 
     * @return true if the transition has finished, false otherwise
     */
    public boolean isFinished() {
        if (!isStarted) {
            return false;
        }
        long currentNanos = currentNanosSupplier.getAsLong();
        return (currentNanos - startNanos) >= duration;
    }
}
