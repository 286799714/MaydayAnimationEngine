package com.maydaymemory.mae.util.triangulation;

import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;

import java.util.Arrays;

/**
 * 2D triangle class implementation.
 * <p>
 * Open source on: <a href="https://github.com/jdiemke/delaunay-triangulator">Github</a> (MIT license)
 *
 * @author Johannes Diemke
 */
public class Triangle {

    private final SamplerPoint a;
    private final SamplerPoint b;
    private final SamplerPoint c;

    private final Vector2fc b_a;
    private final Vector2fc c_b;
    private final Vector2fc a_c;
    private final float detT;
    private final boolean isOrientedCCW;

    /**
     * Constructor of the 2D triangle class used to create a new triangle
     * instance from three 2D vectors describing the triangle's vertices.
     *
     * @param a
     *            The first vertex of the triangle
     * @param b
     *            The second vertex of the triangle
     * @param c
     *            The third vertex of the triangle
     */
    public Triangle(SamplerPoint a, SamplerPoint b, SamplerPoint c) {
        this.a = a;
        this.b = b;
        this.c = c;

        this.b_a = b.position().sub(a.position(), new Vector2f());
        this.c_b = c.position().sub(b.position(), new Vector2f());
        this.a_c = a.position().sub(c.position(), new Vector2f());

        float a11 = a.x() - c.x();
        float a21 = b.x() - c.x();

        float a12 = a.y() - c.y();
        float a22 = b.y() - c.y();

        isOrientedCCW = (a11 * a22 - a12 * a21) > 0f;

        detT = (b.y() - c.y())*(a.x() - c.x()) + (c.x() - b.x())*(a.y() - c.y());
    }

    /**
     * Tests if a 2D point lies inside this 2D triangle. See Real-Time Collision
     * Detection, chap. 5, p. 206.
     *
     * @param point
     *            The point to be tested
     * @return Returns true iff the point lies inside this 2D triangle
     */
    public boolean contains(Vector2fc point) {
        float pab = cross(point.sub(a.position(), new Vector2f()), b_a);
        float pbc = cross(point.sub(b.position(), new Vector2f()), c_b);

        if (pab == 0 || pbc == 0) {
            return true;
        }

        if (!hasSameSign(pab, pbc)) {
            return false;
        }

        float pca = cross(point.sub(c.position(), new Vector2f()), a_c);

        if (pca == 0) {
            return true;
        }

        return hasSameSign(pab, pca);
    }

    /**
     * Computes the barycentric coordinates (alpha, beta, gamma) of a point
     * with respect to this triangle and stores them in the provided destination vector.
     *
     * @param point The 2D point inside or near the triangle.
     * @param dest  The {@link Vector3f} to store the resulting barycentric coordinates.
     */
    public void computeBarycentricCoordinates(Vector2fc point, Vector3f dest) {
        float alpha = ((b.y() - c.y())*(point.x() - c.x()) + (c.x() - b.x())*(point.y() - c.y())) / detT;
        float beta = ((c.y() - a.y())*(point.x() - c.x()) + (a.x() - c.x())*(point.y() - c.y())) / detT;
        float gamma = 1 - alpha - beta;
        dest.set(alpha, beta, gamma);
    }

    /**
     * Tests if a given point lies in the circumcircle of this triangle. Let the
     * triangle ABC appear in counterclockwise (CCW) order. Then when det &gt;
     * 0, the point lies inside the circumcircle through the three points a, b
     * and c. If instead det &lt; 0, the point lies outside the circumcircle.
     * When det = 0, the four points are cocircular. If the triangle is oriented
     * clockwise (CW) the result is reversed. See Real-Time Collision Detection,
     * chap. 3, p. 34.
     *
     * @param point
     *            The point to be tested
     * @return Returns true iff the point lies inside the circumcircle through
     *         the three points a, b, and c of the triangle
     */
    public boolean isPointInCircumcircle(Vector2fc point) {
        float a11 = a.x() - point.x();
        float a21 = b.x() - point.x();
        float a31 = c.x() - point.x();

        float a12 = a.y() - point.y();
        float a22 = b.y() - point.y();
        float a32 = c.y() - point.y();

        float a13 = (a.x() - point.x()) * (a.x() - point.x()) + (a.y() - point.y()) * (a.y() - point.y());
        float a23 = (b.x() - point.x()) * (b.x() - point.x()) + (b.y() - point.y()) * (b.y() - point.y());
        float a33 = (c.x() - point.x()) * (c.x() - point.x()) + (c.y() - point.y()) * (c.y() - point.y());

        float det = a11 * a22 * a33 + a12 * a23 * a31 + a13 * a21 * a32 - a13 * a22 * a31 - a12 * a21 * a33
                - a11 * a23 * a32;

        if (isOrientedCCW()) {
            return det > 0.0f;
        }

        return det < 0.0f;
    }

