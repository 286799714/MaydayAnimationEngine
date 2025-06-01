package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.BoneTransformFactory;
import com.maydaymemory.mae.basic.DummyPose;
import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.basic.PoseBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.TreeSet;
import java.util.function.Supplier;

/**
 * A SimpleInterpolatorBlender is built in to implement interpolation
 */
public class SimpleBlendSpace1D implements BlendSpace1D {
    private final TreeSet<SamplerPoint> points = new TreeSet<>();
    private final Int2ObjectOpenHashMap<SamplerPoint> pointMap = new Int2ObjectOpenHashMap<>();
    private final SimpleInterpolatorBlender interpolatorBlender;

    public SimpleBlendSpace1D(BoneTransformFactory boneTransformFactory,
                              Supplier<PoseBuilder> poseBuilderSupplier) {
        this.interpolatorBlender = new SimpleInterpolatorBlender(boneTransformFactory, poseBuilderSupplier);
    }

    private static class SamplerPoint implements Comparable<SamplerPoint> {
        private final float position;
        private Pose pose;

        public SamplerPoint(float position){
            this.position = position;
        }

        public void setPose(Pose pose) {
            this.pose = pose;
        }

        public Pose getPose() {
            return pose;
        }

        public float getPosition() {
            return position;
        }

        @Override
        public int compareTo(SamplerPoint o) {
            return Float.compare(position, o.position);
        }
    }

    @Override
    public void setSamplerPosition(int index, float position) {
        pointMap.compute(index, (i, oldPoint) -> {
            SamplerPoint newPoint = new SamplerPoint(position);
            if (oldPoint != null) {
                points.remove(oldPoint);
                newPoint.pose = oldPoint.pose;
            }
            points.add(newPoint);
            return newPoint;
        });
    }

    @Override
    public void setSamplerPose(int index, Pose pose) {
        pointMap.compute(index, (i, point) -> {
            if (point == null) {
                point = new SamplerPoint(0); // position doesn't matter here because it is not into the TreeMap points
            }
            point.setPose(pose);
            return point;
        });
    }

    @Override
    public Pose blend(float position) {
        if (points.isEmpty()) {
            throw new IllegalStateException("There is no sample point");
        }

        SamplerPoint dummy = new SamplerPoint(position);
        SamplerPoint right = points.ceiling(dummy);
        SamplerPoint left = points.lower(dummy);

        if (left != null && right != null) {
            Pose p1 = left.getPose();
            Pose p2 = right.getPose();

            if (p1 == null) {
                p1 = DummyPose.INSTANCE;
            }
            if (p2 == null) {
                p2 = DummyPose.INSTANCE;
            }

            float alpha = (position - left.getPosition()) / (right.getPosition() - left.getPosition());

            return interpolatorBlender.blend(p1, p2, alpha);
        } else if (left != null) {
            return left.getPose();
        } else if (right != null) {
            return right.getPose();
        }

        throw new IllegalStateException("This code should never be reached.");
    }
}
