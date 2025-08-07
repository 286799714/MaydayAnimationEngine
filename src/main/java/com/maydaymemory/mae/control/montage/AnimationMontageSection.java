package com.maydaymemory.mae.control.montage;

import javax.annotation.Nullable;

/**
 * Animation Montage Section, representing a time segment in animation montage.
 * 
 * <p>Animation montage section defines a continuous time period in animation montage, containing start time, end time and automatic next section.
 * Can be used to organize complex animation sequences.</p>
 * 
 * @author MaydayMemory
 * @since 1.0.4
 */
public class AnimationMontageSection {
    /** Section name, serves as unique identifier within the same montage, cannot be duplicated */
    private final String name;
    
    /** Section start time (seconds) */
    private final float startTime;
    
    /** Section end time (seconds) */
    private final float endTime;
    
    /** Next section name, can be null indicating no next section */
    private final @Nullable String nextSection;

    /**
     * Construct a new animation montage section.
     * 
     * @param name Section name
     * @param startTime Start time (seconds)
     * @param endTime End time (seconds)
     * @param nextSection Next section name, can be null indicating no next section
     */
    public AnimationMontageSection(String name, float startTime, float endTime,
                                   @Nullable String nextSection) {
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.nextSection = nextSection;
    }

    /**
     * Get section name.
     * 
     * @return Section name
     */
    public String getName() {
        return name;
    }

    /**
     * Get section start time.
     * 
     * @return Start time (seconds)
     */
    public float getStartTime() {
        return startTime;
    }

    /**
     * Get section end time.
     * 
     * @return End time (seconds)
     */
    public float getEndTime() {
        return endTime;
    }

    /**
     * Get section length.
     * 
     * @return Section length (seconds)
     */
    public float getLength() {
        return endTime - startTime;
    }

    /**
     * Get next section name.
     * 
     * @return Next section name, returns null if no next section
     */
    public @Nullable String getNextSection() {
        return nextSection;
    }
}
