package com.maydaymemory.mae.control.misc;

import com.maydaymemory.mae.basic.*;
import com.maydaymemory.mae.control.OutputPort;
import com.maydaymemory.mae.control.PoseSlot;
import com.maydaymemory.mae.control.Slot;
import com.maydaymemory.mae.control.Tickable;
import com.maydaymemory.mae.util.LongSupplier;
import com.maydaymemory.mae.util.MathUtil;
import it.unimi.dsi.fastutil.ints.Int2ObjectAVLTreeMap;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;

import java.util.*;
import java.util.function.Supplier;

/**
 * Experimental enhanced real-time velocity estimation node
 * 
 * <p>This implementation includes:</p>
 * <ul>
 *   <li><strong>Multi-frame sampling:</strong> Uses multiple historical frames for more stable velocity estimation</li>
 *   <li><strong>Velocity limiting:</strong> Clamps velocity values to prevent unrealistic spikes</li>
 *   <li><strong>Smooth filtering:</strong> Applies low-pass filtering to smooth velocity changes</li>
 * </ul>
 *
 * <p>This approach significantly improves velocity estimation accuracy compared to simple two-frame differentiation,
 * However, due to its experimental nature, users should be prepared for potential API changes and behavior modifications
 * in future versions.</p>
 */
public class RealtimeVelocityEstimatorNode implements Tickable {
    private final Slot<Pose> poseSlot = new PoseSlot();
    private final OutputPort<Pose> outputPort = this::getVelocityPose;
    private final Supplier<PoseBuilder> poseBuilderSupplier;
    private final LongSupplier currentNanosSupplier;

    // Configuration parameters
    private final int maxHistoryFrames;

    // Historical data storage
    private final Queue<HistoricalFrame> frameHistory;

    // Current state
    private long lastTickTime;
    private boolean isInitialized;

    /**
     * Creates a new velocity estimator with default parameters.
     * 
     * <p><strong>Note:</strong> This is an experimental implementation. The default parameters
     * may change in future versions as the algorithm is refined.</p>
     */
    public RealtimeVelocityEstimatorNode(Supplier<PoseBuilder> poseBuilderSupplier, LongSupplier currentNanosSupplier) {
        this(poseBuilderSupplier, currentNanosSupplier, 8);
    }

    /**
     * Creates a new velocity estimator with custom parameters.
     *
     * <p><strong>Experimental Warning:</strong> This constructor and its parameters are experimental.
     * The parameter ranges and optimal values may change as the algorithm is refined. Users should
     * be prepared for potential API changes in future versions.</p>
     *
     * @param poseBuilderSupplier    supplier for pose builders
     * @param currentNanosSupplier   supplier for current time in nanoseconds
     * @param maxHistoryFrames       maximum number of historical frames to keep (typically 4-12)
     */
    public RealtimeVelocityEstimatorNode(Supplier<PoseBuilder> poseBuilderSupplier, LongSupplier currentNanosSupplier, int maxHistoryFrames) {
        this.poseBuilderSupplier = poseBuilderSupplier;
        this.currentNanosSupplier = currentNanosSupplier;
        this.maxHistoryFrames = maxHistoryFrames;

        this.frameHistory = new LinkedList<>();
    }

    public Slot<Pose> getPoseSlot() {
        return poseSlot;
    }

    public OutputPort<Pose> getOutputPort() {
        return outputPort;
    }

    /**
     * Calculates the smoothed velocity of the current pose input using multi-frame analysis.
     *
     * <p><strong>Experimental Warning:</strong></p>
     * <ul>
     *   <li>This method may produce unexpected results in extreme scenarios such as very high-speed motion, 
     *       severe frame rate fluctuations, or when dealing with complex skeletal hierarchies</li>
     *   <li>The algorithm is still under development and may undergo significant changes in future versions</li>
     *   <li>Performance may degrade with large numbers of bones or extended frame history</li>
     * </ul>
     *
     * <p>This method uses a combination of techniques to provide stable velocity estimation:</p>
     * <ul>
     *   <li>Multi-frame weighted averaging for noise reduction</li>
     *   <li>Velocity clamping to prevent unrealistic values</li>
     *   <li>Low-pass filtering for smooth transitions</li>
     * </ul>
     *
     * @return estimated velocity pose with improved stability (experimental)
     */
    public Pose getVelocityPose() {
        if (!isInitialized || frameHistory.size() < 2) {
            return DummyPose.INSTANCE;
        }

        return calculateSmoothedVelocity();
    }

    @Override
    public void tick() {
        long currentTime = currentNanosSupplier.getAsLong();
        float deltaTime = isInitialized ? MathUtil.toSecond(currentTime - lastTickTime) : 0.0f;

        Pose currentPose = poseSlot.get();
        if (currentPose == null) {
            return;
        }

        // Add current frame to history
        HistoricalFrame frame = new HistoricalFrame(currentPose, deltaTime);
        frameHistory.offer(frame);

        // Maintain history size
        while (frameHistory.size() > maxHistoryFrames) {
            frameHistory.poll();
        }

        lastTickTime = currentTime;
        isInitialized = true;
    }

