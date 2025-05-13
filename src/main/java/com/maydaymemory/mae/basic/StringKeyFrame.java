package com.maydaymemory.mae.basic;

/**
 * Simple keyframe carrying a value of <code>String</code>
 */
public class StringKeyFrame extends BaseKeyframe<String> {
    private final String value;

    public StringKeyFrame(String value, float timeS) {
        super(timeS);
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
