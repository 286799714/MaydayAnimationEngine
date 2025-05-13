package com.maydaymemory.mae.basic;

import com.maydaymemory.mae.util.FloatSupplier;
import org.joml.Vector3f;
import org.joml.Vector3fc;

/**
 * Keyframe implementation which can dynamically evaluate values.
 * Evaluating is performed each time the value is obtained.
 */
public class DynamicVector3fKeyframe extends BaseKeyframe<Vector3fc> implements InterpolatableKeyframe<Vector3fc> {
    private final FloatSupplier x1, y1, z1, x2, y2, z2;
    private final Interpolator<Vector3fc> interpolator;

    public DynamicVector3fKeyframe(float timeS,
                                   FloatSupplier x1, FloatSupplier y1, FloatSupplier z1,
                                   FloatSupplier x2, FloatSupplier y2, FloatSupplier z2,
                                   Interpolator<Vector3fc> interpolator) {
        super(timeS);
        this.x1 = x1;
        this.y1 = y1;
        this.z1 = z1;
        this.x2 = x2;
        this.y2 = y2;
        this.z2 = z2;
        this.interpolator = interpolator;
    }

    @Override
    public Vector3fc getPre() {
        return new Vector3f(x1.getAsFloat(), y1.getAsFloat(), z1.getAsFloat());
    }

    @Override
    public Vector3fc getPost() {
        return new Vector3f(x2.getAsFloat(), y2.getAsFloat(), z2.getAsFloat());
    }

    @Override
    public Interpolator<Vector3fc> getInterpolator() {
        return interpolator;
    }

    @Override
    public Vector3fc getValue() {
        return getPre();
    }
}
