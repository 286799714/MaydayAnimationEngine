package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.BoneTransform;
import com.maydaymemory.mae.basic.BoneTransformFactory;
import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.basic.PoseBuilder;
import com.maydaymemory.mae.util.MathUtil;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Iterator;
import java.util.function.Supplier;

public class QuinticHermiteInterpolatorBlender implements KinematicInterpolatorBlender{
    private static final Vector3f ZERO_VECTOR = new Vector3f(0, 0, 0);
    private static final BoneTransformPair IDENTITY_TRANSFORM_PAIR = new BoneTransformPair(BoneTransform.IDENTITY_TRANSFORM, BoneTransform.IDENTITY_TRANSFORM);
    private final BoneTransformFactory boneTransformFactory;
    private final Supplier<PoseBuilder> poseBuilderSupplier;

    public QuinticHermiteInterpolatorBlender(BoneTransformFactory boneTransformFactory,
                                   Supplier<PoseBuilder> poseBuilderSupplier) {
        this.boneTransformFactory = boneTransformFactory;
        this.poseBuilderSupplier = poseBuilderSupplier;
    }

    @Override
    public Pose blend(Pose base, Pose deltaBaseContribution, Pose target, Pose deltaTargetContribution, float weight) {
        float w3 = weight * weight * weight;
        float w4 = w3 * weight;
        float w5 = w4 * weight;
        float h1 = 1 - 10 * w3 + 15 * w4 - 6 * w5;
        float h2 = weight - 6 * w3 + 8 * w4 - 3 * w5;
        float h3 = 10 * w3 - 15 * w4 + 6 * w5;
        float h4 = -4 * w3 + 7 * w4 - 3 * w5;

        Iterator<BoneTransform> baseIter = base.getBoneTransforms().iterator();
        Iterator<BoneTransform> deltaBaseIter = deltaBaseContribution.getBoneTransforms().iterator();
        Iterator<BoneTransform> targetIter = target.getBoneTransforms().iterator();
        Iterator<BoneTransform> deltaTargetIter = deltaTargetContribution.getBoneTransforms().iterator();
        BoneTransformPair basePair = nextAndCheck(baseIter, deltaBaseIter, "base");
        BoneTransformPair targetPair = nextAndCheck(targetIter, deltaTargetIter, "target");
        boolean baseIsIdentity = basePair.first.boneIndex() != -1;
        boolean targetIsIdentity = targetPair.first.boneIndex() != -1;
        PoseBuilder builder = poseBuilderSupplier.get();
        while (!baseIsIdentity || !targetIsIdentity) {
            BoneTransform t0, dt0, t1, dt1;
            if (!baseIsIdentity && !targetIsIdentity) {
                int cmp = basePair.first.compareTo(targetPair.first);
                if (cmp == 0) {
                    t0 = basePair.first;
                    dt0 = basePair.second;
                    t1 = targetPair.first;
                    dt1 = targetPair.second;
                    basePair = nextAndCheck(baseIter, deltaBaseIter, "base");
                    targetPair = nextAndCheck(targetIter, deltaTargetIter, "target");
                    baseIsIdentity = basePair.first.boneIndex() != -1;
                    targetIsIdentity = targetPair.first.boneIndex() != -1;
                } else if (cmp < 0) {
                    t0 = basePair.first;
                    dt0 = basePair.second;
                    t1 = BoneTransform.IDENTITY_TRANSFORM;
                    dt1 = BoneTransform.IDENTITY_TRANSFORM;
                    basePair = nextAndCheck(baseIter, deltaBaseIter, "base");
                    baseIsIdentity = basePair.first.boneIndex() != -1;
                } else {
                    t0 = BoneTransform.IDENTITY_TRANSFORM;
                    dt0 = BoneTransform.IDENTITY_TRANSFORM;
                    t1 = basePair.first;
                    dt1 = basePair.second;
                    targetPair = nextAndCheck(targetIter, deltaTargetIter, "target");
                    targetIsIdentity = targetPair.first.boneIndex() != -1;
                }
            } else if (!baseIsIdentity) {
                t0 = basePair.first;
                dt0 = basePair.second;
                t1 = BoneTransform.IDENTITY_TRANSFORM;
                dt1 = BoneTransform.IDENTITY_TRANSFORM;
                basePair = nextAndCheck(baseIter, deltaBaseIter, "base");
                baseIsIdentity = basePair.first.boneIndex() != -1;
            } else {
                t0 = BoneTransform.IDENTITY_TRANSFORM;
                dt0 = BoneTransform.IDENTITY_TRANSFORM;
                t1 = basePair.first;
                dt1 = basePair.second;
                targetPair = nextAndCheck(targetIter, deltaTargetIter, "target");
                targetIsIdentity = targetPair.first.boneIndex() != -1;
            }
            int index = Math.max(t0.boneIndex(), t1.boneIndex()); // identity transform's bone index is -1
            Vector3f newTransition = blend(t0.translation(), dt0.translation(), t1.translation(), dt1.translation(), h1, h2, h3, h4, weight);
            Vector3f newScale = blend(t0.scale(), dt0.scale(), t1.scale(), dt1.scale(), h1, h2, h3, h4, weight);

            Quaternionfc q0 = t0.rotation().asQuaternion();
            Quaternionfc q1 = t1.rotation().asQuaternion();
            Quaternionf relativeRotation = new Quaternionf();
            q1.mul(q0.conjugate(relativeRotation), relativeRotation);
            Vector3f relativeRotationVector = MathUtil.logUnit(relativeRotation);
            Vector3f blendedRotationVector = blend(ZERO_VECTOR, dt0.rotation().asEulerAngle(), relativeRotationVector, dt1.rotation().asEulerAngle(), h1, h2, h3, h4, weight);
            Quaternionf blendedRotation = MathUtil.exp(blendedRotationVector);
            builder.addBoneTransform(boneTransformFactory.createBoneTransform(index, newTransition, blendedRotation, newScale));
        }
        return builder.toPose();
    }

