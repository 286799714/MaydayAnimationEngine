package com.maydaymemory.mae.control.blend;

import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.blend.InterpolatorBlender;
import com.maydaymemory.mae.control.PoseProcessor;
import com.maydaymemory.mae.control.PoseProcessorSequence;

public class InterpolatorBlenderProcessor implements PoseProcessor {
    private final InterpolatorBlender blender;
    private int basePoseSlot = -1;
    private int inputPoseSlot = -1;
    private float weight;

    public InterpolatorBlenderProcessor(InterpolatorBlender blender) {
        this.blender = blender;
    }

    public InterpolatorBlender getBlender() {
        return blender;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getBasePoseSlot() {
        return basePoseSlot;
    }

    public void setBasePoseSlot(int basePoseSlot) {
        this.basePoseSlot = basePoseSlot;
    }

    public int getInputPoseSlot() {
        return inputPoseSlot;
    }

    public void setInputPoseSlot(int inputPoseSlot) {
        this.inputPoseSlot = inputPoseSlot;
    }

    @Override
    public Pose process(PoseProcessorSequence sequence) {
        return blender.blend(
                PoseProcessorSequence.getPoseSafe(basePoseSlot, sequence),
                PoseProcessorSequence.getPoseSafe(inputPoseSlot, sequence),
                weight
        );
    }
}
