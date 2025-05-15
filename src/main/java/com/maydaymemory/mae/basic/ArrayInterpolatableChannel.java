package com.maydaymemory.mae.basic;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class ArrayInterpolatableChannel<T>
        extends ArrayAnimationChannelBase<InterpolatableKeyframe<T>>
        implements InterpolatableChannel<T> {

    public ArrayInterpolatableChannel(@Nonnull ArrayList<InterpolatableKeyframe<T>> keyframes) {
        super(keyframes);
    }

    @Override
    public T compute(float timeS) {
        assertNotDirty();
        int index = findIndexBefore(timeS, false);
        int indexNext = Math.min(innerList.size() - 1, index + 1);
        InterpolatableKeyframe<T> keyframe = get(index);
        if (index == indexNext) { // only when timeS >= endTimeS
            return keyframe.getInterpolator().interpolate(this, index, index, 0);
        }
        InterpolatableKeyframe<T> keyframeNext = get(indexNext);
        float alphaTime = timeS - keyframe.getTimeS();
        float spanTime = keyframeNext.getTimeS() - keyframe.getTimeS();
        float alpha = alphaTime / spanTime;
        Interpolator<T> interpolator = keyframe.getInterpolator();
        Interpolator<T> interpolatorNext = keyframeNext.getInterpolator();
        if (interpolator.getPriority().compareTo(interpolatorNext.getPriority()) >= 0) {
            // use previous interpolator if its priority is greater than or equal to next one
            return interpolator.interpolate(this, index, indexNext, alpha);
        } else {
            // use next interpolator if priority is greater than previous one
            return interpolatorNext.interpolate(this, index, indexNext, alpha);
        }
    }

    @Override
    public InterpolatableKeyframe<T> getKeyFrame(int index) {
        return get(index);
    }

    @Override
    public int getKeyFrameCount() {
        return size();
    }
}
