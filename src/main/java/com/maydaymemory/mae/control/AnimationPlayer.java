package com.maydaymemory.mae.control;

import com.maydaymemory.mae.basic.Animation;
import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.util.Iterables;
import com.maydaymemory.mae.util.MathUtil;
import it.unimi.dsi.fastutil.longs.LongLongImmutablePair;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AnimationPlayer {
    protected Animation animation;
    protected AnimationContext ctx;

    public AnimationPlayer(Animation animation, AnimationContext ctx) {
        this.animation = animation;
        this.ctx = ctx;
    }

    public Pose evaluate() {
        return animation.evaluate(MathUtil.toSecond(ctx.getProgress()));
    }

    public Collection<Iterable<?>> clip() {
        LongLongImmutablePair pair = ctx.pollClipPlan();
        if (pair == null) {
            return Collections.emptyList();
        }
        List<Iterable<?>> result = animation.clip(MathUtil.toSecond(pair.leftLong()), MathUtil.toSecond(pair.rightLong()));
        List<Iterable<?>> clips;
        while ((pair = ctx.pollClipPlan()) != null) {
            clips = animation.clip(MathUtil.toSecond(pair.leftLong()), MathUtil.toSecond(pair.rightLong()));
            for (int i = 0; i < clips.size(); i++) {
                Iterable<?> a = result.get(i);
                if (a == null) {
                    continue;
                }
                result.set(i, Iterables.concat(a, clips.get(i)));
            }
        }
        return result;
    }

    public void update() {
        ctx.update();
    }

    public AnimationContext getContext() {
        return ctx;
    }

    public Animation getAnimation() {
        return animation;
    }
}
