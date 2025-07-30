package com.maydaymemory.mae.control.blend;

import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.blend.BlendSpace2D;
import com.maydaymemory.mae.control.OutputPort;
import com.maydaymemory.mae.control.Slot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A control node that wraps a {@link BlendSpace2D} for two-dimensional pose blending operations.
 * 
 * <p>This node provides a control interface for two-dimensional blend spaces, which
 * allow smooth interpolation between multiple poses based on two parameters
 * (position X and position Y). The node manages a collection of pose samplers
 * and two position inputs, providing an output port that delivers the interpolated
 * pose result.</p>
 * 
 * <p>Two-dimensional blend spaces are commonly used in animation systems for creating
 * smooth transitions between different poses based on two continuous parameters
 * such as speed and direction, or other two-dimensional game state variables.</p>
 * 
 * @author MaydayMemory
 * @since 1.0.1
 */
public class BlendSpace2DNode {
    /** The underlying two-dimensional blend space that performs the interpolation */
    private final BlendSpace2D blendSpace;
    
    /** List of slots for pose samplers that provide poses to the blend space */
    private final List<Slot<BlendSpace1DNode.PoseSampler>> samplerSlots = new ArrayList<>();
    
    /** Input slot for the X position parameter that controls the interpolation */
    private final Slot<Float> positionXSlot = new Slot<>();
    
    /** Input slot for the Y position parameter that controls the interpolation */
    private final Slot<Float> positionYSlot = new Slot<>();
    
    /** Output port that provides the interpolated pose result */
    private final OutputPort<Pose> outputPort = this::getPose;

    /**
     * Constructs a new BlendSpace2DNode with the specified blend space.
     * 
     * @param blendSpace the BlendSpace2D instance to use for pose interpolation
     */
    public BlendSpace2DNode(BlendSpace2D blendSpace) {
        this.blendSpace = blendSpace;
    }

    /**
     * Gets the underlying BlendSpace2D instance.
     * 
     * @return the BlendSpace2D used by this node
     */
    public BlendSpace2D getBlendSpace() {
        return blendSpace;
    }

    /**
     * Gets the list of sampler slots that provide poses to the blend space.
     * 
     * @return the list of Slot&lt;PoseSampler&gt; for pose inputs
     */
    public List<Slot<BlendSpace1DNode.PoseSampler>> getSamplerSlots() {
        return samplerSlots;
    }

    /**
     * Gets the input slot for the X position parameter.
     * 
     * @return the Slot for the X position input that controls interpolation
     */
    public Slot<Float> getPositionXSlot() {
        return positionXSlot;
    }

    /**
     * Gets the input slot for the Y position parameter.
     * 
     * @return the Slot for the Y position input that controls interpolation
     */
    public Slot<Float> getPositionYSlot() {
        return positionYSlot;
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
     * Performs the interpolation operation using the current pose samplers and positions.
     * 
     * <p>This method iterates through all sampler slots, updates the blend space with
     * the current poses, and then performs the interpolation based on the X and Y
     * position parameters.</p>
     * 
     * @return the interpolated pose result
     */
    public Pose getPose() {
        for (Slot<BlendSpace1DNode.PoseSampler> samplerSlot : samplerSlots) {
            BlendSpace1DNode.PoseSampler poseSampler = samplerSlot.get();
            if (poseSampler == null) {
                continue;
            }
            blendSpace.setSamplerPose(poseSampler.getIndex(), poseSampler.getPose());
        }
        return blendSpace.blend(Objects.requireNonNull(positionXSlot.get()), Objects.requireNonNull(positionYSlot.get()));
    }
}
