package com.maydaymemory.mae.basic;

public interface ClipChannel<T> extends AnimationChannel{
    /**
     * Clips the content in the specified time range and returns it in order.
     *
     * <p>
     * The returned {@code Iterable<T>} contains all elements within the time range
     * {@code [fromTimeS, toTimeS)}, where:
     * <ul>
     *   <li>{@code fromTimeS} is inclusive — elements at exactly {@code fromTimeS} are included.</li>
     *   <li>{@code toTimeS} is exclusive — elements at exactly {@code toTimeS} are excluded.</li>
     * </ul>
     *
     * <p>
     * If {@code fromTimeS} &lt; {@code toTimeS}, the results are returned in chronological order.<br>
     * If {@code fromTimeS} &gt; {@code toTimeS}, the results are returned in reverse chronological order.
     *
     * @param fromTimeS the starting point of the time range (inclusive)
     * @param toTimeS the ending point of the time range (exclusive)
     * @return an {@code Iterable<T>} representing the clipped content in the specified time range and order
     */
    Iterable<T> clip(float fromTimeS, float toTimeS);
}
