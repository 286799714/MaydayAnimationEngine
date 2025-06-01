package com.maydaymemory.mae.control.runner;

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
