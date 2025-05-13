package com.maydaymemory.mae.util.tiangulation;

import com.maydaymemory.mae.util.triangulation.*;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class DelaunayTriangulatorTest {

    @Test
    public void testSimpleTriangulation() throws NotEnoughPointsException {
        TestSamplerPoint p1 = new TestSamplerPoint(0, 0);
        TestSamplerPoint p2 = new TestSamplerPoint(0, 1);
        TestSamplerPoint p3 = new TestSamplerPoint(1, 0);
        TestSamplerPoint p4 = new TestSamplerPoint(0, -1);
        TestSamplerPoint p5 = new TestSamplerPoint(-0, 0);
        List<TestSamplerPoint> points = Arrays.asList(p1, p2, p3, p4, p5);

        DelaunayTriangulator<TestSamplerPoint> triangulator = new DelaunayTriangulator<>(points);
        triangulator.triangulate();

        List<Triangle> triangles = triangulator.getTriangles();
        Assertions.assertEquals(4, triangles.size());

        // The special case of testing point on the common edges of triangles
        WeightCalculatingResult<TestSamplerPoint> result = triangulator.calculateWeightsClampToEdge(new Vector2f(0f, 0.5f));
        assertNotNull(result);

        // The special case of testing point is outside any triangles
        WeightCalculatingResult<TestSamplerPoint> result1 = triangulator.calculateWeightsClampToEdge(new Vector2f(0.6f, 0.6f));
        assertNotNull(result1);
        //System.out.println(result1);
    }

    private static class TestSamplerPoint implements SamplerPoint {
        private final Vector2f point;

        public TestSamplerPoint(float x, float y) {
            this.point = new Vector2f(x, y);
        }

        @Override
        public float x() {
            return point.x;
        }

        @Override
        public float y() {
            return point.y;
        }

        @Override
        public Vector2fc position() {
            return point;
        }

        @Override
        public String toString() {
            return point.toString();
        }
    }
}
