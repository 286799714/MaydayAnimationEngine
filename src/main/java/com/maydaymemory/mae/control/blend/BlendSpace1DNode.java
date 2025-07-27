package com.maydaymemory.mae.control.blend;

import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.blend.BlendSpace1D;
import com.maydaymemory.mae.control.OutputPort;
import com.maydaymemory.mae.control.Slot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A control node that wraps a {@link BlendSpace1D} for one-dimensional pose blending operations.
 * 
 * <p>This node provides a control interface for one-dimensional blend spaces, which
 * allow smooth interpolation between multiple poses based on a single parameter
 * (position). The node manages a collection of pose samplers and a position input,
 * providing an output port that delivers the interpolated pose result.</p>
 * 
 * <p>The blend space concept is commonly used in animation systems for creating
 * smooth transitions between different poses based on continuous parameters
 * such as speed, direction, or other game state variables.</p>
 * 
 * @author MaydayMemory
 * @since 1.0.1
 */
public class BlendSpace1DNode{
    /** The underlying one-dimensional blend space that performs the interpolation */
    private final BlendSpace1D blendSpace;
    
    /** List of slots for pose samplers that provide poses to the blend space */
    private final List<Slot<PoseSampler>> samplerSlots = new ArrayList<>();
    
    /** Input slot for the position parameter that controls the interpolation */
    private final Slot<Float> positionSlot = new Slot<>();
    
    /** Output port that provides the interpolated pose result */
    private final OutputPort<Pose> outputPort = this::getPose;

    /**
     * Constructs a new BlendSpace1DNode with the specified blend space.
     * 
     * @param blendSpace the BlendSpace1D instance to use for pose interpolation
     */
    public BlendSpace1DNode(BlendSpace1D blendSpace) {
        this.blendSpace = blendSpace;
    }

    /**
     * Gets the underlying BlendSpace1D instance.
     * 
     * @return the BlendSpace1D used by this node
     */
    public BlendSpace1D getBlendSpace() {
        return blendSpace;
    }

    /**
     * Gets the list of sampler slots that provide poses to the blend space.
     * 
     * @return the list of Slot<PoseSampler> for pose inputs
     */
    public List<Slot<PoseSampler>> getSamplerSlots() {
        return samplerSlots;
    }

    /**
     * Gets the input slot for the position parameter.
     * 
     * @return the Slot for the position input that controls interpolation
     */
    public Slot<Float> getPositionSlot() {
        return positionSlot;
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
     * Performs the interpolation operation using the current pose samplers and position.
     * 
     * This method iterates through all sampler slots, updates the blend space with
     * the current poses, and then performs the interpolation based on the position
     * parameter.
     * 
     * @return the interpolated pose result
     */
    public Pose getPose() {
        for (Slot<PoseSampler> samplerSlot : samplerSlots) {
            PoseSampler poseSampler = samplerSlot.get();
            if (poseSampler == null) {
                continue;
            }
            blendSpace.setSamplerPose(poseSampler.getIndex(), poseSampler.getPose());
        }
        return blendSpace.blend(Objects.requireNonNull(positionSlot.get()));
    }

   
    public static class PoseSampler {
        private final int index;
        
        private final Pose pose;

        public PoseSampler(int index, Pose pose) {
            this.index = index;
            this.pose = pose;
        }

        public int getIndex() {
            return index;
        }

        public Pose getPose() {
            return pose;
        }
    }
}
