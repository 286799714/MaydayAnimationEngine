package com.maydaymemory.mae.basic;

/**
 * Represents an animation channel that supports extracting (clipping) content over a time range.
 *
 * <p>This interface extends {@link AnimationChannel} and provides a method to retrieve a sequence of elements
 * (such as events or samples) that occur within a specified time interval. The order of the returned elements
 * depends on the direction of the time range.</p>
 *
 * @param <T> the type of element contained in this channel
 */
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
     * If {@code fromTimeS} &gt; {@code toTimeS}, the results are returned in reverse chronological order.<br>
     * If {@code fromTimeS} = {@code toTimeS}, the result will be empty.
     *
     * @param fromTimeS the starting point of the time range (inclusive)
     * @param toTimeS the ending point of the time range (exclusive)
     * @return an {@code Iterable<Keyframe<T>>} representing the clipped content in the specified time range and order
     */
    Iterable<Keyframe<T>> clip(float fromTimeS, float toTimeS);
}
