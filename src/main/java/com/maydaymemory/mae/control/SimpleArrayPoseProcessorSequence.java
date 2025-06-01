package com.maydaymemory.mae.control;

import com.maydaymemory.mae.basic.Pose;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class SimpleArrayPoseProcessorSequence implements PoseProcessorSequence {
    private final ArrayList<PoseProcessor> array = new ArrayList<>();

    @Override
    public void setProcessor(int i, PoseProcessor processor) {
        int size = array.size();
        if (i == size) {
            array.add(processor);
        } else if (i > size) { // ensure array capability
            for (int j = i; j > size; j--) {
                array.add(null);
            }
            array.add(processor);
        } else {
            array.set(i, processor);
        }
    }

    @Nullable
    @Override
    public Pose getPose(int i) {
        if (i >= array.size()) {
            return null;
        }
        PoseProcessor processor = array.get(i);
        if (processor == null) {
            return null;
        }
        return processor.process(this);
    }
}
