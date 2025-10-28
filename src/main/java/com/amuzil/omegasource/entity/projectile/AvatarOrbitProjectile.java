package com.amuzil.omegasource.entity.projectile;

import com.amuzil.omegasource.entity.AvatarEntities;
import com.amuzil.omegasource.entity.api.IForceModule;
import com.amuzil.omegasource.entity.modules.ModuleRegistry;
import com.amuzil.omegasource.entity.modules.force.OrbitModule;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;


public class AvatarOrbitProjectile extends AvatarProjectile {

    public AvatarOrbitProjectile(EntityType<AvatarOrbitProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        addForceModule((IForceModule) ModuleRegistry.create(OrbitModule.id));
    }

    public AvatarOrbitProjectile(Level pLevel) {
        this(AvatarEntities.AVATAR_ORBIT_PROJECTILE_ENTITY_TYPE.get(), pLevel);
    }
}
