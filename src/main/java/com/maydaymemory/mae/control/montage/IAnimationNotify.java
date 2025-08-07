package com.maydaymemory.mae.control.montage;

/**
 * Animation notification interface, for defining one-time notification events during animation playback.
 * 
 * <p>Animation notification is a mechanism in the animation montage system for triggering one-time events at specific time points.
 * When animation plays to the specified notification time point, the system will call the notification's {@link #onNotify(Object)} method.</p>
 * 
 * <p>Main uses of notifications:</p>
 * <ul>
 *   <li>Play sound effects</li>
 *   <li>Trigger particle effects</li>
 *   <li>Execute game logic</li>
 *   <li>Send event messages</li>
 * </ul>
 * 
 * @param <T> Context type
 * 
 * @author MaydayMemory
 * @since 1.0.4
 */
public interface IAnimationNotify<T> {
    /**
     * Called when animation plays to the notification time point.
     * 
     * @param context Context object
     */
    void onNotify(T context);
}
