package com.maydaymemory.mae.util.triangulation;

/**
 * Constructor of the edge distance pack class used to create a new edge
 * distance pack instance from a 2D edge and a scalar value describing a
 * distance.
 * <p>
 * Open source on: <a href="https://github.com/jdiemke/delaunay-triangulator">Github</a> (MIT license)
 *
 * @param edge     The edge
 * @param distance The distance of the edge to some point
 * @author Johannes Diemke
 */
public record EdgeDistancePack(Edge edge, float distance) implements Comparable<EdgeDistancePack> {

    @Override
    public int compareTo(EdgeDistancePack o) {
        return Float.compare(this.distance, o.distance);
    }
}
