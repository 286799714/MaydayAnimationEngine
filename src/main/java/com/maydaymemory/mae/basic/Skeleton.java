package com.maydaymemory.mae.basic;

import java.util.Collection;

/**
 * Represents the structure of a skeletal hierarchy used for animation.
 * Provides information about parent-child bone relationships.
 */
public interface Skeleton {

    /**
     * Returns the number of bones in the skeleton.
     *
     * @return the total number of bones.
     */
    int getBoneCount();

    /**
     * Returns the index of the parent of the given bone.
     *
     * @param boneIndex the index of the bone.
     * @return the index of the parent bone, or -1 if the bone is a root.
     */
    int getParentIndex(int boneIndex);

    /**
     * Returns a list of indices of the direct children of the given bone.
     *
     * @param boneIndex the index of the bone.
     * @return a list of child bone indices.
     */
    Collection<Integer> getChildren(int boneIndex);

    /**
     * Return a collection of indices of descendants within a depth,
     * including the root bone itself. When depth is 1, the collection will only contain
     * the root bone itself. Depth cannot be less than 1.
     *
     * @param rootBoneIndex the index of the root bone.
     * @return a set of all descendant bone indices (including the root).
     * @throws IllegalArgumentException when depth is less than 1.
     */
    Collection<Integer> getDescendantBoneIndices(int rootBoneIndex, int depth);
}
