package com.maydaymemory.mae.basic;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import javax.annotation.Nonnull;

/**
 * Represents the local transformation of a single bone.
 * <p>
 * {@code BoneTransform} is an immutable data record that describes a bone's local-space translation,
 * rotation (as a unit quaternion), and scale. It is typically used in animation systems for defining poses,
 * blending between multiple poses, or interpolating between animation frames.
 * </p>
 * <p>
 * All transformations are defined in local space, meaning they are relative to the bone's parent.
 * This class implements {@link Comparable} to allow sorting based on {@code boneIndex},
 * which facilitates efficient merging of sorted pose data during blending.
 * </p>
 *
 * @param boneIndex   The unique index of the bone within the skeleton hierarchy.
 * @param translation The bone's local-space translation vector.
 * @param rotation    The bone's local-space rotation, represented as a unit quaternion.
 * @param scale       The bone's local-space scale vector.
 */
public record BoneTransform (
        int boneIndex,
        @Nonnull Vector3fc translation,
        @Nonnull RotationView rotation,
        @Nonnull Vector3fc scale
) implements Comparable<BoneTransform> {
    /**
     * Compares this {@link BoneTransform} with another {@link BoneTransform} by their {@code boneIndex}.
     * <p>
     * This method ensures that {@link BoneTransform} objects are sorted in ascending order based on their
     * {@code boneIndex}. Sorting by {@code boneIndex} is crucial for operations such as pose blending, where
     * bone transforms need to be processed in a consistent and predictable order. The sorting also ensures that
     * transformations can be applied correctly when combining multiple poses.
     * </p>
     *
     * @param transform the {@link BoneTransform} to be compared.
     * @return a negative integer, zero, or a positive integer as this {@link BoneTransform}'s {@code boneIndex}
     *         is less than, equal to, or greater than the specified {@code boneIndex}.
     */
    @Override
    public int compareTo(@Nonnull BoneTransform transform) {
        return Integer.compare(this.boneIndex, transform.boneIndex);
    }

    public static final BoneTransform IDENTITY_TRANSFORM = new BoneTransform(
            -1,
            new Vector3f(0, 0, 0),
            RotationView.IDENTITY,
            new Vector3f(1, 1, 1)
    );
}
