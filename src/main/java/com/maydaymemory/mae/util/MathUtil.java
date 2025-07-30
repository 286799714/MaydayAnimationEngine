package com.maydaymemory.mae.util;

import org.joml.*;

import org.joml.Math;

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

    private static final double a1 =  0.254829592;
    private static final double a2 = -0.284496736;
    private static final double a3 =  1.421413741;
    private static final double a4 = -1.453152027;
    private static final double a5 =  1.061405429;
    private static final double p  =  0.3275911;

    public static double erf(double x) {
        // 保存原始值
        double sign = (x >= 0) ? 1 : -1;
        x = Math.abs(x);

        // 近似计算
        double t = 1.0 / (1.0 + p * x);
        double y = 1.0 - (((((a5 * t + a4) * t) + a3) * t + a2) * t + a1) * t * Math.exp(-x * x);

        return sign * y;
    }

    /**
     * This is a version with singularity handling. In the case of gimbal lock,
     * the rotation results of the two overlapping axes will be added together and assigned to x.
     *
     * @param matrix the rotation matrix.
     * @return the euler angles in ZYX order.
     */
    public static Vector3f extractEulerAnglesZYX(Matrix3fc matrix) {
        Vector3f euler = new Vector3f();

        float r13 = matrix.m02();
        float r23 = matrix.m12();
        float r33 = matrix.m22();
        float r11 = matrix.m00();
        float r12 = matrix.m01();

        final float EPSILON = 1e-4f;

        if (Math.abs(r13) < 1.0f - EPSILON) {
            euler.y = Math.asin(-r13);
            euler.x = Math.atan2(r23, r33);
            euler.z = Math.atan2(r12, r11);
        } else {
            float r21 = matrix.m10();
            float r22 = matrix.m11();
            if (Math.abs(r11) < EPSILON && Math.abs(r12) < EPSILON &&
                    Math.abs(r23) < EPSILON && Math.abs(r33) < EPSILON) {
                // gimbal lock
                euler.y = (float) (r13 > 0 ? -Math.PI / 2 : Math.PI / 2);
                euler.z = Math.atan2(-(r21), r22);
                euler.x = 0;
            } else {
                euler.y = Math.asin(-r13);
                euler.x = Math.atan2(r23, r33);
                euler.z = Math.atan2(r12, r11);
            }
        }

        return euler;
    }

    /**
     * This is a version with singularity handling. In the case of gimbal lock,
     * the rotation results of the two overlapping axes will be added together and assigned to z.
     *
     * @param matrix the rotation matrix.
     * @return the euler angles in YXZ order.
     */
    public static Vector3f extractEulerAnglesYXZ(Matrix3fc matrix) {
        Vector3f euler = new Vector3f();

        // r32 = -sin(x)
        // r12 = cos(x) sin(z)      r22 = cos(x) cos(z)
        // r31 = cos(x) sin(y)      r33 = cos(x) cos(y)
        float r32 = matrix.m21();
        float r12 = matrix.m01();
        float r22 = matrix.m11();
        float r31 = matrix.m20();
        float r33 = matrix.m22();

        final float EPSILON = 1e-5f;

        if (Math.abs(r32) < 1.0f - EPSILON) {
            euler.x = Math.asin(-r32);
            euler.y = Math.atan2(r31, r33);
            euler.z = Math.atan2(r12, r22);
        } else {
            if (Math.abs(r31) < EPSILON && Math.abs(r33) < EPSILON &&
                    Math.abs(r12) < EPSILON && Math.abs(r22) < EPSILON) {
                euler.x = (float) (r32 > 0 ? -Math.PI / 2 : Math.PI / 2);
                // when x ≈ -90°
                // r21 ≈ -(sin(y)cos(z) + cos(y)sin(z))
                // r11 ≈ cos(y)cos(z) - sin(y)sin(z)
                // when x ≈ 90°
                // r21 ≈ sin(y)cos(z) - cos(y)sin(z)
                // r11 ≈ cos(y)cos(z) + sin(y)sin(z)
                float r21 = matrix.m10();
                float r11 = matrix.m00();
                euler.z = (float) java.lang.Math.atan2(-r21, r11);
                euler.y = 0;
            } else {
                euler.x = (float) java.lang.Math.asin(-r32);
                euler.y = (float) java.lang.Math.atan2(r31, r33);
                euler.z = (float) java.lang.Math.atan2(r12, r22);
            }
        }

        return euler;
    }
}
