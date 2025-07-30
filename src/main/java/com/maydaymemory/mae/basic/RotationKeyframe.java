package com.maydaymemory.mae.basic;

/**
 * Represents a keyframe of rotation.
 *
 * <p>
 * <b>Note:</b> Unless you know exactly what you are doing,it is recommended that all Rotation Keyframes
 * in the same animation channel are either all Euler Angles or all Quaternion Rotations.
 * </p>
 */
public class RotationKeyframe extends BaseKeyframe<Rotation> implements InterpolatableKeyframe<Rotation>{
    private final Rotation pre;
    private final Rotation post;
    private final Interpolator<Rotation> interpolator;

    /**
     * Constructs a new Rotation Keyframe.
     * @param timeS the time of the keyframe in seconds
     * @param pre the pre-interpolation value, which will be used to interpolate with the previous keyframe.
     * @param post the post-interpolation value, which will be used to interpolate with the subsequent keyframe.
     * @param interpolator interpolator of this keyframe using.
     */
    public RotationKeyframe(float timeS,
                            Rotation pre,
                            Rotation post,
                            Interpolator<Rotation> interpolator) {
        super(timeS);
        this.pre = pre;
        this.post = post;
        this.interpolator = interpolator;
    }

    @Override
    public Rotation getPre() {
        return pre;
    }

    @Override
    public Rotation getPost() {
        return post;
    }

    @Override
    public Interpolator<Rotation> getInterpolator() {
        return interpolator;
    }

    @Override
    public Rotation getValue() {
        return getPre();
    }
}
