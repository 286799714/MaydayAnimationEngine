package com.maydaymemory.mae.control.runner;

import com.maydaymemory.mae.basic.Animation;
import com.maydaymemory.mae.basic.Keyframe;
import com.maydaymemory.mae.basic.Pose;
import com.maydaymemory.mae.control.OutputPort;
import com.maydaymemory.mae.control.Tickable;
import com.maydaymemory.mae.util.Iterables;
import com.maydaymemory.mae.util.MathUtil;
import it.unimi.dsi.fastutil.longs.LongLongImmutablePair;

import javax.annotation.Nullable;
import java.util.Iterator;

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


    /**
     * Gets the animation combined with this runner.
     *
     * @return the animation combined with this runner.
     */
    public Animation getAnimation() {
        return animation;
    }

    @Override
    public Pose evaluate() {
        return animation.evaluate(MathUtil.toSecond(context.getProgress()));
    }

    @Override
    @Nullable
    public <T> Iterable<Keyframe<T>> clip(String channelName) {
        Iterator<? extends LongLongImmutablePair> iterator = context.clipPlanIterator();
        if (!iterator.hasNext()) {
            // Returning clip(name, 0, 0) is to maintain logical consistency:
            // if the channel exists, return an empty set; if not, return null.
            return animation.clip(channelName, 0, 0);
        }
        LongLongImmutablePair pair = iterator.next();
        Iterable<Keyframe<T>> result = animation.clip(channelName, MathUtil.toSecond(pair.leftLong()), MathUtil.toSecond(pair.rightLong()));
        if (result == null) {
            return  null;
        }
        Iterable<Keyframe<T>> clip;
        while (iterator.hasNext()) {
            pair = iterator.next();
            clip = animation.clip(channelName, MathUtil.toSecond(pair.leftLong()), MathUtil.toSecond(pair.rightLong()));
            if (clip != null && clip.iterator().hasNext()) {
                result = Iterables.concat(result, clip);
            }
        }
        return result;
    }

    @Override
    @Nullable
    public <T> T evaluateCurve(String curveName) {
        return animation.evaluateCurve(curveName, MathUtil.toSecond(context.getProgress()));
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
