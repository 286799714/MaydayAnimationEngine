package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.*;
import com.maydaymemory.mae.util.triangulation.DelaunayTriangulator;
import com.maydaymemory.mae.util.triangulation.NotEnoughPointsException;
import com.maydaymemory.mae.util.triangulation.SamplerPoint;
import com.maydaymemory.mae.util.triangulation.WeightCalculatingResult;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.joml.*;

import java.lang.Math;
import java.util.Iterator;
import java.util.function.Supplier;

/**
 * An implementation of BlendSpace2D that uses Delaunay triangulation to interpolate between sample poses.
 * When the input position is outside the convex hull of sample points, the blend result is clamped to the nearest edge.
 *
 * <p>Typical usage: blending walk animations based on player velocity (see BlendSpace2D for example).</p>
 *
 * <b>Important:</b> You <b>must</b> call {@link #triangulate()} after setting all sample points and before calling {@link #blend(float, float)}.
 * Failing to do so will result in an {@link IllegalStateException}.
 */
public class ClampToEdgeBlendSpace2D implements BlendSpace2D {
    private DelaunayTriangulator<MySamplerPoint> triangulator;
    private final Int2ObjectOpenHashMap<MySamplerPoint> pointMap = new Int2ObjectOpenHashMap<>();
    private final Supplier<PoseBuilder> poseBuilderSupplier;
    private final BoneTransformFactory boneTransformFactory;

    public ClampToEdgeBlendSpace2D(BoneTransformFactory boneTransformFactory,
                                   Supplier<PoseBuilder> poseBuilderSupplier) {
        this.boneTransformFactory = boneTransformFactory;
        this.poseBuilderSupplier = poseBuilderSupplier;
    }

    /**
     * Sets the position of a sample point at the given index.
     * Throws an exception if the blend space has already been triangulated.
     *
     * @param index the index of the sample point
     * @param x the X coordinate
     * @param y the Y coordinate
     * @throws IllegalStateException if triangulation has already occurred
     */
    @Override
    public void setSamplerPosition(int index, float x, float y) {
        if (triangulator != null) {
            throw new IllegalStateException("This Blend Space 2D has been triangulated.");
        }
        pointMap.compute(index, (i, point) -> {
            if (point != null) {
                point.setPosition(new Vector2f(x, y));
            } else {
                point = new MySamplerPoint(new Vector2f(x, y));
            }
            return point;
        });
    }

    /**
     * Triangulates the sample points using Delaunay triangulation.
     * Must be called before blending. Throws if not enough points.
     *
     * @throws IllegalStateException if already triangulated or not enough points(at least 3)
     */
    public void triangulate() {
        if (triangulator != null) {
            throw new IllegalStateException("This Blend Space 2D has been triangulated.");
        }
        triangulator = new DelaunayTriangulator<>(pointMap.values() );//.stream().filter((point) -> point.position == null).toList());
        try {
            triangulator.triangulate();
        } catch (NotEnoughPointsException e) {
            triangulator = null;
            throw new IllegalStateException(e);
        }
    }

    /**
     * Sets the pose for a sample point at the given index.
     *
     * @param index the index of the sample point
     * @param pose the pose to assign
     */
    @Override
    public void setSamplerPose(int index, Pose pose) {
        pointMap.compute(index, (i, point) -> {
            if (point == null) {
                point = new MySamplerPoint(null);
            }
            point.setPose(pose);
            return point;
        });
    }