    /**
     * Test if this triangle is oriented counterclockwise (CCW). Let A, B and C
     * be three 2D points. If det &gt; 0, C lies to the left of the directed
     * line AB. Equivalently the triangle ABC is oriented counterclockwise. When
     * det &lt; 0, C lies to the right of the directed line AB, and the triangle
     * ABC is oriented clockwise. When det = 0, the three points are collinear.
     * See Real-Time Collision Detection, chap. 3, p. 32
     *
     * @return Returns true iff the triangle ABC is oriented counterclockwise
     *         (CCW)
     */
    public boolean isOrientedCCW() {
        return isOrientedCCW;
    }

    /**
     * Returns true if this triangle contains the given edge.
     *
     * @param edge
     *            The edge to be tested
     * @return Returns true if this triangle contains the edge
     */
    public boolean isNeighbour(Edge edge) {
        return (a == edge.getA() || b == edge.getA() || c == edge.getA()) && (a == edge.getB() || b == edge.getB() || c == edge.getB());
    }

    /**
     * Returns the vertex of this triangle that is not part of the given edge.
     *
     * @param edge
     *            The edge
     * @return The vertex of this triangle that is not part of the edge
     */
    public SamplerPoint getNoneEdgeVertex(Edge edge) {
        if (a != edge.getA() && a != edge.getB()) {
            return a;
        } else if (b != edge.getA() && b != edge.getB()) {
            return b;
        } else if (c != edge.getA() && c != edge.getB()) {
            return c;
        }

        return null;
    }

    /**
     * Returns true if the given vertex is one of the vertices describing this
     * triangle.
     *
     * @param vertex
     *            The vertex to be tested
     * @return Returns true if the Vertex is one of the vertices describing this
     *         triangle
     */
    public boolean hasVertex(SamplerPoint vertex) {
        return a == vertex || b == vertex || c == vertex;
    }

    /**
     * Returns an EdgeDistancePack containing the edge and its distance nearest
     * to the specified point.
     *
     * @param point
     *            The point the nearest edge is queried for
     * @return The edge of this triangle that is nearest to the specified point
     */
    public EdgeDistancePack findNearestEdge(Vector2fc point) {
        EdgeDistancePack[] edges = new EdgeDistancePack[3];

        edges[0] = new EdgeDistancePack(new Edge(a, b),
                computeClosestPoint(new Edge(a, b), point).sub(point).length());
        edges[1] = new EdgeDistancePack(new Edge(b, c),
                computeClosestPoint(new Edge(b, c), point).sub(point).length());
        edges[2] = new EdgeDistancePack(new Edge(c, a),
                computeClosestPoint(new Edge(c, a), point).sub(point).length());

        Arrays.sort(edges);
        return edges[0];
    }

    public SamplerPoint getA() {
        return a;
    }

    public SamplerPoint getB() {
        return b;
    }

    public SamplerPoint getC() {
        return c;
    }

    /**
     * Computes the closest point on the given edge to the specified point.
     *
     * @param edge
     *            The edge on which we search the closest point to the specified
     *            point
     * @param point
     *            The point to which we search the closest point on the edge
     * @return The closest point on the given edge to the specified point
     */
    private Vector2f computeClosestPoint(Edge edge, Vector2fc point) {
        Vector2fc ab = edge.getA2B();
        Vector2f dest = new Vector2f();
        float t = point.sub(edge.getA().position(), dest).dot(ab) / ab.dot(ab);

        if (t < 0.0f) {
            t = 0.0f;
        } else if (t > 1.0f) {
            t = 1.0f;
        }

        return edge.getA().position().add(ab.x() * t, ab.y() * t, dest);
    }

    /**
     * Tests if the two arguments have the same sign.
     *
     * @param a
     *            The first floating point argument
     * @param b
     *            The second floating point argument
     * @return Returns true iff both arguments have the same sign
     */
    private boolean hasSameSign(float a, float b) {
        return Math.signum(a) == Math.signum(b);
    }

    @Override
    public String toString() {
        return "Triangle2D[" + a + ", " + b + ", " + c + "]";
    }

    private float cross(Vector2fc vec1, Vector2fc vec2) {
        return vec1.y() * vec2.x() - vec1.x() * vec2.y();
    }
}
