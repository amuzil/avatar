package com.amuzil.av3.entity.projectile;

import com.amuzil.av3.entity.AvatarEntities;
import com.amuzil.av3.entity.api.IForceModule;
import com.amuzil.av3.entity.modules.ModuleRegistry;
import com.amuzil.av3.entity.modules.force.MoveModule;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;


public class AvatarDirectProjectile extends AvatarProjectile {

    public AvatarDirectProjectile(EntityType<AvatarDirectProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);
        addForceModule((IForceModule) ModuleRegistry.create(MoveModule.id));
    }

    public AvatarDirectProjectile(Level pLevel) {
        this(AvatarEntities.AVATAR_DIRECT_PROJECTILE_ENTITY_TYPE.get(), pLevel);
    }

}