    /**
     * Blends poses based on the input (x, y) position.
     * If the input is outside the convex hull, the result is clamped to the nearest edge.
     *
     * @param x the X coordinate of the blend input
     * @param y the Y coordinate of the blend input
     * @return the blended pose
     * @throws IllegalStateException if triangulation has not been performed
     */
    @Override
    public Pose blend(float x, float y) {
        if (triangulator == null) {
            throw new IllegalStateException("This Blend Space 2D has NOT been triangulated.");
        }
        WeightCalculatingResult<MySamplerPoint> result = triangulator.calculateWeightsClampToEdge(new Vector2f(x, y));
        Pose pose1 = result.a().getPose();
        Pose pose2 = result.b().getPose();
        Pose pose3 = result.c().getPose();
        if (pose1 == null) {
            pose1 = DummyPose.INSTANCE;
        }
        if (pose2 == null) {
            pose2 = DummyPose.INSTANCE;
        }
        if (pose3 == null) {
            pose3 = DummyPose.INSTANCE;
        }
        float alpha = result.alpha();
        float beta = result.beta();
        float gamma = result.gamma();

        PoseBuilder poseBuilder = poseBuilderSupplier.get();

        Iterator<BoneTransform> it1 = pose1.getBoneTransforms().iterator();
        Iterator<BoneTransform> it2 = pose2.getBoneTransforms().iterator();
        Iterator<BoneTransform> it3 = pose3.getBoneTransforms().iterator();

        BoneTransform t1 = it1.hasNext() ? it1.next() : null;
        BoneTransform t2 = it2.hasNext() ? it2.next() : null;
        BoneTransform t3 = it3.hasNext() ? it3.next() : null;
        // create a bone transform which do nothing as a fallback transform. The bone index does not matter.
        BoneTransform identityTransform = BoneTransform.IDENTITY_TRANSFORM;

        int index1, index2, index3, minIndex;
        while (t1 != null || t2 != null || t3 != null) {
            index1 = t1 == null ? Integer.MAX_VALUE : t1.boneIndex();
            index2 = t2 == null ? Integer.MAX_VALUE : t2.boneIndex();
            index3 = t3 == null ? Integer.MAX_VALUE : t3.boneIndex();
            minIndex = Math.min(index1, Math.min(index2, index3));
            BoneTransform transform1 = index1 == minIndex ? t1 : identityTransform;
            BoneTransform transform2 = index2 == minIndex ? t2 : identityTransform;
            BoneTransform transform3 = index3 == minIndex ? t3 : identityTransform;
            Vector3f newTranslation = blendScalar(transform1.translation(), transform2.translation(), transform3.translation(),
                    alpha, beta, gamma);
            Quaternionf newRotation = blendQuaternion(
                    transform1.rotation().asQuaternion(),
                    transform2.rotation().asQuaternion(),
                    transform3.rotation().asQuaternion(),
                    alpha, beta, gamma
            );
            Vector3f newScale = blendScalar(transform1.scale(), transform2.scale(), transform3.scale(),
                    alpha, beta, gamma);
            BoneTransform newBoneTransform = boneTransformFactory.createBoneTransform(minIndex, newTranslation, newRotation, newScale);
            poseBuilder.addBoneTransform(newBoneTransform);
            if (index1 == minIndex) {
                t1 = it1.hasNext() ? it1.next() : null;
            }
            if (index2 == minIndex) {
                t2 = it2.hasNext() ? it2.next() : null;
            }
            if (index3 == minIndex) {
                t3 = it3.hasNext() ? it3.next() : null;
            }
        }
        return poseBuilder.toPose();
    }

    private Vector3f blendScalar(Vector3fc a, Vector3fc b, Vector3fc c,
                                 float alpha, float beta, float gamma) {
        Vector3f result = new Vector3f();
        a.mul(alpha, result);
        result.add(b.x() * beta, b.y() * beta, b.z() * beta);
        result.add(c.x() * gamma, c.y() * gamma, c.z() * gamma);
        return result;
    }

    private Quaternionf blendQuaternion(Quaternionfc a, Quaternionfc b, Quaternionfc c,
                                        float alpha, float beta, float gamma) {
        float ax = a.x();
        float ay = a.y();
        float az = a.z();
        float aw = a.w();
        float bx = b.x();
        float by = b.y();
        float bz = b.z();
        float bw = b.w();
        float cx = c.x();
        float cy = c.y();
        float cz = c.z();
        float cw = c.w();
        // 确保 q2、q3 沿最近路径混合（四元数双解性处理）
        if (ax * bx + ay * by + az * bz + aw * bw < 0) {
            bx = -bx;
            by = -by;
            bz = -bz;
            bw = -bw;
        }
        if (ax * cx + ay * cy + az * cz + aw * cw < 0){
            cx = -cx;
            cy = -cy;
            cz = -cz;
            cw = -cw;
        }

        // 加权相加
        Quaternionf blended = new Quaternionf(
                ax * alpha + bx * beta + cx * gamma,
                ay * alpha + by * beta + cy * gamma,
                az * alpha + bz * beta + cz * gamma,
                aw * alpha + bw * beta + cw * gamma
        );

        // 归一化（等价于正交化）
        blended.normalize();
        return blended;
    }

    private static class MySamplerPoint implements SamplerPoint {
        private Vector2fc position;
        private Pose pose;

        public MySamplerPoint(Vector2fc position) {
            this.position = position;
        }

        public Pose getPose() {
            return pose;
        }

        public void setPose(Pose pose) {
            this.pose = pose;
        }

        @Override
        public float x() {
            return position.x();
        }

        @Override
        public float y() {
            return position.y();
        }

        @Override
        public Vector2fc position() {
            return position;
        }

        public void setPosition(Vector2fc position) {
            this.position = position;
        }
    }
}