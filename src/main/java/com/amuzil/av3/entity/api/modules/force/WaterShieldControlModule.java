package com.amuzil.av3.entity.api.modules.force;

import com.amuzil.av3.entity.AvatarEntity;
import net.minecraft.nbt.CompoundTag;

public class WaterShieldControlModule extends ControlModule {

     public static String id = WaterShieldControlModule.class.getSimpleName();

     @Override
     public String id() {
         return id;
     }

    @Override
    public void init(AvatarEntity entity) {
        super.init(entity);
    }

    @Override
    public void tick(AvatarEntity entity) {
        super.tick(entity);
    }

    @Override
    public void save(CompoundTag nbt) {
        nbt.putString("ID", id);
    }

    @Override
    public void load(CompoundTag nbt) {
        id = nbt.getString("ID");
    }
}
