package com.maydaymemory.mae.control.montage;

/**
 * Animation notification state interface, for defining persistent notification states during animation playback.
 * 
 * <p>Animation notification state is a mechanism in the animation montage system for managing persistent events.
 * Unlike one-time notifications, notification states have a clear lifecycle: start, update, and end.</p>
 * 
 * <p>Note: In one tick, onUpdate will be called at most once. And no matter how short the state duration is,
 * until the animation plays past the notification state's end time point, onUpdate will be called at least once.</p>
 * 
 * <p>Main uses of notification states:</p>
 * <ul>
 *   <li>Continuous sound playback</li>
 *   <li>Maintain particle effects</li>
 *   <li>Execute persistent collision box detection</li>
 * </ul>
 * 
 * <p>Lifecycle:</p>
 * <ol>
 *   <li>{@link #onStart(Object)} - Called when state starts</li>
 *   <li>{@link #onUpdate(Object)} - Called periodically during state duration</li>
 *   <li>{@link #onEnd(Object)} - Called when state ends</li>
 * </ol>
 * 
 * @param <T> Context type
 * 
 * @author MaydayMemory
 * @since 1.0.4
 */
public interface IAnimationNotifyState<T> {
    /**
     * Called when the notification state starts.
     * 
     * @param context Context object
     */
    void onStart(T context);
    
    /**
     * Called when the notification state ends.
     * 
     * @param context Context object
     */
    void onEnd(T context);
    
    /**
     * Called when the notification state updates.
     * 
     * <p>This method is called periodically during the notification state duration,
     * for executing persistent state logic and updates.</p>
     * 
     * @param context Context object
     */
    void onUpdate(T context);
}
