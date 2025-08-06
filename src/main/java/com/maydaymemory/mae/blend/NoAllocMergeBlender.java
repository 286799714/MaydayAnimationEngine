package com.maydaymemory.mae.blend;

import com.maydaymemory.mae.basic.BoneTransform;
import com.maydaymemory.mae.basic.DummyPose;
import com.maydaymemory.mae.basic.Pose;

import java.util.*;

public class NoAllocMergeBlender implements MergeBlender{
    @Override
    public Pose blend(List<Pose> poses) {
        if (poses.isEmpty()) {
            return DummyPose.INSTANCE;
        }
        if (poses.size() == 1) {
            return poses.get(0);
        }
        return () -> () -> new MergedIterator(poses);
    }

    private static class MergedIterator implements Iterator<BoneTransform> {
        private final List<Entry> entries = new LinkedList<>();

        public MergedIterator(List<Pose> poses) {
            for (Pose pose : poses) {
                Iterable<BoneTransform> transforms = pose.getBoneTransforms();
                Iterator<BoneTransform> it = transforms.iterator();
                if (it.hasNext()) {
                    BoneTransform val = it.next();
                    entries.add(new Entry(val, it));
                }
            }
        }

        @Override
        public boolean hasNext() {
            return !entries.isEmpty();
        }

        @Override
        public BoneTransform next() {
            if (entries.isEmpty()) throw new NoSuchElementException();
            int boneIndex = Integer.MAX_VALUE;
            Entry resultEntry = null;
            for (Entry entry : entries) {
                if (entry.value.boneIndex() <= boneIndex) {
                    boneIndex = entry.value.boneIndex();
                    resultEntry = entry;
                }
            }
            Iterator<Entry> it = entries.iterator();
            while (it.hasNext()) {
                Entry entry = it.next();
                if (entry.value.boneIndex() == boneIndex) {
                    if (entry.iterator.hasNext()) {
                        entry.value = entry.iterator.next();
                    } else {
                        it.remove();
                    }
                }
            }
            return resultEntry.value;
        }

        private static class Entry {
            BoneTransform value;
            final Iterator<BoneTransform> iterator;

            Entry(BoneTransform value, Iterator<BoneTransform> iterator) {
                this.value = value;
                this.iterator = iterator;
            }
        }
    }
}
