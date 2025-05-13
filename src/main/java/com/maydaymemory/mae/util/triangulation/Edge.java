package com.maydaymemory.mae.util.triangulation;

import org.joml.Vector2f;
import org.joml.Vector2fc;

/**
 * 2D edge class implementation.
 * <p>
 * Open source on: <a href="https://github.com/jdiemke/delaunay-triangulator">Github</a> (MIT license)
 *
 * @author Johannes Diemke
 */
public class Edge {

    private final SamplerPoint a;
    private final SamplerPoint b;
    private final Vector2f a2b;

    /**
     * Constructor of the 2D edge class used to create a new edge instance from
     * two 2D vectors describing the edge's vertices.
     *
     * @param a
     *            The first vertex of the edge
     * @param b
     *            The second vertex of the edge
     */
    public Edge(SamplerPoint a, SamplerPoint b) {
        this.a = a;
        this.b = b;
        this.a2b = b.position().sub(a.position(), new Vector2f());
    }

    public SamplerPoint getB() {
        return b;
    }

    public SamplerPoint getA() {
        return a;
    }

    public Vector2fc getA2B() {
        return a2b;
    }
}
