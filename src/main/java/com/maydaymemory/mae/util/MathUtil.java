package com.maydaymemory.mae.util;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class MathUtil {
    public static Quaternionf nlerpShortestPath(Quaternionfc q1, Quaternionfc q2, float t) {
        float dot = q1.x() * q2.x() + q1.y() * q2.y() + q1.z() * q2.z() + q1.w() * q2.w();
        Quaternionf q2copy;
        if (dot < 0) {
            q2copy = new Quaternionf(-q2.x(), -q2.y(), -q2.z(), -q2.w());
        } else {
            q2copy = new Quaternionf(q2);
        }
        return q1.nlerp(q2copy, t, q2copy);
    }

    public static float toSecond(long nanos) {
        return nanos / 1E9f;
    }
}
