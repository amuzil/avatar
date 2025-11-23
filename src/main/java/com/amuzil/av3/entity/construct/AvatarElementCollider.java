package com.amuzil.av3.entity.construct;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;


// Used for fire, water, and air. RigidBlock is earth.
public class AvatarElementCollider extends AvatarRigidBlock {

    public AvatarElementCollider(EntityType<? extends AvatarRigidBlock> type, Level level) {
        super(type, level);
    }

    public AvatarElementCollider(Level level) {
        super(level);
    }
}
