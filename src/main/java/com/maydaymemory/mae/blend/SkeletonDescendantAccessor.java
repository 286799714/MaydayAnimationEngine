package com.maydaymemory.mae.blend;

import java.util.Collection;

public interface SkeletonDescendantAccessor {
    /**
     * Return a collection of indices of descendants within a depth,
     * including the root bone itself. When depth is 1, the collection will only contain
     * the root bone itself. Depth cannot be less than 1.
     *
     * @param rootBoneIndex the index of the root bone.
     * @return a set of all descendant bone indices (including the root).
     * @throws IllegalArgumentException when depth is less than 1.
     * @throws IndexOutOfBoundsException when rootBoneIndex is not contained in skeleton
     */
    Collection<Integer> getDescendantBoneIndices(int rootBoneIndex, int depth);
}
