package com.maydaymemory.mae.control.runner;

/**
 * Interface representing a state of animation runner.
 * 
 * <p>This interface defines the contract for animation states that can be used
 * within an animation context. Each state represents a different behavior or
 * phase of animation execution, such as playing, pausing, looping, or stopping.</p>
 * 
 * @author MaydayMemory
 * @since 1.0.1
 */
public interface IAnimationState {
    /**
     * Updates context and return the next state to transition to.
     * If the state should remain the same, return this instance.
     * 
     * @param ctx the animation context containing data to update (e.g. progress, last update time, etc.)
     * @return the next state to transition to, or this instance to remain in the current state
     */
    IAnimationState update(IAnimationContext ctx);
    
    /**
     * Called when entering this state.
     * 
     * <p>This method is invoked when transitioning to this state, allowing
     * the state to perform any initialization or setup operations needed
     * for proper state execution.</p>
     * 
     * @param ctx the animation context containing data to update (e.g. progress, last update time, etc.)
     */
    void onEnter(IAnimationContext ctx);
    
    /**
     * Determines if this state represents an end point in the animation.
     * 
     * <p>End point states indicate that the animation runner has reached a terminal
     * state. This is typically used by states like StopState to signal that animation
     *  execution should halt.</p>
     * 
     * @return true if this state is an end point, false otherwise
     */
    boolean isEndPoint();
}
