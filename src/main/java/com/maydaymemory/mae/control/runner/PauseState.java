package com.maydaymemory.mae.control.runner;

import com.maydaymemory.mae.util.LongSupplier;

public class PauseState implements IAnimationState {
    private final LongSupplier currentNanosSupplier;

    public PauseState(final LongSupplier currentNanosSupplier) {
        this.currentNanosSupplier = currentNanosSupplier;
    }

    @Override
    public IAnimationState update(IAnimationContext ctx) {
        ctx.setLastUpdateTime(currentNanosSupplier.getAsLong());
        return this;
    }

    @Override
    public void onEnter(IAnimationContext ctx) {
        ctx.setLastUpdateTime(currentNanosSupplier.getAsLong());
    }

    @Override
    public boolean isEndPoint() {
        return false;
    }
}
