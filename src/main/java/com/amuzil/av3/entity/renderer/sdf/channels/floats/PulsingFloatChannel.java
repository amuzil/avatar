package com.amuzil.av3.entity.renderer.sdf.channels.floats;

public class PulsingFloatChannel implements IFloatChannel {

    public float base;
    public float amp;
    public float freq;
    public float phase;

    public PulsingFloatChannel(float base,
                               float amp,
                               float freq,
                               float phase) {
        this.base = base;
        this.amp = amp;
        this.freq = freq;
        this.phase = phase;
    }

    @Override
    public float eval(float t) {
        return base + amp * 0.5f * (1f + (float)Math.sin((Math.PI*2)*freq*t + phase));
    }
}
