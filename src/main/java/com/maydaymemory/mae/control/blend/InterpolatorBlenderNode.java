package com.maydaymemory.mae.control.blend;

import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.blend.InterpolatorBlender;
import com.maydaymemory.mae.control.OutputPort;
import com.maydaymemory.mae.control.PoseSlot;
import com.maydaymemory.mae.control.Slot;

import java.util.Objects;

/**
 * A control node that wraps an {@link InterpolatorBlender} for pose blending operations.
 * 
 * <p>This node provides a control interface for interpolator blending, which combines
 * a base pose with an input pose using a weight parameter to control the blending
 * amount. The interpolation is performed using the underlying InterpolatorBlender's
 * interpolation strategy.</p>
 * 
 * <p>The node manages input slots for the base pose, input pose, and weight parameter,
 * providing an output port that delivers the interpolated pose result.</p>
 * 
 * @author MaydayMemory
 * @since 1.0.1
 */
public class InterpolatorBlenderNode{
    /** The underlying interpolator blender that performs the actual blending operation */
    private final InterpolatorBlender blender;
    
    /** Input slot for the weight parameter that controls the blending amount (0.0 to 1.0) */
    private final Slot<Float> weightSlot = new Slot<>();
    
    /** Input slot for the base pose to be blended */
    private final Slot<Pose> basePoseSlot = new PoseSlot();
    
    /** Input slot for the input pose to be blended with the base pose */
    private final Slot<Pose> inputPoseSlot = new PoseSlot();
    
    /** Output port that provides the interpolated pose result */
    private final OutputPort<Pose> outputPort = this::getPose;

    /**
     * Constructs a new InterpolatorBlenderNode with the specified blender.
     * 
     * @param blender the InterpolatorBlender instance to use for pose blending
     */
    public InterpolatorBlenderNode(InterpolatorBlender blender) {
        this.blender = blender;
    }

    /**
     * Gets the underlying InterpolatorBlender instance.
     * 
     * @return the InterpolatorBlender used by this node
     */
    public InterpolatorBlender getBlender() {
        return blender;
    }

    /**
     * Gets the input slot for the weight parameter.
     * 
     * @return the Slot for the weight input that controls blending amount
     */
    public Slot<Float> getWeightSlot() {
        return weightSlot;
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
     * Gets the input slot for the input pose.
     * 
     * @return the Slot for the input pose to be blended
     */
    public Slot<Pose> getInputPoseSlot() {
        return inputPoseSlot;
    }

    /**
     * Gets the output port that provides the interpolated pose.
     * 
     * @return the OutputPort for the interpolated pose result
     */
    public OutputPort<Pose> getOutputPort() {
        return outputPort;
    }

    /**
     * Performs the interpolation operation using the current poses and weight.
     * 
     * <p>This method retrieves the poses from the base and input pose slots along with
     * the weight parameter, and uses the underlying InterpolatorBlender to perform
     * the weighted interpolation between the poses.</p>
     * 
     * @return the interpolated pose result
     * @throws NullPointerException if the weight slot contains null
     */
    public Pose getPose() {
        return blender.blend(basePoseSlot.get(), inputPoseSlot.get(), Objects.requireNonNull(weightSlot.get()));
    }
}
