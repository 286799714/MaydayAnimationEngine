package com.maydaymemory.mae.util.triangulation;

import java.io.Serial;

/**
 * Exception thrown by the Delaunay triangulator when it is initialized with
 * less than three points.
 * <p>
 * Open source on: <a href="https://github.com/jdiemke/delaunay-triangulator">Github</a> (MIT license)
 *
 * @author Johannes Diemke
 */
public class NotEnoughPointsException extends Exception {
    @Serial
    private static final long serialVersionUID = 7061712854155625067L;

    public NotEnoughPointsException() {
    }

    public NotEnoughPointsException(String s) {
        super(s);
    }
}