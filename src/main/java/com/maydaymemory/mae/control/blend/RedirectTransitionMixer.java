package com.maydaymemory.mae.control.blend;

import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.blend.InterpolatorBlender;
import com.maydaymemory.mae.control.PoseProcessor;
import com.maydaymemory.mae.control.PoseProcessorSequence;
import com.maydaymemory.mae.control.Tickable;

import java.util.Deque;
import java.util.LinkedList;

public class RedirectTransitionMixer implements Tickable, PoseProcessor, ITransitionMixer {
    private int current = -1;
    private final Deque<Node> prefix = new LinkedList<>();
    private int transitionTo = -1;
    private ITransitionController controller;
    private final InterpolatorBlender blender;

    public RedirectTransitionMixer(InterpolatorBlender blender) {
        this.blender = blender;
    }

    @Override
    public Pose process(PoseProcessorSequence sequence) {
        if (controller == null) { // logically when controller is null, prefix queue is empty
            return PoseProcessorSequence.getPoseSafe(current, sequence);
        }

        float alpha = controller.getTransitionProgress();
        if (alpha == 1) {
            return PoseProcessorSequence.getPoseSafe(transitionTo, sequence);
        }

        Pose currentPose = PoseProcessorSequence.getPoseSafe(current, sequence);
        Pose prefixPose;
        // assuming that the newTransition() method has minimized the queue, we can directly traverse it here.
        for (Node node : prefix) {
            prefixPose = PoseProcessorSequence.getPoseSafe(node.slot, sequence);
            currentPose = blender.blend(currentPose, prefixPose, node.transition);
        }

        if (alpha == 0) {
            return currentPose;
        }

        Pose transitionPose = PoseProcessorSequence.getPoseSafe(transitionTo, sequence);
        return blender.blend(currentPose, transitionPose, alpha);
    }

    @Override
    public void tick() {
        if (controller == null) {
            return;
        }
        if (controller.isFinished()) {
            current = transitionTo;
            prefix.clear();
            transitionTo = -1;
            controller = null;
        }
    }

    @Override
    public void initialize(int slot) {
        current = slot;
        prefix.clear();
        transitionTo = -1;
        controller = null;
    }

    @Override
    public void newTransition(int slot, ITransitionController controller) {
        if (this.controller != null) {
            float transition = this.controller.getTransitionProgress();
            // if transition progress = 0, this node does not need to enqueue
            if (transition != 0) {
                if (transition == 1) { // if transition progress = 1, the prefix in front of it is meaningless
                    current = transitionTo;
                    prefix.clear();
                } else {
                    Node node = new Node();
                    node.slot = transitionTo;
                    node.transition = transition;
                    prefix.offer(node);
                }
            }
        }
        this.transitionTo = slot;
        this.controller = controller;
        controller.start();
    }

    @Override
    public int currentSlot() {
        return current;
    }

    private static class Node {
        int slot;
        float transition;
    }
}
