package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.Pose;

/**
 * Interface for blending two poses using a specified weight, 
 * typically for smooth transitions or interpolation.
 */
public interface InterpolatorBlender {
    /**
     * Blends two poses using the specified weight.
     *
     * <p>The weight determines the influence of each pose in the result:</p>
     * <ul>
     *   <li>When {@code weight} is 0, the result is identical to {@code basePose}.</li>
     *   <li>When {@code weight} is 1, the result is identical to {@code inputPose}.</li>
     *   <li>Values between 0 and 1 produce a smooth interpolation between the two poses.</li>
     * </ul>
     *
     * @param basePose the base pose (weight=0 means full influence)
     * @param inputPose the input pose to blend with the base (weight=1 means full influence)
     * @param weight the blend weight, typically in [0, 1]
     * @return the blended pose
     */
    Pose blend(Pose basePose, Pose inputPose, float weight);
}
