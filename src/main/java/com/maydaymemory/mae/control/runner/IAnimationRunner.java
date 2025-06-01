package com.maydaymemory.mae.control.runner;

import com.maydaymemory.mae.basic.Pose;

import java.util.Collection;

public interface IAnimationRunner {
    Pose evaluate();

    Collection<Iterable<?>> clip();

    IAnimationContext getAnimationContext();
}
