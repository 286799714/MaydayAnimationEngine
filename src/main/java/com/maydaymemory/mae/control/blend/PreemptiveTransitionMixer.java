package com.maydaymemory.mae.control.blend;

import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.blend.InterpolatorBlender;
import com.maydaymemory.mae.control.PoseProcessor;
import com.maydaymemory.mae.control.PoseProcessorSequence;
import com.maydaymemory.mae.control.Tickable;

public class PreemptiveTransitionMixer implements Tickable, PoseProcessor, ITransitionMixer {
    private int current = -1;
    private int transitionTo = -1;
    private ITransitionController transitionController;
    private final InterpolatorBlender blender;

    public PreemptiveTransitionMixer(InterpolatorBlender blender) {
        this.blender = blender;
    }

    @Override
    public Pose process(PoseProcessorSequence sequence) {
        if (transitionController == null) {
            return PoseProcessorSequence.getPoseSafe(current, sequence);
        }
        float alpha = transitionController.getTransitionProgress();
        if (alpha == 0) {
            return PoseProcessorSequence.getPoseSafe(current, sequence);
        } else if (alpha == 1) {
            return PoseProcessorSequence.getPoseSafe(transitionTo, sequence);
        } else {
            Pose currentPose = PoseProcessorSequence.getPoseSafe(current, sequence);
            Pose transitionToPose = PoseProcessorSequence.getPoseSafe(transitionTo, sequence);
            return blender.blend(currentPose, transitionToPose, alpha);
        }
    }

    @Override
    public void tick() {
        if (transitionController != null) {
            // if transition is finished, let transitionToAnimation to be currentAnimation
            if (transitionController.isFinished()) {
                current = transitionTo;
                transitionTo = -1;
                transitionController = null;
            }
        }
    }

    @Override
    public void initialize(int slot) {
        current = slot;
        transitionController = null;
        transitionTo = -1;
    }

    @Override
    public void newTransition(int slot, ITransitionController controller) {
        this.transitionController = controller;
        controller.start();
        transitionTo = slot;
    }

    @Override
    public int currentSlot() {
        return current;
    }
}
