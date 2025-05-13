package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.Skeleton;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.ints.*;

import java.util.Collection;

public class SkeletonBaseLayerBlend implements LayerBlend{
    private final Skeleton skeleton;
    private final FloatArrayList controlPoints = new FloatArrayList();
    private final Int2ObjectOpenHashMap<IntArraySet> controlBy = new Int2ObjectOpenHashMap<>();

    public SkeletonBaseLayerBlend(Skeleton skeleton) {
        this.skeleton = skeleton;
    }

    /**
     * Adds a control point with an initial weight of 0 for the given root bone and depth.
     * <p>
     * This is a convenience method equivalent to calling
     * {@code addControlPoint(boneIndex, depth, 0f)}.
     *
     * @param boneIndex the index of root bone controlled by this control point.
     * @param depth the maximum depth of descendant bones to be affected
     * @return the index of the newly added control point
     * @throws IllegalArgumentException if there is no bone with the corresponding index in skeleton
     * @see #addControlPoint(int, int, float)
     */
    public int addControlPoint(int boneIndex, int depth) {
        return this.addControlPoint(boneIndex, depth, 0f);
    }

    /**
     * Adds a control point with a specified initial weight for the given root bone and depth.
     * <p>
     * This method identifies all descendant bones of the specified bone index
     * up to the given depth, and marks them as controlled by the new control point.
     * If there is no bone with the corresponding index in skeleton,
     * the method throws an exception.
     *
     * @param rootBoneIndex the index of root bone controlled by this control point.
     * @param depth the maximum depth of descendant bones to be affected
     * @param initialWeight the initial weight value assigned to the new control point
     * @return the index of the newly added control point
     * @throws IllegalArgumentException if there is no bone with the corresponding index in skeleton
     * @throws IllegalArgumentException if {@code initialWeight} is outside the range 0 to 1
     */
    public int addControlPoint(int rootBoneIndex, int depth, float initialWeight) {
        Collection<Integer> descendantBoneIndices = skeleton.getDescendantBoneIndices(rootBoneIndex, depth);
        if (descendantBoneIndices.isEmpty()) {
            throw new IllegalArgumentException("Bone index not found: " + rootBoneIndex);
        }

        if (initialWeight < 0 || initialWeight > 1) {
            throw new IllegalArgumentException("Initial weight must be between 0 and 1");
        }

        int controlPointIndex = controlPoints.size();
        controlPoints.add(initialWeight);

        for (int i : descendantBoneIndices) {
            controlBy.compute(i, (k, set) -> {
                if (set == null) {
                    set = new IntArraySet(new int[]{controlPointIndex});
                } else {
                    set.add(controlPointIndex);
                }
                return set;
            });
        }

        return controlPointIndex;
    }

    /**
     * Sets the weight value for the specified control point.
     * <p>
     * The weight must be a value between 0 and 1, inclusive.
     * If the value is outside of this range, an {@link IllegalArgumentException} is thrown.
     *
     * @param controlPointIndex the index of the control point to modify
     * @param weight the new weight value to assign, must be in the range [0, 1]
     * @throws IllegalArgumentException if the weight is less than 0 or greater than 1
     */
    public void setControlPointWeight(int controlPointIndex, float weight) {
        if (weight < 0 || weight > 1) {
            throw new IllegalArgumentException("Weight must be between 0 and 1");
        }
        controlPoints.set(controlPointIndex, weight);
    }

    @Override
    public float getWeight(int boneIndex) {
        IntArraySet points = controlBy.get(boneIndex);
        if (points == null) {
            return 0f;
        }
        float weight = 0;
        for (int i : points) {
            weight = Math.max(weight, controlPoints.getFloat(i));
        }
        return weight;
    }
}
