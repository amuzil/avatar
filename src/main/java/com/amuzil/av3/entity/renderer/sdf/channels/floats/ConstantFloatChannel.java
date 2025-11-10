package com.amuzil.av3.entity.renderer.sdf.channels.floats;

public class ConstantFloatChannel implements IFloatChannel {
    public final float value;

    public ConstantFloatChannel(float value) {
        this.value = value;
    }

    @Override
    public float eval(float t) {
        return value;
    }
}
