package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.Pose;

/**
 * Interface for layered pose blending, similar to Unreal Engine's Layered Per Bone Blend node.
 * <p>
 * This interface defines a strategy to blend input pose over a base pose,
 * how close each bone's transform is to the input pose depends on the weight of the corresponding bone in the <code>layer</code>.
 * <p>
 */
public interface LayeredBlender {
    Pose blend(Pose basePose, Pose inputPose, LayerBlend layer);
}
