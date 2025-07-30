package com.maydaymemory.mae.basic;

import org.joml.Vector3fc;

import javax.annotation.Nonnull;

/**
 * Interpolator for Rotation in Euler Angles form
 */
public class EulerAnglesRotationInterpolator implements Interpolator<Rotation>{
    private final Interpolator<Vector3fc> interpolator;

    /**
     * Euler angles could be considered as vector, so this interpolator is just a wrapper for vector interpolator
     *
     * @param interpolator vector interpolator to wrap
     */
    public EulerAnglesRotationInterpolator(Interpolator<Vector3fc> interpolator) {
        this.interpolator = interpolator;
    }

    @Override
    public Rotation interpolate(InterpolatableChannel<Rotation> channel, int indexFrom, int indexTo, float alpha) {
        return new Rotation(interpolator.interpolate(new ChannelWrapper(channel, interpolator), indexFrom, indexTo, alpha));
    }

    @Override
    public Priority getPriority() {
        return interpolator.getPriority();
    }

    private static class ChannelWrapper implements InterpolatableChannel<Vector3fc>{
        private final InterpolatableChannel<Rotation> channel;
        private final Interpolator<Vector3fc> interpolator;

        public ChannelWrapper(InterpolatableChannel<Rotation> channel, Interpolator<Vector3fc> interpolator) {
            this.channel = channel;
            this.interpolator = interpolator;
        }

        @Override
        public Vector3fc compute(float timeS) {
            Rotation compute = channel.compute(timeS);
            if (!compute.isEulerAngles()) {
                throw new IllegalArgumentException("Rotation must be euler angles when using EulerAnglesRotationInterpolator");
            }
            return compute.getEulerAngles();
        }

        @Override
        public InterpolatableKeyframe<Vector3fc> getKeyFrame(int index) {
            InterpolatableKeyframe<Rotation> initialKeyframe = channel.getKeyFrame(index);
            return new KeyframeWrapper(initialKeyframe, interpolator);
        }

        @Override
        public int getKeyFrameCount() {
            return channel.getKeyFrameCount();
        }

        @Override
        public float getEndTimeS() {
            return channel.getEndTimeS();
        }
    }

    private static class KeyframeWrapper implements InterpolatableKeyframe<Vector3fc> {
        private final InterpolatableKeyframe<Rotation> initialKeyframe;
        private final Interpolator<Vector3fc> interpolator;

        public KeyframeWrapper(InterpolatableKeyframe<Rotation> initialKeyframe, Interpolator<Vector3fc> interpolator) {
            this.initialKeyframe = initialKeyframe;
            this.interpolator = interpolator;
        }

        @Override
        public Vector3fc getPre() {
            Rotation pre = initialKeyframe.getPre();
            if (!pre.isEulerAngles()) {
                throw new IllegalArgumentException("Rotation must be euler angles when using EulerAnglesRotationInterpolator");
            }
            return pre.getEulerAngles();
        }

        @Override
        public Vector3fc getPost() {
            Rotation post = initialKeyframe.getPost();
            if (!post.isEulerAngles()) {
                throw new IllegalArgumentException("Rotation must be euler angles when using EulerAnglesRotationInterpolator");
            }
            return post.getEulerAngles();
        }

        @Override
        public Interpolator<Vector3fc> getInterpolator() {
            return interpolator;
        }

        @Override
        public float getTimeS() {
            return initialKeyframe.getTimeS();
        }

        @Override
        public int compareTo(@Nonnull Keyframe<Vector3fc> other) {
            return Float.compare(this.getTimeS(), other.getTimeS());
        }

        @Override
        public Vector3fc getValue() {
            return getPre();
        }
    }
}
