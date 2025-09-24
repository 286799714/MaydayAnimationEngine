package com.maydaymemory.mae.util.triangulation;

import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;

import java.util.Collection;
import java.util.List;

/**
 * A Java implementation of an incremental 2D Delaunay triangulation algorithm.
 * <p>
 * Open source on: <a href="https://github.com/jdiemke/delaunay-triangulator">Github</a> (MIT license)
 *
 * @author Johannes Diemke
 */
public class DelaunayTriangulator<T extends SamplerPoint> {

    private final Collection<? extends SamplerPoint> pointSet;
    private TriangleSoup triangleSoup;

    /**
     * Constructor of the SimpleDelaunayTriangulator class used to create a new
     * triangulator instance.
     *
     * @param pointSet The point set to be triangulated
     */
    public DelaunayTriangulator(Collection<T> pointSet) {
        this.pointSet = pointSet;
        this.triangleSoup = new TriangleSoup();
    }

    /**
     * This method generates a Delaunay triangulation from the specified point
     * set.
     *
     * @throws NotEnoughPointsException Thrown when the point set contains less than three points
     */
    public void triangulate() throws NotEnoughPointsException {
        triangleSoup = new TriangleSoup();

        if (pointSet == null || pointSet.size() < 3) {
            throw new NotEnoughPointsException("Less than three points in point set. total: " + (pointSet == null ? 0 : pointSet.size()));
        }

        /*
         * In order for the in circumcircle test to not consider the vertices of
         * the super triangle we have to start out with a big triangle
         * containing the whole point set. We have to scale the super triangle
         * to be very large. Otherwise, the triangulation is not convex.
         */
        float maxOfAnyCoordinate = 0.0f;

        for (SamplerPoint point : getPointSet()) {
            maxOfAnyCoordinate = Math.max(Math.max(point.x(), point.y()), maxOfAnyCoordinate);
        }

        maxOfAnyCoordinate *= 16.0f;

        DummySamplerPoint p1 = new DummySamplerPoint(0.0f, 3.0f * maxOfAnyCoordinate);
        DummySamplerPoint p2 = new DummySamplerPoint(3.0f * maxOfAnyCoordinate, 0.0f);
        DummySamplerPoint p3 = new DummySamplerPoint(-3.0f * maxOfAnyCoordinate, -3.0f * maxOfAnyCoordinate);

        Triangle superTriangle = new Triangle(p1, p2, p3);

        triangleSoup.add(superTriangle);

        for (SamplerPoint currentPoint : pointSet) {
            Triangle triangle = triangleSoup.findContainingTriangle(currentPoint.position());

            if (triangle == null) {
                /*
                 * If no containing triangle exists, then the vertex is not
                 * inside a triangle (this can also happen due to numerical
                 * errors) and lies on an edge. In order to find this edge we
                 * search all edges of the triangle soup and select the one
                 * which is nearest to the point we try to add. This edge is
                 * removed and four new edges are added.
                 */
                Edge edge = triangleSoup.findNearestEdge(currentPoint.position());

                Triangle first = triangleSoup.findOneTriangleSharing(edge);
                Triangle second = triangleSoup.findNeighbour(first, edge);

                SamplerPoint firstNoneEdgeVertex = first.getNoneEdgeVertex(edge);
                SamplerPoint secondNoneEdgeVertex = second.getNoneEdgeVertex(edge);

                triangleSoup.remove(first);
                triangleSoup.remove(second);

                Triangle triangle1 = new Triangle(edge.getA(), firstNoneEdgeVertex, currentPoint);
                Triangle triangle2 = new Triangle(edge.getB(), firstNoneEdgeVertex, currentPoint);
                Triangle triangle3 = new Triangle(edge.getA(), secondNoneEdgeVertex, currentPoint);
                Triangle triangle4 = new Triangle(edge.getB(), secondNoneEdgeVertex, currentPoint);

                triangleSoup.add(triangle1);
                triangleSoup.add(triangle2);
                triangleSoup.add(triangle3);
                triangleSoup.add(triangle4);

                legalizeEdge(triangle1, new Edge(edge.getA(), firstNoneEdgeVertex), currentPoint);
                legalizeEdge(triangle2, new Edge(edge.getB(), firstNoneEdgeVertex), currentPoint);
                legalizeEdge(triangle3, new Edge(edge.getA(), secondNoneEdgeVertex), currentPoint);
                legalizeEdge(triangle4, new Edge(edge.getB(), secondNoneEdgeVertex), currentPoint);
            } else {
                /*
                 * The vertex is inside a triangle.
                 */
                SamplerPoint a = triangle.getA();
                SamplerPoint b = triangle.getB();
                SamplerPoint c = triangle.getC();

                triangleSoup.remove(triangle);

                Triangle first = new Triangle(a, b, currentPoint);
                Triangle second = new Triangle(b, c, currentPoint);
                Triangle third = new Triangle(c, a, currentPoint);

                triangleSoup.add(first);
                triangleSoup.add(second);
                triangleSoup.add(third);

                legalizeEdge(first, new Edge(a, b), currentPoint);
                legalizeEdge(second, new Edge(b, c), currentPoint);
                legalizeEdge(third, new Edge(c, a), currentPoint);
            }
        }

        /*
         * Remove all triangles that contain vertices of the super triangle.
         */
        triangleSoup.removeTrianglesUsing(superTriangle.getA());
        triangleSoup.removeTrianglesUsing(superTriangle.getB());
        triangleSoup.removeTrianglesUsing(superTriangle.getC());
    }

