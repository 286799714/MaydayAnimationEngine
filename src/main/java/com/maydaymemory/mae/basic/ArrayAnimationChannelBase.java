package com.maydaymemory.mae.basic;

import com.maydaymemory.mae.util.DirtyTrackingArrayList;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;

public abstract class ArrayAnimationChannelBase<T extends Keyframe<?>>
        extends DirtyTrackingArrayList<T>
        implements AnimationChannel{

    private int indexCache = -1;

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
        indexCache = -1;
        return true;
    }

    /**
     * Returns the time in seconds of the last keyframe in the list.
     * This method requires the list to be in a non-dirty (refreshed) state.
     *
     * @return the time (in seconds) of the last keyframe.
     * @throws AssertionError if the internal state is dirty and has not been refreshed.
     */
    @Override
    public float getEndTimeS() {
        assertNotDirty();
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
        float cachedKeyframeTime = indexCache == -1 ? Float.MIN_VALUE : innerList.get(indexCache).getTimeS();
        if (cachedKeyframeTime < timeS || (cachedKeyframeTime == timeS && !open)) {
            if (indexCache == innerList.size() - 1 || innerList.get(indexCache + 1).getTimeS() > timeS) {
                return indexCache;
            }
        }

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
        indexCache = result;
        return result;
    }

    protected void assertNotDirty() {
        if (isDirty()) {
            throw new AssertionError("The channel is dirty. Please make sure to call the refresh method after manipulating keyframes.");
        }
    }
}
