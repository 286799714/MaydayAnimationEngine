package com.maydaymemory.mae.blend;

public final class DummyLayerBlend implements LayerBlend{
    private final float weight;

    public static final DummyLayerBlend ZERO_WEIGHT_DUMMY = new DummyLayerBlend(0);
    public static final DummyLayerBlend ONE_WEIGHT_DUMMY = new DummyLayerBlend(1);

    public DummyLayerBlend(float weight){
        this.weight = weight;
    }

    @Override
    public float getWeight(int boneIndex) {
        return weight;
    }
}
