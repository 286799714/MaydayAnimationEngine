package com.maydaymemory.mae.basic;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArrayClipChannel <T>
        extends ArrayAnimationChannelBase<Keyframe<T>>
        implements ClipChannel<T> {

    public ArrayClipChannel(@Nonnull ArrayList<Keyframe<T>> keyframes) {
        super(keyframes);
    }

    @Override
    public Iterable<T> clip(float fromTimeS, float toTimeS) {
        assertNotDirty();
        if (fromTimeS == toTimeS) {
            return Collections.emptyList();
        }
        if (fromTimeS > toTimeS) {
            throw new IllegalArgumentException("fromTimeS > toTimeS");
        }
        // fromTimeS is inclusive in time period, therefore, it should look for the last index in the open interval to its left
        int indexFrom = findIndexBefore(fromTimeS, true);
        // toTimeS is not included in time period, it should look for the last index in the open interval to its left as well.
        int indexTo = findIndexBefore(toTimeS, true);
        if (indexFrom == indexTo) {
            return Collections.emptyList();
        }
        // No need to check if indexFrom is out of bound because if indexFrom != indexTo, it must smaller than indexTo,
        // and indexTo is smaller than list size. Therefore (indexFrom + 1) must smaller than list size.
        List<Keyframe<T>> subList = subList(indexFrom + 1, indexTo + 1);
        List<T> result = new ArrayList<>(subList.size());
        for (Keyframe<T> keyframe : subList) {
            result.add(keyframe.getValue());
        }
        return result;
    }
}
