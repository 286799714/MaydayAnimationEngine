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

    /**
     * Computes the analytical velocity (derivative) of the Hermite curve at the given time.
     * This is the CORRECT way to get velocity during interpolation, rather than finite differencing.
     * 
     * @param base the starting pose
     * @param baseVelocity the velocity at the starting pose
     * @param target the ending pose
     * @param targetVelocity the velocity at the ending pose
     * @param time current time
     * @param duration total duration
     * @return the analytical velocity at the current time
     */
    public Pose blendVelocity(Pose base, Pose baseVelocity, Pose target, Pose targetVelocity, float time, float duration) {
        float weight = time / duration;
        float w2 = weight * weight;
        
        // Derivatives of Hermite basis functions with respect to weight
        // h1' = 6w² - 6w
        // h2' = 3w² - 4w + 1
        // h3' = -6w² + 6w
        // h4' = 3w² - 2w
        float dh1 = 6 * w2 - 6 * weight;
        float dh2 = 3 * w2 - 4 * weight + 1;
        float dh3 = -6 * w2 + 6 * weight;
        float dh4 = 3 * w2 - 2 * weight;
        
        // Note: dP/dt = (dP/dw) * (dw/dt) = (dP/dw) / duration
        // So we need to divide by duration, which is handled in blendVelocity() helper
        
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

            Vector3f velocityTranslation = blendVelocity(
                    transformsToCalculate[0].translation(),
                    transformsToCalculate[1].translation(),
                    transformsToCalculate[2].translation(),
                    transformsToCalculate[3].translation(),
                    dh1, dh2, dh3, dh4, duration
            );
            Vector3f velocityScale = blendVelocity(
                    transformsToCalculate[0].scale(),
                    transformsToCalculate[1].scale(),
                    transformsToCalculate[2].scale(),
                    transformsToCalculate[3].scale(),
                    dh1, dh2, dh3, dh4, duration
            );

            Vector3f velocityRotation = blendVelocity(
                    BoneTransform.ZERO_VECTOR,
                    transformsToCalculate[1].rotation().asEulerAngle(),
                    MathUtil.logUnit(
                        transformsToCalculate[2].rotation().asQuaternion().mul(
                            transformsToCalculate[0].rotation().asQuaternion().conjugate(new Quaternionf()), 
                            new Quaternionf()
                        )
                    ),
                    transformsToCalculate[3].rotation().asEulerAngle(),
                    dh1, dh2, dh3, dh4, duration
            );
            
            builder.addBoneTransform(new BoneTransform(
                    index, 
                    velocityTranslation, 
                    new RotationVelocityRotationView(velocityRotation), 
                    velocityScale
            ));
        }
        return builder.toPose();
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
                    h1, h2, h3, h4, duration
            );
            Vector3f newScale = blend(
                    transformsToCalculate[0].scale(),
                    transformsToCalculate[1].scale(),
                    transformsToCalculate[2].scale(),
                    transformsToCalculate[3].scale(),
                    h1, h2, h3, h4, duration
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
                    h1, h2, h3, h4, duration
            );
            Quaternionf blendedRotation = MathUtil.exp(blendedRotationVector).mul(q0);
            builder.addBoneTransform(boneTransformFactory.createBoneTransform(index, newTransition, blendedRotation, newScale));
        }
        return builder.toPose();
    }

    private static Vector3f blend(Vector3fc p0, Vector3fc v0, Vector3fc p1, Vector3fc v1,
                                  float h1, float h2, float h3, float h4, float duration) {
        Vector3f adjustedV0 = new Vector3f(v0).mul(duration);
        Vector3f adjustedV1 = new Vector3f(v1).mul(duration);
        float x = p0.x() * h1 + adjustedV0.x * h2 + p1.x() * h3 + adjustedV1.x * h4;
        float y = p0.y() * h1 + adjustedV0.y * h2 + p1.y() * h3 + adjustedV1.y * h4;
        float z = p0.z() * h1 + adjustedV0.z * h2 + p1.z() * h3 + adjustedV1.z * h4;
        return new Vector3f(x, y, z);
    }
    
    /**
     * Computes the derivative of the Hermite blend function.
     * 
     * @param p0 starting position
     * @param v0 starting velocity
     * @param p1 ending position
     * @param v1 ending velocity
     * @param dh1 derivative of h1 with respect to weight
     * @param dh2 derivative of h2 with respect to weight
     * @param dh3 derivative of h3 with respect to weight
     * @param dh4 derivative of h4 with respect to weight
     * @param duration total duration
     * @return velocity vector (dP/dt)
     */
    private static Vector3f blendVelocity(Vector3fc p0, Vector3fc v0, Vector3fc p1, Vector3fc v1,
                                          float dh1, float dh2, float dh3, float dh4, float duration) {
        // dP/dw = dh1*p0 + dh2*v0*duration + dh3*p1 + dh4*v1*duration
        // dP/dt = (dP/dw) / duration
        Vector3f adjustedV0 = new Vector3f(v0).mul(duration);
        Vector3f adjustedV1 = new Vector3f(v1).mul(duration);
        float x = (p0.x() * dh1 + adjustedV0.x * dh2 + p1.x() * dh3 + adjustedV1.x * dh4) / duration;
        float y = (p0.y() * dh1 + adjustedV0.y * dh2 + p1.y() * dh3 + adjustedV1.y * dh4) / duration;
        float z = (p0.z() * dh1 + adjustedV0.z * dh2 + p1.z() * dh3 + adjustedV1.z * dh4) / duration;
        return new Vector3f(x, y, z);
    }
}
