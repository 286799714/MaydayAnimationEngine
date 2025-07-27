package com.maydaymemory.mae.control.blend;

import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.blend.AdditionalBlender;
import com.maydaymemory.mae.control.OutputPort;
import com.maydaymemory.mae.control.PoseSlot;
import com.maydaymemory.mae.control.Slot;

/**
 * A control node that wraps an {@link AdditionalBlender} for pose blending operations.
 * 
 * <p>This node provides a control interface for additional blending, which combines
 * a base pose with an additional pose using the specified blending strategy.
 * The node manages input slots for the base and additional poses, and provides
 * an output port that delivers the blended result.</p>
 * 
 * @author MaydayMemory
 * @since 1.0.1
 */
public class AdditionalBlenderNode {
    /** The underlying additional blender that performs the actual blending operation */
    private final AdditionalBlender blender;
    
    /** Output port that provides the blended pose result */
    private final OutputPort<Pose> outputPort = this::getPose;
    
    /** Input slot for the base pose to be blended */
    private final Slot<Pose> basePoseSlot = new PoseSlot();
    
    /** Input slot for the additional pose to be blended with the base pose */
    private final Slot<Pose> additionalPoseSlot = new PoseSlot();

    /**
     * Constructs a new AdditionalBlenderNode with the specified blender.
     * 
     * @param blender the AdditionalBlender instance to use for pose blending
     * @throws IllegalArgumentException if blender is null
     */
    public AdditionalBlenderNode(AdditionalBlender blender) {
        this.blender = blender;
    }

    /**
     * Gets the underlying AdditionalBlender instance.
     * 
     * @return the AdditionalBlender used by this node
     */
    public AdditionalBlender getBlender() {
        return blender;
    }

    /**
     * Gets the output port that provides the blended pose.
     * 
     * @return the OutputPort for the blended pose result
     */
    public OutputPort<Pose> getOutputPort() {
        return outputPort;
    }

    /**
     * Gets the input slot for the base pose.
     * 
     * @return the Slot for the base pose input
     */
    public Slot<Pose> getBasePoseSlot() {
        return basePoseSlot;
    }

    /**
     * Gets the input slot for the additional pose.
     * 
     * @return the Slot for the additional pose input
     */
    public Slot<Pose> getAdditionalPoseSlot() {
        return additionalPoseSlot;
    }

    /**
     * Performs the blending operation using the current poses from input slots.
     * 
     * This method retrieves the poses from the base and additional pose slots
     * and uses the underlying AdditionalBlender to combine them.
     * 
     * @return the blended pose result
     */
    public Pose getPose() {
        return blender.blend(basePoseSlot.get(), additionalPoseSlot.get());
    }
}
