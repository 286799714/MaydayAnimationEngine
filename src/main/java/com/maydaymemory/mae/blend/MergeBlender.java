package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.Pose;

import java.util.List;

/**
 * Merge multiple poses.
 *
 * <p>When the bone index is the same, the transform from the rightmost pose will be preserved.</p>
 */
public interface MergeBlender {
    /**
     * Merge multiple poses.
     *
     * <p>When the bone index is the same, the transform from the rightmost pose will be preserved.</p>
     *
     * @param poses poses to merge.
     * @return the merged pose
     */
    Pose blend(List<Pose> poses);
}
