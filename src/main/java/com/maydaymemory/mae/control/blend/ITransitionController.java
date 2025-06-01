package com.maydaymemory.mae.control.blend;

public interface ITransitionController {
    void start();
    float getTransitionProgress();
    boolean isFinished();
}
