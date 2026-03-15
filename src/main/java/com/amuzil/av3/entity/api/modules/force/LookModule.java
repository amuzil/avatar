package com.amuzil.av3.entity.api.modules.force;

import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.IClientModule;
import com.amuzil.av3.entity.api.IForceModule;
import com.amuzil.av3.utils.maths.VectorUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.phys.Vec2;

public class LookModule implements IForceModule, IClientModule {

    public static final String id = LookModule.class.getSimpleName();

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {

    }

    @Override
    public void tick(AvatarEntity entity) {

        Vec2 rot = VectorUtils.dirToRotations(entity.lookDirection());
        entity.setXRot(rot.x);
        entity.setYRot(rot.y);
//        System.out.println("Debug");

    }

    @Override
    public void save(CompoundTag nbt) {

    }

    @Override
    public void load(CompoundTag nbt) {

    }
}
