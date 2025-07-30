package com.maydaymemory.mae.basic;

import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ZYXRotationViewTest {

    private static final float EPSILON = 1e-4f;

    @Test
    public void testNormalAngles() {
        Vector3f angles = new Vector3f(0.5f, 0.3f, 0.7f);
        ZYXRotationView view = new ZYXRotationView(angles);

        Quaternionf quat = new Quaternionf(view.asQuaternion());
        ZYXRotationView viewFromQuat = new ZYXRotationView(quat);
        Vector3f resultAngles = new Vector3f(viewFromQuat.asEulerAngle());

        assertVector3fEquals(angles, resultAngles);
    }

    /**
     * Test the gimbal lock singularity: Y = -90 degrees (-π/2)
     */
    @Test
    public void testGimbalLockMinusY90() {
        float yAngle = (float) (-Math.PI / 2);
        Vector3f angles = new Vector3f(0.5f, yAngle, 0.7f);
        
        ZYXRotationView view = new ZYXRotationView(angles);
        Quaternionf quat = new Quaternionf(view.asQuaternion());

        ZYXRotationView viewFromQuat = new ZYXRotationView(quat);
        Vector3f resultAngles = new Vector3f(viewFromQuat.asEulerAngle());

        assertEquals(yAngle, resultAngles.y, EPSILON);

        float expectedCombinedAngle = angles.x + angles.z;
        float resultCombinedAngle = resultAngles.x + resultAngles.z;
        assertEquals(expectedCombinedAngle, resultCombinedAngle, EPSILON);
    }

    /**
     * Test the gimbal lock singularity: Y = 90 degrees (π/2)
     */
    @Test
    public void testGimbalLockY90() {
        float yAngle = (float) (Math.PI / 2);
        Vector3f angles = new Vector3f(0.5f, yAngle, 0.7f);
        
        ZYXRotationView view = new ZYXRotationView(angles);
        Quaternionf quat = new Quaternionf(view.asQuaternion());

        ZYXRotationView viewFromQuat = new ZYXRotationView(quat);
        Vector3f resultAngles = new Vector3f(viewFromQuat.asEulerAngle());

        assertEquals(yAngle, resultAngles.y, EPSILON);

        float expectedCombinedAngle = angles.x - angles.z;
        float resultCombinedAngle = resultAngles.x - resultAngles.z;
        assertEquals(expectedCombinedAngle, resultCombinedAngle, EPSILON);
    }

    /**
     * Testing the situation of approaching the gimbal lock
     */
    @Test
    public void testNearGimbalLock() {
        float yAngle = (float) (Math.PI / 2 - 0.001f); // 0.05 degrees
        Vector3f angles = new Vector3f(0.5f, yAngle, 0.7f);
        
        ZYXRotationView view = new ZYXRotationView(angles);
        Quaternionf quat = new Quaternionf(view.asQuaternion());
        
        ZYXRotationView viewFromQuat = new ZYXRotationView(quat);
        Vector3f resultAngles = new Vector3f(viewFromQuat.asEulerAngle());
        
        // It should be possible to restore the original angle as much as possible, rather than the degraded angle
        assertVector3fEquals(angles, resultAngles);
    }

    /**
     * Testing edge case: Y = 0 degrees
     */
    @Test
    public void testYZero() {
        Vector3f angles = new Vector3f(0.5f, 0f, 0.7f);
        ZYXRotationView view = new ZYXRotationView(angles);
        
        Quaternionf quat = new Quaternionf(view.asQuaternion());
        ZYXRotationView viewFromQuat = new ZYXRotationView(quat);
        Vector3f resultAngles = new Vector3f(viewFromQuat.asEulerAngle());
        
        assertVector3fEquals(angles, resultAngles);
    }

    /**
     * Test all angles to 0
     */
    @Test
    public void testZeroAngles() {
        Vector3f angles = new Vector3f(0f, 0f, 0f);
        ZYXRotationView view = new ZYXRotationView(angles);
        
        Quaternionf quat = new Quaternionf(view.asQuaternion());
        ZYXRotationView viewFromQuat = new ZYXRotationView(quat);
        Vector3f resultAngles = new Vector3f(viewFromQuat.asEulerAngle());
        
        assertVector3fEquals(angles, resultAngles);
    }

    /**
     * Test large angle value
     */
    @Test
    public void testLargeAngles() {
        Vector3f angles = new Vector3f((float)Math.PI, (float)Math.PI, (float)Math.PI);
        ZYXRotationView view = new ZYXRotationView(angles);
        
        Quaternionf quat = new Quaternionf(view.asQuaternion());
        ZYXRotationView viewFromQuat = new ZYXRotationView(quat);
        Vector3f resultAngles = new Vector3f(viewFromQuat.asEulerAngle());
        
        assertTrue(Math.abs(resultAngles.x) - Math.PI< EPSILON);
        assertTrue(Math.abs(resultAngles.y) - Math.PI< EPSILON);
        assertTrue(Math.abs(resultAngles.z) - Math.PI< EPSILON);
    }

    /**
     * Auxiliary method: Compare whether two vector are equal
     */
    private void assertVector3fEquals(Vector3f expected, Vector3f actual) {
        assertEquals(expected.x, actual.x, EPSILON, "X component mismatch");
        assertEquals(expected.y, actual.y, EPSILON, "Y component mismatch");
        assertEquals(expected.z, actual.z, EPSILON, "Z component mismatch");
    }

    /**
     * Test the case of constructing from quaternion
     */
    @Test
    public void testConstructionFromQuaternion() {
        Vector3f initialAngles = new Vector3f(0.5f, (float)(Math.PI/2), 0.7f);
        Quaternionf quat = new Quaternionf().rotateZYX(initialAngles.z, initialAngles.y, initialAngles.x);
        ZYXRotationView view = new ZYXRotationView(quat);

        Vector3f angles = new Vector3f(view.asEulerAngle());

        assertEquals(initialAngles.y, angles.y, EPSILON);
        assertEquals(initialAngles.x - initialAngles.z, angles.x - angles.z, EPSILON);
    }
} 