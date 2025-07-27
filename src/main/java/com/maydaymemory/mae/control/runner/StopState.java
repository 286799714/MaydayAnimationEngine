package com.maydaymemory.mae.control.runner;

/**
 * Animation state that represents stopped animation playback.
 * 
 * <p>This state implements animation stopping, where the animation has
 * reached a terminal state and should not continue processing. Unlike
 * the pause state, the stop state indicates that animation execution
 * should halt completely.</p>
 * 
 * <p>The stop state is typically used when an animation has completed
 * its natural cycle or when explicitly requested to stop. It serves
 * as an end point in the animation state machine, signaling that
 * no further updates should occur.</p>
 * 
 * @author MaydayMemory
 * @since 1.0.1
 */
public class StopState implements IAnimationState{

    @Override
    public IAnimationState update(IAnimationContext ctx) {
        return this;
    }

    @Override
    public void onEnter(IAnimationContext ctx) {}

    @Override
    public boolean isEndPoint() {
        return true;
    }
}
