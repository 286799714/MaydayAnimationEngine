package com.maydaymemory.mae.control;

import com.maydaymemory.mae.util.LongSupplier;

public class PauseState implements AnimationState{
    private final LongSupplier currentNanosSupplier;

    public PauseState(final LongSupplier currentNanosSupplier) {
        this.currentNanosSupplier = currentNanosSupplier;
    }

    @Override
    public AnimationState update(IAnimationContext ctx) {
        ctx.setLastUpdateTime(currentNanosSupplier.getAsLong());
        return this;
    }
}
