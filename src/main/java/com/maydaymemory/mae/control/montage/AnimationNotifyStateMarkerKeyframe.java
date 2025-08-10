package com.maydaymemory.mae.control.montage;

import com.maydaymemory.mae.basic.BaseKeyframe;

public class AnimationNotifyStateMarkerKeyframe<T> extends BaseKeyframe<AnimationNotifyStateMarker<T>> {
    private final AnimationNotifyStateMarker<T> marker;

    public AnimationNotifyStateMarkerKeyframe(float timeS, AnimationNotifyStateMarker<T> marker) {
        super(timeS);
        this.marker = marker;
    }

    @Override
    public AnimationNotifyStateMarker<T> getValue() {
        return marker;
    }
}
