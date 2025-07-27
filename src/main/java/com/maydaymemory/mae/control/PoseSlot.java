package com.maydaymemory.mae.control;

import com.maydaymemory.mae.basic.DummyPose;
import com.maydaymemory.mae.basic.Pose;

/**
 * Specialized slot for {@link Pose} values that guarantees a non-null return value.
 *
 * <p>This class extends {@link Slot} for {@link Pose} types and overrides the
 * {@code get()} method to always return a valid {@link Pose} instance. If the slot
 * is not connected and no default value is set, it returns {@link DummyPose#INSTANCE}
 * as a safe fallback. This ensures that pose consumers never receive a null value.</p>
 *
 * <p>This is useful in animation systems where a valid pose is always required
 * for blending, evaluation, or rendering, and null values could cause errors.</p>
 *
 * @author MaydayMemory
 * @since 1.0.1
 */
public class PoseSlot extends Slot<Pose> {
    /**
     * Retrieves the current pose from the connected output port, the default value,
     * or returns {@link DummyPose#INSTANCE} if neither is available.
     *
     * @return a non-null {@link Pose} instance
     */
    @Override
    public Pose get() {
        Pose pose = super.get();
        return pose != null ? pose : DummyPose.INSTANCE;
    }
}
