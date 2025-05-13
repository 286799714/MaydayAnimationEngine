package com.maydaymemory.mae.basic;

import org.joml.Vector3fc;

/**
 * Simple keyframe base on {@link Vector3fc}, usually used to describe transformations (e.g., translation, rotation, scaling)
 */
public class Vector3fKeyframe extends BaseKeyframe<Vector3fc> implements InterpolatableKeyframe<Vector3fc> {
    private final Vector3fc pre;
    private final Vector3fc post;
    private final Interpolator<Vector3fc> interpolator;

    public Vector3fKeyframe(float timeS,
                            Vector3fc pre,
                            Vector3fc post,
                            Interpolator<Vector3fc> interpolator) {
        super(timeS);
        this.pre = pre;
        this.post = post;
        this.interpolator = interpolator;
    }

    @Override
    public Vector3fc getPre() {
        return pre;
    }

    @Override
    public Vector3fc getPost() {
        return post;
    }

    @Override
    public Interpolator<Vector3fc> getInterpolator() {
        return interpolator;
    }

    @Override
    public Vector3fc getValue() {
        return pre;
    }
}
