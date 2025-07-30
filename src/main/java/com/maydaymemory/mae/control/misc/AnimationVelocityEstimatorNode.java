package com.maydaymemory.mae.control.misc;

import com.maydaymemory.mae.basic.*;
import com.maydaymemory.mae.control.OutputPort;
import com.maydaymemory.mae.control.Slot;
import com.maydaymemory.mae.util.MathUtil;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Velocity estimation node, which could estimate pose velocity of animation.
 *
 * <p>
 * <b>Experimental:</b> users should be prepared for potential API changes and behavior modifications
 * in future versions.
 * </p>
 */
public class AnimationVelocityEstimatorNode {
    private final Slot<Animation> animationSlot = new Slot<>();
    private final Slot<Float> timeSlot = new Slot<>();
    private final Slot<Float> samplingIntervalSlot = new Slot<>();
    private final OutputPort<Pose> outputPort = this::getVelocityPose;
    private final Supplier<PoseBuilder> poseBuilderSupplier;

    /**
     * Constructs a new AnimationVelocityEstimatorNode.
     *
     * @param poseBuilderSupplier a supplier for a PoseBuilder
     */
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
        return poseDifferentiation(p0, p1, poseBuilderSupplier, samplingInterval);
    }

    /**
     * Gets the sampling interval slot.
     *
     * <p>
     * The animation speed is estimated by sampling the animation at two points,
     * calculating the difference, and dividing it by the interval. The default value is 0.016s.
     * This value should not be too large or too small.
     * </p>
     *
     * @return sampling interval slot
     */
    public Slot<Float> getSamplingIntervalSlot() {
        return samplingIntervalSlot;
    }

    /**
     * Get time slot for input.
     *
     * @return time slot
     */
    public Slot<Float> getTimeSlot() {
        return timeSlot;
    }

    /**
     * Get Animation slot for input.
     *
     * @return Animation slot
     */
    public Slot<Animation> getAnimationSlot() {
        return animationSlot;
    }

    /**
     * Get Pose output port.
     *
     * @return pose output port
     */
    public OutputPort<Pose> getOutputPort() {
        return outputPort;
    }

    /**
     * Calculates velocity by differentiating between two poses.
     * This is a simple two-frame differentiation method for basic velocity estimation.
     *
     * @param lastPose the previous pose
     * @param currentPose the current pose
     * @param poseBuilderSupplier supplier for pose builders
     * @param intervalTime time interval between poses in seconds
     * @return velocity pose representing the rate of change
     */
    private static Pose poseDifferentiation(Pose lastPose, Pose currentPose, Supplier<PoseBuilder> poseBuilderSupplier, float intervalTime) {
        Iterator<BoneTransform> i0 = lastPose.getBoneTransforms().iterator();
        Iterator<BoneTransform> i1 = currentPose.getBoneTransforms().iterator();
        BoneTransform t0 = i0.hasNext() ? i0.next() : null;
        BoneTransform t1 = i1.hasNext() ? i1.next() : null;
        PoseBuilder builder = poseBuilderSupplier.get();
        while (t0 != null && t1 != null) {
            int cmp = t0.compareTo(t1);
            if (cmp == 0) {
                Vector3fc translationVelocity = t1.translation().sub(t0.translation(), new Vector3f()).div(intervalTime);
                Vector3fc scaleVelocity = t1.scale().sub(t0.scale(), new Vector3f()).div(intervalTime);
                Quaternionfc q0 = t0.rotation().asQuaternion();
                Quaternionfc q1 = t1.rotation().asQuaternion();
                Quaternionf relativeRotation = new Quaternionf();
                q1.mul(q0.conjugate(relativeRotation), relativeRotation); // relativeRotation = q1 * q0^-1
                Vector3f rotationVelocity = MathUtil.logUnit(relativeRotation).div(intervalTime);
                BoneTransform boneTransform = new BoneTransform(t0.boneIndex(), translationVelocity, new RotationVelocityRotationView(rotationVelocity), scaleVelocity);
                builder.addBoneTransform(boneTransform);
                t0 = i0.hasNext() ? i0.next() : null;
                t1 = i1.hasNext() ? i1.next() : null;
            } else if (cmp < 0) {
                t0 = i0.hasNext() ? i0.next() : null;
            } else {
                t1 = i1.hasNext() ? i1.next() : null;
            }
        }
        return builder.toPose();
    }
}
