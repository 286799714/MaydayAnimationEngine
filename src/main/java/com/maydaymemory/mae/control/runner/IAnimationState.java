package com.maydaymemory.mae.control.runner;

public interface IAnimationState {
    IAnimationState update(IAnimationContext ctx);
    void onEnter(IAnimationContext ctx);
    boolean isEndPoint();
}
