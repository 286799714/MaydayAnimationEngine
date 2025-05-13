package com.maydaymemory.mae.util.triangulation;

public record WeightCalculatingResult<T extends SamplerPoint>(
        T a,
        T b,
        T c,
        float alpha,
        float beta,
        float gamma
) {}
