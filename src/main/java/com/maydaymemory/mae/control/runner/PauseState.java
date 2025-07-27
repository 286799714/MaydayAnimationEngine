package com.maydaymemory.mae.control.runner;

/**
 * Animation state that represents paused animation playback.
 * 
 * <p>This state implements animation pausing, where the animation progress
 * remains static and no further advancement occurs. The state maintains
 * the current animation position without any time-based progression.</p>
 * 
 * <p>The pause state is useful for temporarily halting animation playback
 * while maintaining the current pose, allowing for user-controlled
 * animation control or waiting for external events.</p>
 * 
 * @author MaydayMemory
 * @since 1.0.1
 */
public class PauseState implements IAnimationState {
    @Override
    public IAnimationState update(IAnimationContext ctx) {
        return this;
    }

    @Override
    public void onEnter(IAnimationContext ctx) {}

    @Override
    public boolean isEndPoint() {
        return false;
    }
}
