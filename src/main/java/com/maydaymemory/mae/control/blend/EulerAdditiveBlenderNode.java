package com.maydaymemory.mae.control.blend;

import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.blend.EulerAdditiveBlender;
import com.maydaymemory.mae.control.OutputPort;
import com.maydaymemory.mae.control.PoseSlot;
import com.maydaymemory.mae.control.Slot;

/**
 * A control node that wraps an {@link EulerAdditiveBlender} for pose blending operations.
 * 
 * @author MaydayMemory
 * @since 1.0.1
 */
public class EulerAdditiveBlenderNode {
    /** The underlying additive blender that performs the actual blending operation */
    private final EulerAdditiveBlender blender;
    
    /** Output port that provides the additively blended pose result */
    private final OutputPort<Pose> outputPort = this::getPose;
    
    /** Input slot for the first pose (pose A) */
    private final Slot<Pose> poseSlotA = new PoseSlot();
    
    /** Input slot for the second pose (pose B) */
    private final Slot<Pose> poseSlotB = new PoseSlot();

    /**
     * Constructs a new AdditiveBlenderNode with the specified blender.
     * 
     * @param blender the AdditiveBlender instance to use for pose blending
     */
    public EulerAdditiveBlenderNode(EulerAdditiveBlender blender) {
        this.blender = blender;
    }

    /**
     * Gets the underlying AdditiveBlender instance.
     * 
     * @return the AdditiveBlender used by this node
     */
    public EulerAdditiveBlender getBlender() {
        return blender;
    }

    /**
     * Gets the output port that provides the additively blended pose.
     * 
     * @return the OutputPort for the additively blended pose result
     */
    public OutputPort<Pose> getOutputPort() {
        return outputPort;
    }

    /**
     * Gets the input slot for the first pose (pose A).
     * 
     * @return the Slot for the first pose input
     */
    public Slot<Pose> getPoseSlotA() {
        return poseSlotA;
    }

    /**
     * Gets the input slot for the second pose (pose B).
     * 
     * @return the Slot for the second pose input
     */
    public Slot<Pose> getPoseSlotB() {
        return poseSlotB;
    }

    /**
     * Performs the additive blending operation using the current poses from input slots.
     * 
     * This method retrieves the poses from both input slots and uses the underlying
     * AdditiveBlender to combine them additively.
     * 
     * @return the additively blended pose result
     */
    public Pose getPose() {
        return blender.blend(poseSlotA.get(), poseSlotB.get());
    }
}
