package com.maydaymemory.mae.control.blend;

public interface ITransitionMixer {
    void initialize(int slot);
    void newTransition(int slot, ITransitionController controller);
    int currentSlot();
}
