package com.maydaymemory.mae.basic;

import javax.annotation.Nonnull;
import java.util.*;

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

    /**
     * Constructs an empty clip channel.
     */
    public ArrayClipChannel() {
        super(new ArrayList<>());
    }

    @Override
    public Iterable<Keyframe<T>> clip(float fromTimeS, float toTimeS) {
        assertNotDirty();
        if (fromTimeS == toTimeS) {
            return Collections.emptyList();
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
        if (fromTimeS > toTimeS) {
            // return in reverse order
            return reversed(subList);
        } else {
            return subList;
        }
    }

    public static <T> Iterable<T> reversed(List<T> list) {
        return () -> new Iterator<T>() {
            private final ListIterator<T> it = list.listIterator(list.size());

            @Override
            public boolean hasNext() {
                return it.hasPrevious();
            }

            @Override
            public T next() {
                return it.previous();
            }
        };
    }
}