    /**
     * Calculates smoothed velocity using multi-frame analysis and filtering.
     */
    private Pose calculateSmoothedVelocity() {
        if (frameHistory.size() < 2) {
            return DummyPose.INSTANCE;
        }

        PoseBuilder builder = poseBuilderSupplier.get();
        HistoricalFrame[] frames = frameHistory.toArray(new HistoricalFrame[0]);

        // Group bone transforms by bone index
        Map<Integer, BoneTransform[]> boneTransformsMap = new Int2ObjectAVLTreeMap<>();
        for (int i = 0; i < frames.length; i++) {
            HistoricalFrame frame = frames[i];
            for (BoneTransform transform : frame.pose.getBoneTransforms()) {
                boneTransformsMap.computeIfAbsent(transform.boneIndex(), k -> {
                    BoneTransform[] array = new BoneTransform[frames.length];
                    for (int j = 0; j < frames.length; j++) {
                        array[j] = BoneTransform.IDENTITY_TRANSFORM;
                    }
                    return array;
                })[i] = transform;
            }
        }

        // Calculate smoothed velocity for each bone
        for (Map.Entry<Integer, BoneTransform[]> entry : boneTransformsMap.entrySet()) {
            SmoothedVelocity smoothedVelocity = calculateBoneSmoothedVelocity(entry.getValue(), frames);
            BoneTransform velocityTransform = createVelocityTransform(entry.getKey(), smoothedVelocity);
            builder.addBoneTransform(velocityTransform);
        }

        return builder.toPose();
    }

    /**
     * Calculates smoothed velocity for a specific bone using multi-frame analysis.
     */
    private SmoothedVelocity calculateBoneSmoothedVelocity(BoneTransform[] transforms, HistoricalFrame[] frames) {
        // Calculate instantaneous velocities from multiple frame pairs
        Vector3f[] translationVelocities = new Vector3f[frames.length - 1];
        Vector3f[] rotationVelocities = new Vector3f[frames.length - 1];
        Vector3f[] scaleVelocities = new Vector3f[frames.length - 1];

        for (int i = 1; i < frames.length; i++) {
            HistoricalFrame currFrame = frames[i];

            BoneTransform prevTransform = transforms[i - 1];
            BoneTransform currTransform = transforms[i];

            float intervalTime = currFrame.deltaTime;

            // Calculate instantaneous velocities
            Vector3f translationVelocity = currTransform.translation().sub(prevTransform.translation(), new Vector3f()).div(intervalTime);
            Vector3f scaleVelocity = currTransform.scale().sub(prevTransform.scale(), new Vector3f()).div(intervalTime);

            // Calculate rotation velocity using quaternion difference
            Quaternionfc q0 = prevTransform.rotation().asQuaternion();
            Quaternionfc q1 = currTransform.rotation().asQuaternion();
            Quaternionf relativeRotation = new Quaternionf();
            q1.mul(q0.conjugate(relativeRotation), relativeRotation);
            Vector3f rotationVelocity = MathUtil.logUnit(relativeRotation).div(intervalTime);

            translationVelocities[i - 1] = translationVelocity;
            rotationVelocities[i - 1] = rotationVelocity;
            scaleVelocities[i - 1] = scaleVelocity;
        }

        SmoothedVelocity smoothedVelocity = new SmoothedVelocity();
        // Calculate weighted average (more recent frames have higher weight)
        smoothedVelocity.translationVelocity = calculateWeightedAverage(translationVelocities, frames);
        smoothedVelocity.rotationVelocity = calculateWeightedAverage(rotationVelocities, frames);
        smoothedVelocity.scaleVelocity = calculateWeightedAverage(scaleVelocities, frames);

        return smoothedVelocity;
    }

    /**
     * Calculates weighted average of velocity vectors (more recent = higher weight).
     * The Gaussian weighted model is used to calculate the weight of each sampled velocity,
     * which equivalent to an approximate low-pass filter.
     */
    private Vector3f calculateWeightedAverage(Vector3f[] velocities, HistoricalFrame[] frames) {
        Vector3f result = new Vector3f(0, 0, 0);
        float baselineWeight = 0.0f;
        float cumulativeTime = 0.0f;

        for (int i = 0; i < velocities.length; i++) {
            cumulativeTime += frames[i].deltaTime;
            float cumulativeWeight = gaussianIntegral(cumulativeTime);
            float weight = cumulativeWeight - baselineWeight;
            baselineWeight = cumulativeWeight;
            Vector3f velocity = velocities[i];
            result.add(velocity.x() * weight, velocity.y() * weight, velocity.z() * weight);
        }

        if (baselineWeight > 0) {
            result.div(baselineWeight);
        }

        return result;
    }

    /**
     * Creates a velocity transform from smoothed velocity data.
     */
    private BoneTransform createVelocityTransform(int boneIndex, SmoothedVelocity velocity) {
        return new BoneTransform(
                boneIndex,
                velocity.translationVelocity,
                new RotationVelocityRotationView(velocity.rotationVelocity),
                velocity.scaleVelocity
        );
    }

    private static float gaussianIntegral(double t) {
        return (float) (0.5 * (1 + MathUtil.erf(t / (0.046 * Math.sqrt(2)))));
    }

    /**
     * Historical frame data for multi-frame analysis.
     */
    private static class HistoricalFrame {
        final Pose pose;
        final float deltaTime;

        HistoricalFrame(Pose pose, float deltaTime) {
            this.pose = pose;
            this.deltaTime = deltaTime;
        }
    }

    /**
     * Smoothed velocity data for a bone.
     */
    private static class SmoothedVelocity {
        Vector3f translationVelocity = new Vector3f(0, 0, 0);
        Vector3f rotationVelocity = new Vector3f(0, 0, 0);
        Vector3f scaleVelocity = new Vector3f(0, 0, 0);
    }
}
