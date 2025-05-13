package com.maydaymemory.mae.blend;

@FunctionalInterface
public interface LayerBlend {
    float getWeight(int boneIndex);
}
