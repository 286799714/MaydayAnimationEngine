package com.maydaymemory.mae.control.runner;

import com.maydaymemory.mae.basic.Animation;
import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.control.PoseProcessor;
import com.maydaymemory.mae.control.PoseProcessorSequence;
import com.maydaymemory.mae.control.Tickable;
import com.maydaymemory.mae.util.Iterables;
import com.maydaymemory.mae.util.MathUtil;
import it.unimi.dsi.fastutil.longs.LongLongImmutablePair;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AnimationRunner implements Tickable, PoseProcessor, IAnimationRunner {
    private final Animation animation;
    private final IAnimationContext context;

    public AnimationRunner(Animation animation, IAnimationContext context) {
        this.animation = animation;
        this.context = context;
    }

    @Override
    public Pose evaluate() {
        return animation.evaluate(MathUtil.toSecond(context.getProgress()));
    }

    @Override
    public Collection<Iterable<?>> clip() {
        LongLongImmutablePair pair = context.pollClipPlan();
        if (pair == null) {
            return Collections.emptyList();
        }
        List<Iterable<?>> result = animation.clip(MathUtil.toSecond(pair.leftLong()), MathUtil.toSecond(pair.rightLong()));
        List<Iterable<?>> clips;
        while ((pair = context.pollClipPlan()) != null) {
            clips = animation.clip(MathUtil.toSecond(pair.leftLong()), MathUtil.toSecond(pair.rightLong()));
            for (int i = 0; i < result.size(); i++) {
                Iterable<?> a = result.get(i);
                if (a == null) {
                    continue;
                }
                result.set(i, Iterables.concat(a, clips.get(i)));
            }
        }
        return result;
    }

    @Override
    public IAnimationContext getAnimationContext() {
        return context;
    }

    @Override
    public Pose process(PoseProcessorSequence sequence) {
        return evaluate();
    }

    @Override
    public void tick() {
        context.update();
    }
}
