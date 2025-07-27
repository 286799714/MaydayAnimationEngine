package com.maydaymemory.mae.control.statemachine;

/**
 * Enumeration defining strategies for handling transition interruptions.
 * 
 * <p>This enum specifies how transitions can be interrupted by other
 * transitions during their execution. Different strategies provide
 * varying levels of control over transition behavior and allow for
 * complex animation state machine interactions.</p>
 * 
 * <p>The transfer out strategy is used to determine whether a transition
 * in progress can be interrupted and from which states the interruption
 * is allowed.</p>
 * 
 * @author MaydayMemory
 * @since 1.0.1
 */
public enum TransferOutStrategy {
    /**
     * No interruptions allowed.
     * 
     * <p>Once this transition starts, it cannot be interrupted by any
     * other transitions. The transition will complete fully before
     * any new transitions can be considered.</p>
     */
    NONE,
    
    /**
     * Allows to be interrupted by transitions from the start state.
     * 
     * <p>This transition can be interrupted by transitions that originate
     * from the same state that started this transition. This allows for
     * quick changes of direction or behavior while maintaining some
     * control over interruption sources.</p>
     */
    FROM_STATE,
    
    /**
     * Allows to be interrupted by transitions from the target state.
     * 
     * <p>This transition can be interrupted by transitions that originate
     * from the target state of this transition. This is useful for
     * creating chains of transitions or allowing early termination
     * when the target state has its own transitions available.</p>
     */
    TO_STATE,
    
    /**
     * Allows to be interrupted by transitions from the start state and the target state,
     * transition from start state first.
     * 
     * <p>This transition can be interrupted by transitions from either the
     * start state or the target state, with priority given to transitions
     * from the start state. This provides maximum flexibility while
     * maintaining a predictable interruption hierarchy.</p>
     */
    ANY_STATE
}
