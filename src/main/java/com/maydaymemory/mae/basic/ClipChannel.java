package com.maydaymemory.mae.basic;

public interface ClipChannel<T> extends AnimationChannel{
    /**
     * Clip a block of data within a time period and return it in chronological order
     *
     * @param fromTimeS the left edge of the time period, inclusive
     * @param toTimeS the right edge of the time period, not included
     * @return A traversable container returned in chronological order within the time period
     * @throws IllegalArgumentException when <code>fromTimeS</code> is bigger than <code>toTimeS</>
     */
    Iterable<T> clip(float fromTimeS, float toTimeS);
}
