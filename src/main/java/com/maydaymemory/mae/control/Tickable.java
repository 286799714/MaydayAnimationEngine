package com.maydaymemory.mae.control;

/**
 * Interface for objects that require regular update or ticking.
 *
 * <p>This interface is used to mark components that need to perform periodic
 * updates, such as advancing animation, updating state, or processing time-based
 * logic. It is commonly used in game loops, animation systems, or simulation
 * frameworks to provide a unified update mechanism.</p>
 *
 * <p>Implementing classes should provide the logic for the {@link #tick()} method,
 * which will be called at regular intervals by the controlling system.</p>
 *
 * @author MaydayMemory
 * @since 1.0.1
 */
public interface Tickable {
    /**
     * Performs a single update or tick operation.
     *
     * <p>This method should be called regularly to advance the state of the object,
     * such as progressing animation, updating timers, or processing events.</p>
     */
    void tick();
}
