package com.amuzil.av3.entity.projectile;

import com.amuzil.av3.entity.AvatarEntities;
import com.amuzil.av3.entity.api.IForceModule;
import com.amuzil.av3.entity.api.modules.ModuleRegistry;
import com.amuzil.av3.entity.api.modules.force.OrbitModule;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;


public class AvatarOrbitProjectile extends AvatarProjectile {

    public AvatarOrbitProjectile(EntityType<AvatarOrbitProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);
        addForceModule((IForceModule) ModuleRegistry.create(OrbitModule.id));
    }

    public AvatarOrbitProjectile(Level pLevel) {
        this(AvatarEntities.AVATAR_ORBIT_PROJECTILE_ENTITY_TYPE.get(), pLevel);
    }
}
