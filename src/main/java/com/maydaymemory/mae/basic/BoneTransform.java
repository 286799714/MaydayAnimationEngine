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
 */
public class BoneTransform implements Comparable<BoneTransform> {
    private final int boneIndex;
    private final @Nonnull Vector3fc translation;
    private final @Nonnull RotationView rotation;
    private final @Nonnull Vector3fc scale;

    /**
     * Constructs a new BoneTransform with the specified bone index and transformation components.
     * 
     * @param boneIndex   The unique index of the bone within the skeleton hierarchy.
     * @param translation The bone's local-space translation vector.
     * @param rotation    The bone's local-space rotation, represented as a unit quaternion.
     * @param scale       The bone's local-space scale vector.
     */
    public BoneTransform(int boneIndex, Vector3fc translation, RotationView rotation, Vector3fc scale) {
        this.boneIndex = boneIndex;
        this.translation = translation;
        this.rotation = rotation;
        this.scale = scale;
    }

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

    /**
     * Gets the bone index of this transform.
     * 
     * @return the bone index
     */
    public int boneIndex() {
        return boneIndex;
    }

    /**
     * Gets the translation component of this transform.
     * 
     * @return the translation vector
     */
    public Vector3fc translation() {
        return translation;
    }

    /**
     * Gets the rotation component of this transform.
     * 
     * @return the rotation view
     */
    public RotationView rotation() {
        return rotation;
    }

    /**
     * Gets the scale component of this transform.
     * 
     * @return the scale vector
     */
    public Vector3fc scale() {
        return scale;
    }

    /**
     * An identity Transform which represents a bone with no transformation applied.
     *
     * <p><b>Important:</b> this should not be in any output pose,
     * it only serves as a replacement for null input to simplify processing logic</p>
     */
    public static final BoneTransform IDENTITY_TRANSFORM = new BoneTransform(
            -1,
            new Vector3f(0, 0, 0),
            RotationView.IDENTITY,
            new Vector3f(1, 1, 1)
    );

    /**
     * A zero vector constant used for representing zero translations, scales, or velocities.
     */
    public static final Vector3f ZERO_VECTOR = new Vector3f(0, 0, 0);
    /**
     * Represents a velocity with a value of 0 (including velocity, angular velocity, and scaling rate)
     *
     * <p><b>Important:</b> this should not be in any output pose,
     * it only serves as a replacement for null input to simplify processing logic</p>
     */
    public static final BoneTransform IDENTITY_VELOCITY = new BoneTransform(
            -1, ZERO_VECTOR, new RotationVelocityRotationView(ZERO_VECTOR), ZERO_VECTOR
    );
}
