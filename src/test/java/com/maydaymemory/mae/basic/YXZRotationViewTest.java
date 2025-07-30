package com.maydaymemory.mae.basic;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class YXZRotationViewTest {

    private static final float EPSILON = 1e-4f;

    @Test
    public void testNormalAngles() {
        Vector3f angles = new Vector3f(0.5f, 0.3f, 0.7f); // x, y, z
        YXZRotationView view = new YXZRotationView(angles);

        Quaternionf quat = new Quaternionf(view.asQuaternion());
        YXZRotationView viewFromQuat = new YXZRotationView(quat);
        Vector3f resultAngles = new Vector3f(viewFromQuat.asEulerAngle());

        assertVector3fEquals(angles, resultAngles, EPSILON);
    }

    /**
     * Test the gimbal lock singularity: X = -90 degrees (-π/2)
     */
    @Test
    public void testGimbalLockMinusX90() {
        float xAngle = (float) (-Math.PI / 2);
        Vector3f angles = new Vector3f(xAngle, 0.3f, 0.7f);
        
        YXZRotationView view = new YXZRotationView(angles);
        Quaternionf quat = new Quaternionf(view.asQuaternion());

        YXZRotationView viewFromQuat = new YXZRotationView(quat);
        Vector3f resultAngles = new Vector3f(viewFromQuat.asEulerAngle());

        assertEquals(xAngle, resultAngles.x, EPSILON);

        float expectedCombinedAngle = angles.y + angles.z;
        float resultCombinedAngle = resultAngles.y + resultAngles.z;
        assertEquals(expectedCombinedAngle, resultCombinedAngle, EPSILON);
    }

    /**
     * Test the gimbal lock singularity: X = 90 degrees (π/2)
     */
    @Test
    public void testGimbalLockX90() {
        float xAngle = (float) (Math.PI / 2);
        Vector3f angles = new Vector3f(xAngle, 0.3f, 0.7f);
        
        YXZRotationView view = new YXZRotationView(angles);
        Quaternionf quat = new Quaternionf(view.asQuaternion());

        YXZRotationView viewFromQuat = new YXZRotationView(quat);
        Vector3f resultAngles = new Vector3f(viewFromQuat.asEulerAngle());

        assertEquals(xAngle, resultAngles.x, EPSILON);

        float expectedCombinedAngle = angles.z - angles.y;
        float resultCombinedAngle = resultAngles.z - resultAngles.y;
        assertEquals(expectedCombinedAngle, resultCombinedAngle, EPSILON);
    }

    /**
     * Testing the situation of approaching the gimbal lock
     */
    @Test
    public void testNearGimbalLock() {
        // 测试接近万向锁的情况：X = 89.9度
        float xAngle = (float) (Math.PI / 2 - 0.001f);
        Vector3f angles = new Vector3f(xAngle, 0.3f, 0.7f);
        
        YXZRotationView view = new YXZRotationView(angles);
        Quaternionf quat = new Quaternionf(view.asQuaternion());
        
        YXZRotationView viewFromQuat = new YXZRotationView(quat);
        Vector3f resultAngles = new Vector3f(viewFromQuat.asEulerAngle());

        // It should be possible to restore the original angle as much as possible, rather than the degraded angle
        assertVector3fEquals(angles, resultAngles, EPSILON);
    }

    /**
     * Testing edge case: X = 0 degrees
     */
    @Test
    public void testXZero() {
        Vector3f angles = new Vector3f(0f, 0.3f, 0.7f);
        YXZRotationView view = new YXZRotationView(angles);
        
        Quaternionf quat = new Quaternionf(view.asQuaternion());
        YXZRotationView viewFromQuat = new YXZRotationView(quat);
        Vector3f resultAngles = new Vector3f(viewFromQuat.asEulerAngle());
        
        assertVector3fEquals(angles, resultAngles, EPSILON);
    }

    /**
     * Test all angles to 0
     */
    @Test
    public void testZeroAngles() {
        Vector3f angles = new Vector3f(0f, 0f, 0f);
        YXZRotationView view = new YXZRotationView(angles);
        
        Quaternionf quat = new Quaternionf(view.asQuaternion());
        YXZRotationView viewFromQuat = new YXZRotationView(quat);
        Vector3f resultAngles = new Vector3f(viewFromQuat.asEulerAngle());
        
        assertVector3fEquals(angles, resultAngles, EPSILON);
    }

    /**
     * Test large angle value
     */
    @Test
    public void testLargeAngles() {
        Vector3f angles = new Vector3f((float)Math.PI, (float)Math.PI, (float)Math.PI);
        YXZRotationView view = new YXZRotationView(angles);
        
        Quaternionf quat = new Quaternionf(view.asQuaternion());
        YXZRotationView viewFromQuat = new YXZRotationView(quat);
        Vector3f resultAngles = new Vector3f(viewFromQuat.asEulerAngle());

        assertTrue(Math.abs(resultAngles.x) - Math.PI< EPSILON);
        assertTrue(Math.abs(resultAngles.y) - Math.PI< EPSILON);
        assertTrue(Math.abs(resultAngles.z) - Math.PI< EPSILON);
    }

    /**
     * Auxiliary method: Compare whether two vector are equal
     */
    private void assertVector3fEquals(Vector3f expected, Vector3f actual, float epsilon) {
        assertEquals(expected.x, actual.x, epsilon, "X component mismatch");
        assertEquals(expected.y, actual.y, epsilon, "Y component mismatch");
        assertEquals(expected.z, actual.z, epsilon, "Z component mismatch");
    }

    /**
     * Test the case of constructing from quaternion
     */
    @Test
    public void testConstructionFromQuaternion() {
        Vector3f initialAngles = new Vector3f((float)(Math.PI/2), 0.3f , 0.7f);
        Quaternionf quat = new Quaternionf().rotateYXZ(initialAngles.y, initialAngles.x, initialAngles.z);
        YXZRotationView view = new YXZRotationView(quat);
        
        Vector3f angles = new Vector3f(view.asEulerAngle());

        assertEquals((float)(Math.PI/2), angles.x, EPSILON);
        assertEquals(initialAngles.y - initialAngles.z, angles.y - angles.z, EPSILON);
    }
} 