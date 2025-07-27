package com.maydaymemory.mae.blend;

public final class DummyLayerBlend implements LayerBlend{
    public static final DummyLayerBlend INSTANCE = new DummyLayerBlend();

    private DummyLayerBlend(){}

    @Override
    public float getWeight(int boneIndex) {
        return 0;
    }
}
