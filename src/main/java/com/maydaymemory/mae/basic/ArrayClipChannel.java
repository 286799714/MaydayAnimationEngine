package com.maydaymemory.mae.basic;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a clip channel (or event channel as universal understanding).
 * Common use cases include triggering sound effects, particle systems, gameplay logic.
 *
 * <p>
 * When the animation is played, a time period is passed in for sampling.
 * This channel needs to return all keyframes in the time period in order.
 * </p>
 *
 * @param <T> the type of the keyframe
 */
public class ArrayClipChannel <T>
        extends ArrayAnimationChannelBase<Keyframe<T>>
        implements ClipChannel<T> {

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
    public ArrayClipChannel(@Nonnull ArrayList<Keyframe<T>> keyframes) {
        super(keyframes);
    }

    @Override
    public Iterable<T> clip(float fromTimeS, float toTimeS) {
        assertNotDirty();
        if (fromTimeS == toTimeS) {
            // handle the situation of [x, x). When the time period is [x, x), the keyframe at x should be returned.
            int index = findIndexBefore(fromTimeS, false);
            Keyframe<T> keyframe = get(index);
            if (keyframe.getTimeS() == fromTimeS) {
                return Collections.singletonList(keyframe.getValue());
            } else {
                return Collections.emptyList();
            }
        }
        int indexFrom, indexTo;
        if (fromTimeS > toTimeS) {
            // toTimeS is exclusive in time period, therefore, it should look for the last index in the close interval to its left
            indexFrom = findIndexBefore(toTimeS, false);
            // fromTimeS is inclusive in time period, it should look for the last index in the close interval to its left as well.
            indexTo = findIndexBefore(fromTimeS, false);
        } else {
            // fromTimeS is inclusive in time period, therefore, it should look for the last index in the open interval to its left
            indexFrom = findIndexBefore(fromTimeS, true);
            // toTimeS is exclusive in time period, it should look for the last index in the open interval to its left as well.
            indexTo = findIndexBefore(toTimeS, true);
        }
        if (indexFrom == indexTo) {
            return Collections.emptyList();
        }
        // No need to check if indexFrom is out of bound because if indexFrom != indexTo, it must smaller than indexTo,
        // and indexTo is smaller than list size. Therefore (indexFrom + 1) must smaller than list size.
        List<Keyframe<T>> subList = subList(indexFrom + 1, indexTo + 1);
        List<T> result = new ArrayList<>(subList.size());
        if (fromTimeS > toTimeS) { // return in reverse order
            for (int i = subList.size() - 1; i >= 0; i--) {
                result.add(subList.get(i).getValue());
            }
        } else {
            for (Keyframe<T> keyframe : subList) {
                result.add(keyframe.getValue());
            }
        }
        return result;
    }
}
