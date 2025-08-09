package com.maydaymemory.mae.control.blend;

import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.blend.DummyLayerBlend;
import com.maydaymemory.mae.blend.LayerBlend;
import com.maydaymemory.mae.blend.LayeredBlender;
import com.maydaymemory.mae.control.OutputPort;
import com.maydaymemory.mae.control.PoseSlot;
import com.maydaymemory.mae.control.Slot;

/**
 * A control node that wraps a {@link LayeredBlender} for layered pose blending operations.
 * 
 * <p>This node provides a control interface for layered blending, which combines
 * a base pose with an input pose using a layer blend strategy. Layered blending
 * is commonly used in animation systems to apply different blending rules to
 * different parts of a skeleton or to create complex blending behaviors.</p>
 * 
 * <p>The node manages input slots for the base pose, input pose, and layer blend
 * strategy, providing an output port that delivers the layered blend result.
 * If no layer blend is provided, a DummyLayerBlend is used as a fallback.</p>
 * 
 * @author MaydayMemory
 * @since 1.0.1
 */
public class LayeredBlenderNode {
    /** The underlying layered blender that performs the actual blending operation */
    private final LayeredBlender blender;
    
    /** Input slot for the layer blend strategy that controls how poses are combined */
    private final Slot<LayerBlend> layerSlot = new Slot<>();
    
    /** Input slot for the base pose to be blended */
    private final Slot<Pose> basePoseSlot = new PoseSlot();
    
    /** Input slot for the input pose to be blended with the base pose */
    private final Slot<Pose> inputPoseSlot = new PoseSlot();
    
    /** Output port that provides the layered blend pose result */
    private final OutputPort<Pose> outputPort = this::getPose;

    /**
     * Constructs a new LayeredBlenderNode with the specified blender.
     * 
     * @param blender the LayeredBlender instance to use for pose blending
     * @throws IllegalArgumentException if blender is null
     */
    public LayeredBlenderNode(LayeredBlender blender) {
        this.blender = blender;
    }

    /**
     * Gets the underlying LayeredBlender instance.
     * 
     * @return the LayeredBlender used by this node
     */
    public LayeredBlender getBlender() {
        return blender;
    }

    /**
     * Gets the input slot for the layer blend strategy.
     * 
     * @return the Slot for the layer blend input that controls blending behavior
     */
    public Slot<LayerBlend> getLayerSlot() {
        return layerSlot;
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
     * Gets the output port that provides the layered blend pose.
     * 
     * @return the OutputPort for the layered blend pose result
     */
    public OutputPort<Pose> getOutputPort() {
        return outputPort;
    }

    /**
     * Performs the layered blending operation using the current poses and layer strategy.
     * 
     * <p>This method retrieves the poses from the base and input pose slots along with
     * the layer blend strategy, and uses the underlying LayeredBlender to perform
     * the layered blending. If no layer blend is provided, a DummyLayerBlend is used
     * as a fallback to ensure the operation can always proceed.</p>
     * 
     * @return the layered blend pose result
     */
    public Pose getPose() {
        LayerBlend layer = layerSlot.get();
        return blender.blend(basePoseSlot.get(), inputPoseSlot.get(), layer == null ? DummyLayerBlend.ZERO_WEIGHT_DUMMY : layer);
    }
}
