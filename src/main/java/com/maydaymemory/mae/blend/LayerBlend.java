package com.maydaymemory.mae.blend;

/**
 * Functional interface for providing per-bone blending weights in layered blending operations.
 * Implementations return the blend weight for a given bone index.
 */
@FunctionalInterface
public interface LayerBlend {
    float getWeight(int boneIndex);
}
