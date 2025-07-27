package com.maydaymemory.mae.util;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

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

    public static long toNanos(float second) {
        return (long) (second * 1E9d);
    }

    public static Vector3f logUnit(Quaternionfc q) {
        float x = q.x();
        float y = q.y();
        float z = q.z();
        float w = q.w();
        double vLenSq = x * x + y * y + z * z;

        // 避免精度误差导致 w > 1
        double clampedW = Math.max(-1.0, Math.min(1.0, w));
        double theta = Math.acos(clampedW); // w = cos(θ)

        // 如果没有旋转（theta ≈ 0）
        if (vLenSq < 1e-12) {
            return new Vector3f(0, 0, 0);
        }

        double scale = theta / Math.sqrt(vLenSq);
        return new Vector3f((float) (x * scale), (float) (y * scale), (float) (z * scale));
    }

    public static Quaternionf exp(Vector3fc v) {
        float x = v.x();
        float y = v.y();
        float z = v.z();
        // v = (x, y, z)，标量部分应为 0
        double theta = Math.sqrt(x * x + y * y + z * z);

        if (theta < 1e-12) {
            // 没有旋转，返回单位四元数
            return new Quaternionf(0, 0, 0, 1);
        }

        double sinTheta = Math.sin(theta);
        double scale = sinTheta / theta;
        return new Quaternionf(x * scale, y * scale, z * scale, Math.cos(theta));
    }
}
