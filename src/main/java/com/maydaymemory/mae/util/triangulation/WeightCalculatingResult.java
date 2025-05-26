package com.maydaymemory.mae.util.triangulation;

public class WeightCalculatingResult<T extends SamplerPoint> {
     private final T a;
     private final T b;
     private final T c;
     private final float alpha;
     private final float beta;
     private final float gamma;

     public WeightCalculatingResult(T a, T b, T c, float alpha, float beta, float gamma) {
          this.a = a;
          this.b = b;
          this.c = c;
          this.alpha = alpha;
          this.beta = beta;
          this.gamma = gamma;
     }

     public T a() {
          return a;
     }

     public T b() {
          return b;
     }

     public T c() {
          return c;
     }

     public float alpha() {
          return alpha;
     }

     public float beta() {
          return beta;
     }

     public float gamma() {
          return gamma;
     }
}
