package com.maydaymemory.mae.basic;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ArrayClipChannelTest {
    @Test
    public void testArrayClipChannel() {
        ArrayClipChannel<String> clipChannel = new ArrayClipChannel<>();
        float v1 = 0.2f;
        float v2 = 0.2f - Math.ulp(0.2f);
        float v3 = 0.2f + Math.ulp(0.2f);
        clipChannel.add(new StringKeyFrame("test", v1));
        clipChannel.refresh();
        Iterable<Keyframe<String>> clip2 = clipChannel.clip(v1, v3);
        Iterable<Keyframe<String>> clip1 = clipChannel.clip(v2 - Math.ulp(v2), v2);
        Iterable<Keyframe<String>> clip3 = clipChannel.clip(v2, v1);
        Iterable<Keyframe<String>> clip4 = clipChannel.clip(v1, v2);
        assertFalse(clip1.iterator().hasNext());
        assertTrue(clip2.iterator().hasNext());
        assertFalse(clip3.iterator().hasNext());
        assertTrue(clip4.iterator().hasNext());
        for (int i = 0; i < 100000; ++i) {
            double random1 = Math.random() * 4.0;
            double random2 = Math.random() * 4.0;
            clipChannel.clip((float) random1, (float) random2);
        }
        clipChannel.remove(0);
        clipChannel.refresh();
        clip1 = clipChannel.clip(v1, v1);
        clip2 = clipChannel.clip(v1, v3);
        clip3 = clipChannel.clip(v2, v1);
        clip4 = clipChannel.clip(v1, v2);
        assertFalse(clip1.iterator().hasNext());
        assertFalse(clip2.iterator().hasNext());
        assertFalse(clip3.iterator().hasNext());
        assertFalse(clip4.iterator().hasNext());
    }
}
