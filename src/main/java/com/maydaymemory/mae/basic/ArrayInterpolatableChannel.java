package com.maydaymemory.mae.basic;

import javax.annotation.Nonnull;
import java.util.ArrayList;

/**
 * An array-backed interpolatable channel
 *
 * @param <T> the type of the interpolatable keyframe
 */
public class ArrayInterpolatableChannel<T>
        extends ArrayAnimationChannelBase<InterpolatableKeyframe<T>>
        implements InterpolatableChannel<T> {

    /**
     * Constructs a clip channel with specified initial list,
     * this list will be wrapped (or, so called, enhanced), not copied, which means that the outside holding this list
     * can still access and change the list elements.
     *
     * <p>
     * <b>Important:</b> The program does not check whether the keyframes in the initial list are in the correct order
     * (in ascending time order). Please make sure you know the order of the elements in the list.
     * Otherwise you should pass an empty list to the constructor and add keyframes one by one,
     * then call {@link #refresh()} to sort them.
     * </p>
     *
     * @param keyframes the initial list of keyframes
     */
    public ArrayInterpolatableChannel(@Nonnull ArrayList<InterpolatableKeyframe<T>> keyframes) {
        super(keyframes);
    }

    @Override
    public T compute(float timeS) {
        assertNotDirty();
        int index = findIndexBefore(timeS, false);
        int indexNext = Math.min(innerList.size() - 1, index + 1);
        if (index == -1) {
            if (isEmpty()) {
                return null;
            } else {
                return get(0).getInterpolator().interpolate(this, 0, 0, 0);
            }
        }
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
