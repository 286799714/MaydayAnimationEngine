package com.maydaymemory.mae.control.misc;

import com.maydaymemory.mae.basic.*;
import com.maydaymemory.mae.control.OutputPort;
import com.maydaymemory.mae.control.PoseSlot;
import com.maydaymemory.mae.control.Slot;
import com.maydaymemory.mae.control.Tickable;
import com.maydaymemory.mae.util.LongSupplier;
import com.maydaymemory.mae.util.MathUtil;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Iterator;
import java.util.function.Supplier;

public class RealtimeVelocityEstimatorNode implements Tickable {
    private final Slot<Pose> poseSlot = new PoseSlot();
    private final OutputPort<Pose> outputPort = this::getVelocityPose;
    private final Supplier<PoseBuilder> poseBuilderSupplier;
    private final LongSupplier currentNanosSupplier;
    private Pose lastPose;
    private long lastTickTime;
    private Pose currentPose;
    private float intervalTime;

    public RealtimeVelocityEstimatorNode(Supplier<PoseBuilder> poseBuilderSupplier, LongSupplier currentNanosSupplier) {
        this.poseBuilderSupplier = poseBuilderSupplier;
        this.currentNanosSupplier = currentNanosSupplier;
    }

    public Slot<Pose> getPoseSlot() {
        return poseSlot;
    }

    public OutputPort<Pose> getOutputPort() {
        return outputPort;
    }

    /**
     * This method calculates the velocity of the current pose input in realtime.
     *
     * <p>Please make sure that the Poses correspond to the same bones and the changes are as continuous as possible,
     * otherwise the calculated results will not make sense.</p>
     *
     * @return estimated pose
     */
    public Pose getVelocityPose() {
        if (lastPose == null || currentPose == null || intervalTime <= 0) {
            return DummyPose.INSTANCE;
        }
        return poseDifferentiation(lastPose, currentPose, poseBuilderSupplier, intervalTime);
    }

    static Pose poseDifferentiation(Pose lastPose, Pose currentPose, Supplier<PoseBuilder> poseBuilderSupplier, float intervalTime) {
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

    @Override
    public void tick() {
        if (lastPose == null) {
            lastPose = poseSlot.get();
            lastTickTime = currentNanosSupplier.getAsLong();
        } else{
            long currentTime = currentNanosSupplier.getAsLong();
            intervalTime = MathUtil.toSecond(currentTime - lastTickTime);
            lastTickTime = currentTime;
            if (currentPose != null) {
                lastPose = currentPose;
            }
            currentPose = poseSlot.get();
        }
    }
}
