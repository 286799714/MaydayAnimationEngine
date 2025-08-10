package com.maydaymemory.mae.control.montage;

import com.maydaymemory.mae.basic.BaseKeyframe;

public class AnimationNotifyKeyframe<T> extends BaseKeyframe<IAnimationNotify<T>> {
    private final IAnimationNotify<T> animationNotify;

    public AnimationNotifyKeyframe(float timeS, IAnimationNotify<T> animationNotify) {
        super(timeS);
        this.animationNotify = animationNotify;
    }

    @Override
    public IAnimationNotify<T> getValue() {
        return animationNotify;
    }
}
