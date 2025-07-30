package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.*;
import com.maydaymemory.mae.util.MathUtil;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Iterator;
import java.util.function.Supplier;

public class CubicHermiteInterpolatorBlender implements KinematicInterpolatorBlender{
    private final BoneTransformFactory boneTransformFactory;
    private final Supplier<PoseBuilder> poseBuilderSupplier;

    public CubicHermiteInterpolatorBlender(BoneTransformFactory boneTransformFactory,
                                           Supplier<PoseBuilder> poseBuilderSupplier) {
        this.boneTransformFactory = boneTransformFactory;
        this.poseBuilderSupplier = poseBuilderSupplier;
    }

    @Override
    public Pose blend(Pose base, Pose baseVelocity, Pose target, Pose targetVelocity, float time, float duration) {
        float weight = time / duration;
        float w2 = weight * weight;
        float w3 = w2 * weight;
        float h1 = 2 * w3 - 3 * w2 + 1;
        float h2 = w3 - 2 * w2 + weight;
        float h3 = -2 * w3 + 3 * w2;
        float h4 = w3 - w2;
        @SuppressWarnings("unchecked")
        Iterator<BoneTransform>[] iterators = new Iterator[] {
                base.getBoneTransforms().iterator(),
                baseVelocity.getBoneTransforms().iterator(),
                target.getBoneTransforms().iterator(),
                targetVelocity.getBoneTransforms().iterator()
        };
        boolean[] nextFlag = new boolean[]{true, true, true, true};
        BoneTransform[] transforms = new BoneTransform[4];
        BoneTransform[] transformsToCalculate = new BoneTransform[4];
        PoseBuilder builder = poseBuilderSupplier.get();
        while (iterators[0].hasNext() || iterators[1].hasNext() || iterators[2].hasNext() || iterators[3].hasNext()) {
            for (int i = 0; i < 4; i++) {
                if (nextFlag[i]) {
                    transforms[i] = iterators[i].hasNext() ? iterators[i].next() : null;
                }
            }
            int index = Integer.MAX_VALUE;
            for (BoneTransform transform : transforms) {
                if (transform == null) {
                    continue;
                }
                index = Math.min(index, transform.boneIndex());
            }
            for (int i = 0; i < 4; i++) {
                BoneTransform transform = transforms[i];
                if (transform != null && transform.boneIndex() == index) {
                    transformsToCalculate[i] = transform;
                    nextFlag[i] = true;
                } else {
                    transformsToCalculate[i] = (i & 1) == 0 ? BoneTransform.IDENTITY_TRANSFORM : BoneTransform.IDENTITY_VELOCITY;
                    nextFlag[i] = false;
                }
            }

            Vector3f newTransition = blend(
                    transformsToCalculate[0].translation(),
                    transformsToCalculate[1].translation(),
                    transformsToCalculate[2].translation(),
                    transformsToCalculate[3].translation(),
                    h1, h2, h3, h4, time
            );
            Vector3f newScale = blend(
                    transformsToCalculate[0].scale(),
                    transformsToCalculate[1].scale(),
                    transformsToCalculate[2].scale(),
                    transformsToCalculate[3].scale(),
                    h1, h2, h3, h4, time
            );

            Quaternionfc q0 = transformsToCalculate[0].rotation().asQuaternion();
            Quaternionfc q1 = transformsToCalculate[2].rotation().asQuaternion();
            Quaternionf relativeRotation = new Quaternionf();
            q1.mul(q0.conjugate(relativeRotation), relativeRotation); // relativeRotation = q1 * q0^-1
            Vector3f relativeRotationVector = MathUtil.logUnit(relativeRotation);
            Vector3f blendedRotationVector = blend(
                    BoneTransform.ZERO_VECTOR,
                    transformsToCalculate[1].rotation().asEulerAngle(),
                    relativeRotationVector,
                    transformsToCalculate[3].rotation().asEulerAngle(),
                    h1, h2, h3, h4, time
            );
            Quaternionf blendedRotation = MathUtil.exp(blendedRotationVector).mul(q0);
            builder.addBoneTransform(boneTransformFactory.createBoneTransform(index, newTransition, blendedRotation, newScale));
        }
        return builder.toPose();
    }

    private static Vector3f blend(Vector3fc p0, Vector3fc v0, Vector3fc p1, Vector3fc v1,
                                  float h1, float h2, float h3, float h4, float time) {
        Vector3f alphaP0 = v0.mul(time, new Vector3f());
        Vector3f alphaP1 = v1.mul(time, new Vector3f());
        float x = p0.x() * h1 + alphaP0.x * h2 + p1.x() * h3 + alphaP1.x * h4;
        float y = p0.y() * h1 + alphaP0.y * h2 + p1.y() * h3 + alphaP1.y * h4;
        float z = p0.z() * h1 + alphaP0.z * h2 + p1.z() * h3 + alphaP1.z * h4;
        return new Vector3f(x, y, z);
    }
}
