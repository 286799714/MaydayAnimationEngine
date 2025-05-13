package com.maydaymemory.mae.basic;

import org.joml.Vector3f;
import org.joml.Vector3fc;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BasicAnimation implements Animation {
    private final String name;
    private float endTimeS = -1;

    // use array list so that we have the best in-order traversal performance
    // inserting tracks will be less efficient, but this usually doesn't need to be efficient,
    // because animations are generally static assets.
    private final ArrayList<ChannelBunch> channels = new ArrayList<>();

    private final Supplier<PoseBuilder> poseBuilderSupplier;
    private final BoneTransformFactory transformFactory;

    private static final Vector3f IDENTITY_TRANSLATION = new Vector3f(0, 0, 0);
    private static final Vector3f IDENTITY_ROTATION = new Vector3f(0, 0, 0);
    private static final Vector3f IDENTITY_SCALE = new Vector3f(1, 1, 1);

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
    public void setTranslationChannel(int i, @Nonnull InterpolatableChannel<? extends Vector3fc> channel) {
        getOrCreateChannelBunchAnd(i, channelBunch -> channelBunch.translationChannel = channel);
        // Mark end time as dirty, when getting,
        // all channels will be traversed again to calculate the new endTime and then cache it
        endTimeS = -1;
    }

    @Override
    public void setScaleChannel(int i, @Nonnull InterpolatableChannel<? extends Vector3fc> channel) {
        getOrCreateChannelBunchAnd(i, channelBunch -> channelBunch.scaleChannel = channel);
        endTimeS = -1;
    }

    @Override
    public void setRotationChannel(int i, @Nonnull InterpolatableChannel<? extends Vector3fc> channel) {
        getOrCreateChannelBunchAnd(i, channelBunch -> channelBunch.rotationChannel = channel);
        endTimeS = -1;
    }

    @Override
    public Pose evaluate(float timeS) {
        PoseBuilder poseBuilder = poseBuilderSupplier.get();
        for(ChannelBunch channelBunch : channels) {
            InterpolatableChannel<? extends Vector3fc> translationChannel = channelBunch.translationChannel;
            InterpolatableChannel<? extends Vector3fc> rotationChannel = channelBunch.rotationChannel;
            InterpolatableChannel<? extends Vector3fc> scaleChannel = channelBunch.scaleChannel;
            Vector3fc translation = translationChannel == null ? IDENTITY_TRANSLATION : translationChannel.compute(timeS);
            Vector3fc rotation = rotationChannel == null ? IDENTITY_ROTATION : rotationChannel.compute(timeS);
            Vector3fc scale = scaleChannel == null ? IDENTITY_SCALE : scaleChannel.compute(timeS);
            BoneTransform boneTransform = transformFactory.createBoneTransform(channelBunch.boneIndex, translation, rotation, scale);
            poseBuilder.addBoneTransform(boneTransform);
        }
        return poseBuilder.toPose();
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
        private InterpolatableChannel<? extends Vector3fc> translationChannel;
        private InterpolatableChannel<? extends Vector3fc> rotationChannel;
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
