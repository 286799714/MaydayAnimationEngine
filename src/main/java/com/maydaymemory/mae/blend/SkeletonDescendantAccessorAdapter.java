package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.Skeleton;
import it.unimi.dsi.fastutil.ints.IntArrayList;

import java.util.Collection;

public class SkeletonDescendantAccessorAdapter implements SkeletonDescendantAccessor {
    private final Skeleton skeleton;

    public SkeletonDescendantAccessorAdapter(Skeleton skeleton) {
        this.skeleton = skeleton;
    }

    /**
     * Returns a collection of bone indices that are descendants of the specified root bone index,
     * up to a given depth in the skeleton hierarchy.
     *
     * <p>This method performs a breadth-first traversal starting from the specified root bone.
     * It collects all child bones level by level, up to the specified depth.
     *
     * @param rootBoneIndex the index of the root bone from which to begin traversal
     * @param depth the number of hierarchical levels to traverse down from the root
     * @return a {@code Collection<Integer>} containing the indices of all descendant bones,
     *         including the root bone itself, up to the specified depth
     */
    @Override
    public Collection<Integer> getDescendantBoneIndices(int rootBoneIndex, int depth) {
        IntArrayList list = new IntArrayList();
        list.add(rootBoneIndex);
        int marker = 0;
        for (int i = 0; i < depth; i++) {
            int size = list.size();
            for (int j = marker; j < size; j++) {
                list.addAll(skeleton.getChildren(list.getInt(j)));
            }
            marker = size;
        }
        return list;
    }
}
