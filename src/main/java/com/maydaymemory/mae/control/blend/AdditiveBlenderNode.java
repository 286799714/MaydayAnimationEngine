package com.maydaymemory.mae.control.blend;

import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.blend.AdditiveBlender;
import com.maydaymemory.mae.control.OutputPort;
import com.maydaymemory.mae.control.PoseSlot;
import com.maydaymemory.mae.control.Slot;

/**
 * A control node that wraps an {@link AdditiveBlender} for pose blending operations.
 * 
 * <p>This node provides a control interface for additive blending, which combines
 * two poses using additive blending techniques. Additive blending is commonly
 * used for layering animations where one pose adds to another rather than
 * replacing it completely.</p>
 * 
 * <p>The node manages input slots for two poses (A and B) and provides an output
 * port that delivers the additively blended result.</p>
 * 
 * @author MaydayMemory
 * @since 1.0.1
 */
public class AdditiveBlenderNode {
    /** The underlying additive blender that performs the actual blending operation */
    private final AdditiveBlender blender;
    
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
    public AdditiveBlenderNode(AdditiveBlender blender) {
        this.blender = blender;
    }

    /**
     * Gets the underlying AdditiveBlender instance.
     * 
     * @return the AdditiveBlender used by this node
     */
    public AdditiveBlender getBlender() {
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
