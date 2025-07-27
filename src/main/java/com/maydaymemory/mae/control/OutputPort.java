package com.maydaymemory.mae.control;

/**
 * Functional interface representing an output port for data retrieval.
 *
 * <p>This interface is used to abstract the concept of an output port in a node-based
 * or dataflow system. It provides a single method to retrieve the current value
 * produced by a node or component. Output ports are typically connected to input slots
 * to enable data propagation between nodes.</p>
 *
 * @param <T> the type of value provided by this output port
 * @author MaydayMemory
 * @since 1.0.1
 */
@FunctionalInterface
public interface OutputPort<T> {
    /**
     * Retrieves the current value from this output port.
     *
     * @return the current value
     */
    T get();
}
