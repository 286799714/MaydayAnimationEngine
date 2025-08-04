package com.maydaymemory.mae.basic;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Basic implementation of Animation
 */
public class BasicAnimation implements Animation {
    private final String name;
    private float endTimeS = -1;

    // use array list so that we have the best in-order traversal performance
    // inserting tracks will be less efficient, but this usually doesn't need to be efficient,
    // because animations are generally static assets.
    private final ArrayList<ChannelBunch> channels = new ArrayList<>();
    private final ArrayList<ClipChannel<?>> clipChannels = new ArrayList<>();
    private final ArrayList<InterpolatableChannel<?>> curves = new ArrayList<>();

    private final Supplier<PoseBuilder> poseBuilderSupplier;
    private final BoneTransformFactory transformFactory;

    private static final Vector3f IDENTITY_TRANSLATION = new Vector3f(0, 0, 0);
    private static final Quaternionf IDENTITY_ROTATION = new Quaternionf(0, 0, 0, 1);
    private static final Vector3f IDENTITY_SCALE = new Vector3f(1, 1, 1);

    /**
     * Construct an animation.
     *
     * @param name the name of the animation
     * @param boneTransformFactory the factory to create bone transforms
     * @param poseBuilderSupplier the supplier of pose builders
     */
    public BasicAnimation(String name,
                          BoneTransformFactory boneTransformFactory,
                          Supplier<PoseBuilder> poseBuilderSupplier) {
        this.name = name;
        this.poseBuilderSupplier = poseBuilderSupplier;
        transformFactory = boneTransformFactory;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setTranslationChannel(int boneIndex, @Nullable InterpolatableChannel<? extends Vector3fc> channel) {
        getOrCreateChannelBunchAnd(boneIndex, channelBunch -> channelBunch.translationChannel = channel);
        // Mark end time as dirty, when getting,
        // all channels will be traversed again to calculate the new endTime and then cache it
        endTimeS = -1;
    }

    @Override
    public void setScaleChannel(int boneIndex, @Nullable InterpolatableChannel<? extends Vector3fc> channel) {
        getOrCreateChannelBunchAnd(boneIndex, channelBunch -> channelBunch.scaleChannel = channel);
        endTimeS = -1;
    }

    @Override
    public void setRotationChannel(int boneIndex, @Nullable InterpolatableChannel<? extends Rotation> channel) {
        getOrCreateChannelBunchAnd(boneIndex, channelBunch -> channelBunch.rotationChannel = channel);
        endTimeS = -1;
    }

    @Override
    public Pose evaluate(float timeS) {
        PoseBuilder poseBuilder = poseBuilderSupplier.get();
        for(ChannelBunch channelBunch : channels) {
            InterpolatableChannel<? extends Vector3fc> translationChannel = channelBunch.translationChannel;
            InterpolatableChannel<? extends Rotation> rotationChannel = channelBunch.rotationChannel;
            InterpolatableChannel<? extends Vector3fc> scaleChannel = channelBunch.scaleChannel;
            Vector3fc translation = translationChannel == null ? IDENTITY_TRANSLATION : translationChannel.compute(timeS);
            Rotation rotation = rotationChannel == null ? null : rotationChannel.compute(timeS);
            Vector3fc scale = scaleChannel == null ? IDENTITY_SCALE : scaleChannel.compute(timeS);
            BoneTransform boneTransform;
            if (rotation == null) {
                boneTransform = transformFactory.createBoneTransform(channelBunch.boneIndex, translation, IDENTITY_ROTATION, scale);
            } else if (rotation.isEulerAngles()) {
                boneTransform = transformFactory.createBoneTransform(channelBunch.boneIndex, translation, rotation.getEulerAngles(), scale);
            } else {
                boneTransform = transformFactory.createBoneTransform(channelBunch.boneIndex, translation, rotation.getQuaternion(), scale);
            }
            poseBuilder.addBoneTransform(boneTransform);
        }
        return poseBuilder.toPose();
    }

    @Override
    public void setClipChannel(int i, ClipChannel<?> channel) {
        int size = clipChannels.size();
        if (i == size) {
            clipChannels.add(channel);
        } else if (i > size) { // ensure array capability
            for (int j = i; j > size; j--) {
                clipChannels.add(null);
            }
            clipChannels.add(channel);
        } else {
            clipChannels.set(i, channel);
        }
        endTimeS = -1;
    }

    @Override
    public List<Iterable<?>> clip(float fromTimeS, float toTimeS) {
        ArrayList<Iterable<?>> results = new ArrayList<>(clipChannels.size());
        for (ClipChannel<?> clipChannel : clipChannels) {
            if (clipChannel != null) {
                results.add(clipChannel.clip(fromTimeS, toTimeS));
            } else {
                results.add(null);
            }
        }
        return results;
    }

    @Nullable
    @Override
    public Iterable<?> clip(int i, float fromTimeS, float toTimeS) {
        if (i >= clipChannels.size() || i < 0) {
            return null;
        }
        ClipChannel<?> channel = clipChannels.get(i);
        if (channel != null) {
            return channel.clip(fromTimeS, toTimeS);
        }
        return null;
    }

    @Override
    public void setCurve(int i, @Nullable InterpolatableChannel<?> curve) {
        int size = curves.size();
        if (i == size) {
            curves.add(curve);
        } else if (i > size) { // ensure array capability
            for (int j = i; j > size; j--) {
                curves.add(null);
            }
            curves.add(curve);
        } else {
            curves.set(i, curve);
        }
        endTimeS = -1;
    }

    @Override
    public List<Object> evaluateCurve(float timeS) {
        ArrayList<Object> results = new ArrayList<>(curves.size());
        for (InterpolatableChannel<?> curve : curves) {
            if (curve != null) {
                results.add(curve.compute(timeS));
            } else {
                results.add(null);
            }
        }
        return results;
    }

    @Nullable
    @Override
    public Object evaluateCurve(int i, float timeS) {
        if (i >= curves.size() || i < 0) {
            return null;
        }
        InterpolatableChannel<?> curve = curves.get(i);
        if (curve != null) {
            return curve.compute(timeS);
        }
        return null;
    }

    @Override
    public float getEndTimeS() {
        if (endTimeS == -1) {
            endTimeS = 0;
            for (ChannelBunch channelBunch : channels) {
                if (channelBunch.translationChannel != null) {
                    endTimeS = Math.max(channelBunch.translationChannel.getEndTimeS(), endTimeS);
                }
                if (channelBunch.rotationChannel != null) {
                    endTimeS = Math.max(channelBunch.rotationChannel.getEndTimeS(), endTimeS);
                }
                if (channelBunch.scaleChannel != null) {
                    endTimeS = Math.max(channelBunch.scaleChannel.getEndTimeS(), endTimeS);
                }
            }
            for (ClipChannel<?> channel : clipChannels) {
                if (channel != null) {
                    endTimeS = Math.max(channel.getEndTimeS(), endTimeS);
                }
            }
            for (InterpolatableChannel<?> channel : curves) {
                if (channel != null) {
                    endTimeS = Math.max(channel.getEndTimeS(), endTimeS);
                }
            }
        }
        return endTimeS;
    }

    private void getOrCreateChannelBunchAnd(int boneIndex, Consumer<ChannelBunch> consumer) {
        if (channels.isEmpty() || channels.get(channels.size() - 1).boneIndex < boneIndex) {
            ChannelBunch channelBunch = new ChannelBunch(boneIndex);
            consumer.accept(channelBunch);
            channels.add(channelBunch);
            return;
        }
        // Make a special judgment on the last element,
        // so that if the user inserts the channel in ascending order,
        // the average time complexity can be reduced to o(1)
        ChannelBunch lastChannelBunch = channels.get(channels.size() - 1);
        if (lastChannelBunch.boneIndex == boneIndex) {
            consumer.accept(lastChannelBunch);
            return;
        }
        ChannelBunch channelBunch = new ChannelBunch(boneIndex);
        int i = Collections.binarySearch(channels, channelBunch);
        if (i < 0) {
            i = -i - 1;
            channels.add(i, channelBunch);
            consumer.accept(channelBunch);
        } else {
            consumer.accept(channels.get(i));
        }
    }

    private static class ChannelBunch implements Comparable<ChannelBunch> {
        private final int boneIndex;
        @Nullable
        private InterpolatableChannel<? extends Vector3fc> translationChannel;
        @Nullable
        private InterpolatableChannel<? extends Rotation> rotationChannel;
        @Nullable
        private InterpolatableChannel<? extends Vector3fc> scaleChannel;

        public ChannelBunch(int boneIndex) {
            this.boneIndex = boneIndex;
        }

        @Override
        public int compareTo(ChannelBunch o) {
            return Integer.compare(boneIndex, o.boneIndex);
        }
    }
}
