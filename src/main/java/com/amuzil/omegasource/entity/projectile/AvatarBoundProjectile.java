package com.amuzil.omegasource.entity.projectile;

import com.amuzil.omegasource.entity.AvatarEntities;
import com.amuzil.omegasource.entity.api.IForceModule;
import com.amuzil.omegasource.entity.modules.ModuleRegistry;
import com.amuzil.omegasource.entity.modules.force.BindModule;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;


public class AvatarBoundProjectile extends AvatarProjectile {

    public AvatarBoundProjectile(EntityType<AvatarBoundProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        addForceModule((IForceModule) ModuleRegistry.create(BindModule.id));
    }

    public AvatarBoundProjectile(Level pLevel) {
        this(AvatarEntities.AVATAR_BOUND_PROJECTILE_ENTITY_TYPE.get(), pLevel);
    }
}
