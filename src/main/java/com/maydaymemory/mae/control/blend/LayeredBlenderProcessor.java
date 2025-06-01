package com.maydaymemory.mae.control.blend;

import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.blend.LayerBlend;
import com.maydaymemory.mae.blend.LayeredBlender;
import com.maydaymemory.mae.control.PoseProcessor;
import com.maydaymemory.mae.control.PoseProcessorSequence;

public class LayeredBlenderProcessor implements PoseProcessor {
    private final LayeredBlender blender;
    private final LayerBlend layer;
    private int basePoseSlot = -1;
    private int inputPoseSlot = -1;

    public LayeredBlenderProcessor(LayeredBlender blender, LayerBlend layer) {
        this.blender = blender;
        this.layer = layer;
    }

    public LayerBlend getLayer() {
        return layer;
    }

    public LayeredBlender getBlender() {
        return blender;
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
                layer
        );
    }
}
