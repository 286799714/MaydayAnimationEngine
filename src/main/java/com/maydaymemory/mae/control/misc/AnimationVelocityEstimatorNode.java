package com.maydaymemory.mae.control.misc;

import com.maydaymemory.mae.basic.*;
import com.maydaymemory.mae.control.OutputPort;
import com.maydaymemory.mae.control.Slot;
import java.util.Objects;
import java.util.function.Supplier;

public class AnimationVelocityEstimatorNode {
    private final Slot<Animation> animationSlot = new Slot<>();
    private final Slot<Float> timeSlot = new Slot<>();
    private final Slot<Float> samplingIntervalSlot = new Slot<>();
    private final OutputPort<Pose> outputPort = this::getVelocityPose;
    private final Supplier<PoseBuilder> poseBuilderSupplier;

    public AnimationVelocityEstimatorNode(Supplier<PoseBuilder> poseBuilderSupplier) {
        samplingIntervalSlot.setDefaultValue(0.016f); // 60 FPS
        timeSlot.setDefaultValue(0f);
        this.poseBuilderSupplier = poseBuilderSupplier;
    }

    /**
     * Calculates the speed in seconds and returns it
     *
     * @return speed in seconds
     */
    public Pose getVelocityPose() {
        Animation animation = animationSlot.get();
        if (animation == null) {
            return DummyPose.INSTANCE;
        }
        float time = Objects.requireNonNull(timeSlot.get());
        float samplingInterval = Objects.requireNonNull(samplingIntervalSlot.get());
        if (samplingInterval <= 0) {
            throw new IllegalArgumentException(String.format("Sampling interval must be positive. Input interval: %f", samplingInterval));
        }
        Pose p0 = animation.evaluate(time);
        Pose p1 = animation.evaluate(time + samplingInterval);
        return RealtimeVelocityEstimatorNode.poseDifferentiation(p0, p1, poseBuilderSupplier, samplingInterval);
    }

    public Slot<Float> getSamplingIntervalSlot() {
        return samplingIntervalSlot;
    }

    public Slot<Float> getTimeSlot() {
        return timeSlot;
    }

    public Slot<Animation> getAnimationSlot() {
        return animationSlot;
    }

    public OutputPort<Pose> getOutputPort() {
        return outputPort;
    }
}