    private static Vector3f blend(Vector3fc p0, Vector3fc c0, Vector3fc p1, Vector3fc c1,
                                  float h1, float h2, float h3, float h4, float weight) {
        Vector3f alphaP0 = c0.mul(weight, new Vector3f());
        Vector3f alphaP1 = c1.mul(weight, new Vector3f());
        float x = p0.x() * h1 + alphaP0.x * h2 + p1.x() * h3 + alphaP1.x * h4;
        float y = p0.y() * h1 + alphaP0.y * h2 + p1.y() * h3 + alphaP1.y * h4;
        float z = p0.z() * h1 + alphaP0.z * h2 + p1.z() * h3 + alphaP1.z * h4;
        return new Vector3f(x, y, z);
    }

    private BoneTransformPair nextAndCheck(Iterator<BoneTransform> iter1, Iterator<BoneTransform> iter2, String poseType) {
        boolean hasNext = iter1.hasNext();
        if (hasNext != iter2.hasNext()) {
            throw new IllegalArgumentException("The number of bone transform of " + poseType + " and its delta pose does not match.");
        }
        if (hasNext) {
            BoneTransform transform1 = iter1.next();
            BoneTransform transform2 = iter2.next();
            if (transform1.boneIndex() != transform2.boneIndex()) {
                throw new IllegalArgumentException(String.format("The bone index of %s:%d does not match with its delta pose:%d.", poseType, transform1.boneIndex(), transform2.boneIndex()));
            }
            return new BoneTransformPair(transform1, transform2);
        } else {
            return IDENTITY_TRANSFORM_PAIR;
        }
    }

    private static class BoneTransformPair {
        private final BoneTransform first;
        private final BoneTransform second;

        public BoneTransformPair(BoneTransform first, BoneTransform second) {
            this.first = first;
            this.second = second;
        }
    }
}
