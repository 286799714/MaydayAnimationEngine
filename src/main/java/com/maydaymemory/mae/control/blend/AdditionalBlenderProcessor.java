package com.maydaymemory.mae.control.blend;

import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.blend.AdditionalBlender;
import com.maydaymemory.mae.control.PoseProcessor;
import com.maydaymemory.mae.control.PoseProcessorSequence;

public class AdditionalBlenderProcessor implements PoseProcessor {
    private final AdditionalBlender blender;
    private int basePoseSlot = -1;
    private int additionalPoseSlot = -1;

    public AdditionalBlenderProcessor(AdditionalBlender blender) {
        this.blender = blender;
    }

    public int getBasePoseSlot() {
        return basePoseSlot;
    }

    public void setBasePoseSlot(int basePoseSlot) {
        this.basePoseSlot = basePoseSlot;
    }

    public int getAdditionalPoseSlot() {
        return additionalPoseSlot;
    }

    public void setAdditionalPoseSlot(int additionalPoseSlot) {
        this.additionalPoseSlot = additionalPoseSlot;
    }

    public AdditionalBlender getBlender() {
        return blender;
    }

    @Override
    public Pose process(PoseProcessorSequence sequence) {
        return blender.blend(
                PoseProcessorSequence.getPoseSafe(basePoseSlot, sequence),
                PoseProcessorSequence.getPoseSafe(additionalPoseSlot, sequence)
        );
    }
}
