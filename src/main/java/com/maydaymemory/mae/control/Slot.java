package com.maydaymemory.mae.control;

import javax.annotation.Nullable;

/**
 * Represents an input slot that can be connected to an output port in a node-based system.
 *
 * <p>This class is used to model the concept of an input slot in a dataflow or node graph.
 * A slot can be connected to an {@link OutputPort} to receive data, or it can provide a default value
 * if no connection is present. Slots enable flexible data routing and defaulting in animation graphs,
 * control systems, or other modular architectures.</p>
 *
 * <p>Slots are generic and can be used for any data type. They support connection, disconnection,
 * and default value management.</p>
 *
 * @param <T> the type of value accepted by this slot
 * @author MaydayMemory
 * @since 1.0.1
 */
public class Slot<T> {
    /** The output port this slot is connected to, or null if not connected */
    private OutputPort<T> connected;
    /** The default value to use if not connected */
    private T defaultValue;

    /**
     * Connects this slot to the specified output port.
     *
     * @param output the output port to connect to
     */
    public void connect(OutputPort<T> output) {
        this.connected = output;
    }

    /**
     * Gets the output port this slot is connected to.
     *
     * @return the connected output port, or null if not connected
     */
    public @Nullable OutputPort<T> getConnected() {
        return connected;
    }

    /**
     * Sets the default value for this slot.
     *
     * @param defaultValue the default value to use if not connected
     */
    public void setDefaultValue(@Nullable T defaultValue) {
        this.defaultValue = defaultValue;
    }

    /**
     * Gets the default value for this slot.
     *
     * @return the default value, or null if not set
     */
    public @Nullable T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Retrieves the current value from the connected output port, or the default value if not connected.
     *
     * @return the current value, or null if neither connected nor default value is set
     */
    public @Nullable T get() {
        return connected != null
                ? connected.get()
                : defaultValue != null
                ? defaultValue
                : null;
    }
}
