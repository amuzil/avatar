package com.amuzil.omegasource.entity.projectile;

import com.amuzil.omegasource.entity.AvatarEntities;
import com.amuzil.omegasource.entity.api.IForceModule;
import com.amuzil.omegasource.entity.modules.ModuleRegistry;
import com.amuzil.omegasource.entity.modules.force.MoveModule;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;


public class AvatarDirectProjectile extends AvatarProjectile {

    public AvatarDirectProjectile(EntityType<AvatarDirectProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        addForceModule((IForceModule) ModuleRegistry.create(MoveModule.id));
    }

    public AvatarDirectProjectile(Level pLevel) {
        this(AvatarEntities.AVATAR_DIRECT_PROJECTILE_ENTITY_TYPE.get(), pLevel);
    }

}
