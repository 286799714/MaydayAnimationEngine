package com.maydaymemory.mae.basic;

public interface PoseBuilder {
    /**
     * Adds a {@link BoneTransform} to this pose in ascending order of bone index.
     * <p>
     * The {@code boneTransform.boneIndex()} value must be greater than the last added bone index.
     * If this condition is violated, an {@link IllegalArgumentException} will be thrown.
     *
     * @param boneTransform the bone transform to add
     * @throws IllegalArgumentException if the bone index is not in ascending order
     * @throws IllegalStateException if the builder is finalized via {@code toPose()}
     */
    void addBoneTransform(BoneTransform boneTransform);

    /**
     * Creates a new {@link Pose} instance from the added bone transforms.
     * <p>
     * The returned pose contains all bone transforms previously added via {@link #addBoneTransform(BoneTransform)}.
     * The resulting pose is immutable and can be used independently of the builder state.
     * After building, the builder may be in a non-buildable state, depending on the specific implementation of the Builder.
     *
     * @return a new {@link Pose} instance constructed from the current bone transforms
     * @throws IllegalStateException if the builder is finalized via {@code toPose()}
     */
    Pose toPose();
}
