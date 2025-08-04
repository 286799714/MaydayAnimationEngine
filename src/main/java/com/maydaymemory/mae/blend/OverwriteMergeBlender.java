package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.Pose;

/**
 * Taking the union of the Transforms of two Poses.
 * <p>When the bone index is the same, depending on the implementation,
 * one transform from the {@code base} pose or the {@code input} pose will be preserved.</p>
 */
public interface OverwriteMergeBlender {
    /**
     * Merge two poses' transform.
     * @param base the base pose
     * @param input the input pose
     * @return the merged pose
     */
    Pose blend(Pose base, Pose input);
}
