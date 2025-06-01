package com.maydaymemory.mae.control.blend;

import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.blend.BlendSpace2D;
import com.maydaymemory.mae.control.PoseProcessor;
import com.maydaymemory.mae.control.PoseProcessorSequence;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

public class BlendSpace2DProcessor implements PoseProcessor {
    private final BlendSpace2D blendSpace;
    private final Int2IntOpenHashMap map = new Int2IntOpenHashMap();
    private float positionX;
    private float positionY;

    public BlendSpace2DProcessor(BlendSpace2D blendSpace) {
        this.blendSpace = blendSpace;
    }

    public void setSamplerSlot(int samplerIndex, int slot) {
        map.put(samplerIndex, slot);
    }

    public BlendSpace2D getBlendSpace() {
        return blendSpace;
    }

    public float getPositionX() {
        return positionX;
    }

    public void setPositionX(float positionX) {
        this.positionX = positionX;
    }

    public float getPositionY() {
        return positionY;
    }

    public void setPositionY(float positionY) {
        this.positionY = positionY;
    }

    @Override
    public Pose process(PoseProcessorSequence sequence) {
        for (Int2IntMap.Entry entry : map.int2IntEntrySet()) {
            blendSpace.setSamplerPose(
                    entry.getIntKey(),
                    PoseProcessorSequence.getPoseSafe(entry.getIntValue(), sequence)
            );
        }
        return blendSpace.blend(positionX, positionY);
    }
}
