package com.maydaymemory.mae.control.blend;

import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.blend.InterpolatorBlender;
import com.maydaymemory.mae.control.PoseProcessor;
import com.maydaymemory.mae.control.PoseProcessorSequence;
import com.maydaymemory.mae.control.Tickable;

import java.util.LinkedList;
import java.util.Queue;

public class QueuedTransitionMixer implements Tickable, PoseProcessor, ITransitionMixer {
    private int current = -1;
    private int transitionTo = -1;
    private ITransitionController transitionController;
    private final InterpolatorBlender blender;
    private final Queue<AnimationPlan> queue = new LinkedList<>();

    public QueuedTransitionMixer(InterpolatorBlender blender) {
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
        if (transitionController == null) {
            AnimationPlan plan = queue.poll();
            if (plan != null) {
                transitionTo = plan.slot;
                transitionController = plan.controller;
                transitionController.start();
            }
        }
    }

    @Override
    public void initialize(int slot) {
        current = slot;
        transitionTo = -1;
        transitionController = null;
        queue.clear();
    }

    @Override
    public void newTransition(int slot, ITransitionController controller) {
        if (transitionController != null) {
            AnimationPlan plan = new AnimationPlan();
            plan.controller = controller;
            plan.slot = slot;
            queue.offer(plan);
        } else {
            transitionTo = slot;
            transitionController = controller;
            controller.start();
        }
    }

    @Override
    public int currentSlot() {
        return current;
    }

    private static class AnimationPlan {
        int slot;
        ITransitionController controller;
    }
}
