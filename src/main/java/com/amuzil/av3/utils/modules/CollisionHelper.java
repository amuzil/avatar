package com.amuzil.av3.utils.modules;

import com.amuzil.av3.bending.form.BendingForm;
import net.minecraft.world.phys.Vec3;


public class CollisionHelper {

    public Vec3 getDirection(BendingForm.Type.Motion motion) {
        return switch (motion) {
            case FORWARD -> new Vec3(0, 0, 1);
            case BACKWARD -> new Vec3(0, 0, -1);
            case LEFTWARD -> new Vec3(-1, 0, 0);
            case RIGHTWARD -> new Vec3(1, 0, 0);
            case UPWARD -> new Vec3(0, 1, 0);
            case DOWNWARD -> new Vec3(0, -1, 0);
            default -> Vec3.ZERO;
        };
    }
}
