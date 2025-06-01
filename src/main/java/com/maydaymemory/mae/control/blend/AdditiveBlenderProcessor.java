package com.maydaymemory.mae.control.blend;

import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.blend.AdditiveBlender;
import com.maydaymemory.mae.control.PoseProcessor;
import com.maydaymemory.mae.control.PoseProcessorSequence;

public class AdditiveBlenderProcessor implements PoseProcessor {
    private final AdditiveBlender blender;
    private int poseSlotA = -1;
    private int poseSlotB = -1;

    public AdditiveBlenderProcessor(AdditiveBlender blender) {
        this.blender = blender;
    }

    public AdditiveBlender getBlender() {
        return blender;
    }

    public int getPoseSlotB() {
        return poseSlotB;
    }

    public void setPoseSlotB(int poseSlotB) {
        this.poseSlotB = poseSlotB;
    }

    public int getPoseSlotA() {
        return poseSlotA;
    }

    public void setPoseSlotA(int poseSlotA) {
        this.poseSlotA = poseSlotA;
    }

    @Override
    public Pose process(PoseProcessorSequence sequence) {
        return blender.blend(
                PoseProcessorSequence.getPoseSafe(poseSlotA, sequence),
                PoseProcessorSequence.getPoseSafe(poseSlotB, sequence)
        );
    }
}