    public Triangle findContainingTriangle(Vector2fc point) {
        return triangleSoup.findContainingTriangle(point);
    }

    @SuppressWarnings("unchecked")
    public WeightCalculatingResult<T> calculateWeightsClampToEdge(Vector2fc point) {
        Triangle bestTriangle = null;
        Vector3f bestBarycentric = new Vector3f();
        float bestDist = Float.POSITIVE_INFINITY;

        Vector3f weights = new Vector3f();
        // In actual use, point changes continuously most of the time.
        // Caching the last matched triangle can avoid many full table scans.
//        if (lastContainedTriangle != null) {
//            lastContainedTriangle.computeBarycentricCoordinates(point, weights);
//            if (weights.x >= 0 && weights.y >= 0 && weights.z >= 0 &&
//                    weights.x <= 1 && weights.y <= 1 && weights.z <= 1) {
//                return new WeightCalculatingResult<>(
//                        (T)lastContainedTriangle.getA(), (T)lastContainedTriangle.getB(), (T)lastContainedTriangle.getC(),
//                        weights.x, weights.y, weights.z
//                );
//            }
//        }
        for (Triangle tri : triangleSoup.getTriangles()) {
            tri.computeBarycentricCoordinates(point, weights);
            if (weights.x >= 0 && weights.y >= 0 && weights.z >= 0 &&
                    weights.x <= 1 && weights.y <= 1 && weights.z <= 1) {
                return new WeightCalculatingResult<>(
                        (T)tri.getA(), (T)tri.getB(), (T)tri.getC(),
                        weights.x, weights.y, weights.z
                );
            }

            Vector2f centroid = new Vector2f();
            centroid.set(tri.getA().position()).add(tri.getB().position()).add(tri.getC().position()).div(3f);
            float dist = point.distanceSquared(centroid);
            if (dist < bestDist) {
                bestDist = dist;
                bestTriangle = tri;
                bestBarycentric.set(weights);
            }
        }

        if (bestTriangle == null) {
            throw new IllegalStateException("No triangle found in this triangulation");
        }

        clampBarycentric(bestBarycentric);
        return new WeightCalculatingResult<T>(
                (T)bestTriangle.getA(), (T)bestTriangle.getB(), (T)bestTriangle.getC(),
                bestBarycentric.x, bestBarycentric.y, bestBarycentric.z
        );
    }

    /**
     * ClampToEdge 策略：将 barycenter 限制在 [0, 1] 范围
     */
    private static void clampBarycentric(Vector3f bary) {
        bary.x = clamp01(bary.x);
        bary.y = clamp01(bary.y);
        bary.z = clamp01(bary.z);
        float sum = bary.x + bary.y + bary.z;
        if (sum != 0) bary.div(sum); // normalization
    }

    private static float clamp01(float n) {
        return Math.max(0f, Math.min(1f, n));
    }

    /**
     * This method legalizes edges by recursively flipping all illegal edges.
     *
     * @param triangle  The triangle
     * @param edge      The edge to be legalized
     * @param newVertex The new vertex
     */
    private void legalizeEdge(Triangle triangle, Edge edge, SamplerPoint newVertex) {
        Triangle neighbourTriangle = triangleSoup.findNeighbour(triangle, edge);

        /*
         * If the triangle has a neighbor, then legalize the edge
         */
        if (neighbourTriangle != null) {
            if (neighbourTriangle.isPointInCircumcircle(newVertex.position())) {
                triangleSoup.remove(triangle);
                triangleSoup.remove(neighbourTriangle);

                SamplerPoint noneEdgeVertex = neighbourTriangle.getNoneEdgeVertex(edge);

                Triangle firstTriangle = new Triangle(noneEdgeVertex, edge.getA(), newVertex);
                Triangle secondTriangle = new Triangle(noneEdgeVertex, edge.getB(), newVertex);

                triangleSoup.add(firstTriangle);
                triangleSoup.add(secondTriangle);

                legalizeEdge(firstTriangle, new Edge(noneEdgeVertex, edge.getA()), newVertex);
                legalizeEdge(secondTriangle, new Edge(noneEdgeVertex, edge.getB()), newVertex);
            }
        }
    }

    /**
     * Returns the point set in form of a vector of 2D vectors.
     *
     * @return Returns the points set.
     */
    public Collection<? extends SamplerPoint> getPointSet() {
        return pointSet;
    }

    /**
     * Returns the triangles of the triangulation in form of a vector of 2D
     * triangles.
     *
     * @return Returns the triangles of the triangulation.
     */
    public List<Triangle> getTriangles() {
        return triangleSoup.getTriangles();
    }

    private static class DummySamplerPoint implements SamplerPoint {
        private final Vector2f point;

        public DummySamplerPoint(float x, float y) {
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
    }
}