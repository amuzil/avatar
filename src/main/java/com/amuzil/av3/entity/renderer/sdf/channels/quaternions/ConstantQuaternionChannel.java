package com.amuzil.av3.entity.renderer.sdf.channels.quaternions;

import org.joml.Quaternionf;

public class ConstantQuaternionChannel implements IQuatChannel{
    public final Quaternionf value;

    public ConstantQuaternionChannel(){
        this(new Quaternionf().identity());
    }

    public ConstantQuaternionChannel(Quaternionf value){
        this.value = value;
    }

    @Override
    public Quaternionf eval(float t, Quaternionf out) {
        return value;
    }
}
