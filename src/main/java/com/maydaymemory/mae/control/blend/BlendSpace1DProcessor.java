package com.maydaymemory.mae.control.blend;

import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.blend.BlendSpace1D;
import com.maydaymemory.mae.control.PoseProcessor;
import com.maydaymemory.mae.control.PoseProcessorSequence;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

public class BlendSpace1DProcessor implements PoseProcessor {
    private final BlendSpace1D blendSpace;
    private final Int2IntOpenHashMap map = new Int2IntOpenHashMap();
    private float position;

    public BlendSpace1DProcessor(BlendSpace1D blendSpace) {
        this.blendSpace = blendSpace;
    }

    public void setSamplerSlot(int samplerIndex, int slot) {
        map.put(samplerIndex, slot);
    }

    public BlendSpace1D getBlendSpace() {
        return blendSpace;
    }

    public float getPosition() {
        return position;
    }

    public void setPosition(float position) {
        this.position = position;
    }

    @Override
    public Pose process(PoseProcessorSequence sequence) {
        for (Int2IntMap.Entry entry : map.int2IntEntrySet()) {
            blendSpace.setSamplerPose(
                    entry.getIntKey(),
                    PoseProcessorSequence.getPoseSafe(entry.getIntValue(), sequence)
            );
        }
        return blendSpace.blend(position);
    }
}
