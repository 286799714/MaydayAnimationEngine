package com.maydaymemory.mae.control.runner;

import com.maydaymemory.mae.basic.Animation;
import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.control.OutputPort;
import com.maydaymemory.mae.control.Tickable;
import com.maydaymemory.mae.util.Iterables;
import com.maydaymemory.mae.util.MathUtil;
import it.unimi.dsi.fastutil.longs.LongLongImmutablePair;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.List;

/**
 * Implementation of {@link IAnimationRunner} that manages animation execution.
 * 
 * <p>This class provides the concrete implementation of the animation runner,
 * coordinating between the animation data and the execution context. It serves
 * as the main entry point for animation playback, handling pose evaluation,
 * clip extraction, and curve evaluation.</p>
 * 
 * <p>The runner implements the {@link Tickable} interface, allowing it to be
 * integrated into game loops or update systems. It maintains an output port
 * for pose evaluation, enabling easy integration with animation control systems.</p>
 * 
 * @author MaydayMemory
 * @since 1.0.1
 */
public class AnimationRunner implements Tickable, IAnimationRunner {
    /** The animation data to be executed */
    private final Animation animation;
    
    /** The animation execution context */
    private final IAnimationContext context;
    
    /** Output port for pose evaluation */
    private final OutputPort<Pose> outputPort = this::evaluate;

    /**
     * Constructs a new AnimationRunner with the specified animation and context.
     * 
     * @param animation the animation to execute
     * @param context the animation context
     */
    public AnimationRunner(Animation animation, IAnimationContext context) {
        this.animation = animation;
        this.context = context;
    }

    /**
     * Gets the output port for pose evaluation.
     * 
     * @return the output port that provides evaluated pose
     */
    public OutputPort<Pose> getOutputPort() {
        return outputPort;
    }

    @Override
    public Pose evaluate() {
        return animation.evaluate(MathUtil.toSecond(context.getProgress()));
    }

    @Override
    public List<Iterable<?>> clip() {
        Iterator<? extends LongLongImmutablePair> iterator = context.clipPlanIterator();
        if (!iterator.hasNext()) {
            // The index where the channel exists corresponds to an empty set,
            // while the index where the channel does not exist corresponds to null
            return animation.clip(0, 0);
        }
        LongLongImmutablePair pair = iterator.next();
        List<Iterable<?>> result = animation.clip(MathUtil.toSecond(pair.leftLong()), MathUtil.toSecond(pair.rightLong()));
        List<Iterable<?>> clips;
        while (iterator.hasNext()) {
            pair = iterator.next();
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
    @Nullable
    public Iterable<?> clip(int i) {
        Iterator<? extends LongLongImmutablePair> iterator = context.clipPlanIterator();
        if (!iterator.hasNext()) {
            return animation.clip(i, 0, 0);
        }
        LongLongImmutablePair pair = iterator.next();
        Iterable<?> result = animation.clip(i, MathUtil.toSecond(pair.leftLong()), MathUtil.toSecond(pair.rightLong()));
        if (result == null) {
            return  null;
        }
        Iterable<?> clip;
        while (iterator.hasNext()) {
            pair = iterator.next();
            clip = animation.clip(i, MathUtil.toSecond(pair.leftLong()), MathUtil.toSecond(pair.rightLong()));
            result = Iterables.concat(result, clip);
        }
        return result;
    }

    @Override
    public List<Object> evaluateCurve() {
        return animation.evaluateCurve(MathUtil.toSecond(context.getProgress()));
    }

    @Override
    @Nullable
    public Object evaluateCurve(int i) {
        return animation.evaluateCurve(i, MathUtil.toSecond(context.getProgress()));
    }

    @Override
    public IAnimationContext getAnimationContext() {
        return context;
    }

    @Override
    public void tick() {
        context.update();
    }
}
