package com.maydaymemory.mae.basic;

import javax.annotation.Nonnull;

public abstract class BaseKeyframe<T> implements Keyframe<T> {
    protected final float timeS;

    public BaseKeyframe(float timeS) {
        this.timeS = timeS;
    }

    @Override
    public int compareTo(@Nonnull Keyframe<T> other) {
        return Float.compare(this.timeS, other.getTimeS());
    }

    @Override
    public float getTimeS() {
        return timeS;
    }
}
