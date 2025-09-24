package com.maydaymemory.mae.basic;

import com.maydaymemory.mae.util.DirtyTrackingArrayList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Animation channel that stores keyframes in an array.
 *
 * @param <T> Keyframe type
 */
public abstract class ArrayAnimationChannelBase<T extends Keyframe<?>>
        extends DirtyTrackingArrayList<T>
        implements AnimationChannel{

    /**
     * Constructs an array animation channel with specified initial list,
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
     * @param initialList the initial list of keyframes
     */
    public ArrayAnimationChannelBase(@Nonnull ArrayList<T> initialList) {
        super(initialList);
    }

    /**
     * Refreshes the internal state by sorting the list if it is marked dirty.
     * Always returns {@code true}, indicating the refresh operation has completed.
     *
     * @return {@code true} because refresh operation is always completed after call this method.
     */
    @Override
    protected boolean onRefresh() {
        Collections.sort(innerList);
        return true;
    }

    /**
     * Returns the time in seconds of the last keyframe in the list.
     * This method requires the list to be in a non-dirty (refreshed) state.
     *
     * @return the time (in seconds) of the last keyframe, or 0 if there is no keyframe in this channel.
     * @throws AssertionError if the internal state is dirty and has not been refreshed.
     */
    @Override
    public float getEndTimeS() {
        assertNotDirty();
        if (innerList.isEmpty()) {
            return 0;
        }
        return innerList.get(innerList.size() - 1).getTimeS();
    }

    /**
     * Performs a binary search to find the index of the last keyframe before a given time,
     * The list must be sorted (i.e., not dirty).
     *
     * @param timeS the time in seconds to compare against keyframe times.
     * @param open if {@code true}, performs an open search (strictly less than {@code timeS});
     *             if {@code false}, allows exact matches (less than or equal to {@code timeS}).
     * @return the index of the last keyframe matching the condition,
     *         or {@code -1} if no such keyframe exists.
     * @throws AssertionError if the internal state is dirty and has not been refreshed.
     */
    public int findIndexBefore(float timeS, boolean open) {
        assertNotDirty();

        if (innerList.isEmpty()) {
            return -1;
        }

        // Cache binary search results to reduce the overhead of interpolation in continuous time
//        float cachedKeyframeTime = indexCached == -1 ? Float.MIN_VALUE : innerList.get(indexCached).getTimeS();
//        if (cachedKeyframeTime < timeS || (cachedKeyframeTime == timeS && !open)) {
//            if (indexCached == innerList.size() - 1 || innerList.get(indexCached + 1).getTimeS() > timeS) {
//                return indexCached;
//            }
//        }

        int low = 0;
        int high = innerList.size() - 1;
        int result = -1;
        while (low <= high) {
            int mid = (low + high) >>> 1;
            T t = innerList.get(mid);
            if (t.getTimeS() < timeS || (t.getTimeS() == timeS && !open)) {
                result = mid;
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return result;
    }

    /**
     * Checks whether this channel is dirty.
     */
    protected void assertNotDirty() {
        if (isDirty()) {
            throw new AssertionError("The channel is dirty. Please make sure to call the refresh method after manipulating keyframes.");
        }
    }
}
