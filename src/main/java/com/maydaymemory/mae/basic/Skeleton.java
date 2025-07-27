package com.maydaymemory.mae.basic;

import java.util.Collection;

/**
 * Represents a hierarchical skeleton structure.A skeleton consists of a set of bones organized
 * in a parent-child relationship.
 * <p>
 * This interface allows querying bone hierarchy and applying animation poses to the skeleton.
 * </p>
 */
public interface Skeleton {

    /**
     * Returns the collection of child bone indices for a given bone.
     *
     * @param i the index of the parent bone
     * @return a collection of indices representing the children of the specified bone
     */
    Collection<Integer> getChildren(int i);

    /**
     * Returns the index of the parent bone for a given bone.
     *
     * @param i the index of the bone
     * @return the index of the parent bone; returns -1 if the bone is the root or has no parent
     */
    int getFather(int i);

    /**
     * Applies the specified pose to the skeleton.
     *
     * @param pose the pose to apply to the skeleton
     */
    void applyPose(Pose pose);

    /**
     * Returns the current pose of the skeleton.
     *
     * @return the current pose of the skeleton
     */
    Pose getPose();

    /**
     * Returns the bind pose of the skeleton.
     * <p>
     * The bind pose, also known as the rest pose or T-pose, represents the default,
     * unanimated state of the skeleton. It serves as the reference pose for skinning,
     * animation blending, and inverse bind matrix calculations.
     * </p>
     *
     * @return the bind pose associated with this skeleton
     */
    Pose getBindPose();
}

