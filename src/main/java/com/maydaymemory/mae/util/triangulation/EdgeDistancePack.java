package com.maydaymemory.mae.util.triangulation;

/**
 * Constructor of the edge distance pack class used to create a new edge
 * distance pack instance from a 2D edge and a scalar value describing a
 * distance.
 * <p>
 * Open source on: <a href="https://github.com/jdiemke/delaunay-triangulator">Github</a> (MIT license)
 *
 * @author Johannes Diemke
 */
public class EdgeDistancePack implements Comparable<EdgeDistancePack> {
    private final Edge edge;
    private final float distance;

    /**
     * Constructor of the edge distance pack class used to create a new edge
     * distance pack instance from a 2D edge and a scalar value describing a
     * distance.
     *
     * @param edge     The edge
     * @param distance The distance of the edge to some point
     */
    public EdgeDistancePack(Edge edge, float distance) {
        this.edge = edge;
        this.distance = distance;
    }

    public Edge edge() {
        return edge;
    }

    public float distance() {
        return distance;
    }

    @Override
    public int compareTo(EdgeDistancePack o) {
        return Float.compare(this.distance, o.distance);
    }
}
