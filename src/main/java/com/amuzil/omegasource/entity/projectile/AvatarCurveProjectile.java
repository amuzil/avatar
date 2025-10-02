package com.amuzil.omegasource.entity.projectile;

import com.amuzil.omegasource.entity.AvatarEntities;
import com.amuzil.omegasource.entity.api.IForceModule;
import com.amuzil.omegasource.entity.modules.ModuleRegistry;
import com.amuzil.omegasource.entity.modules.force.CurveModule;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;


public class AvatarCurveProjectile extends AvatarProjectile {

    public AvatarCurveProjectile(EntityType<? extends AvatarCurveProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        addForceModule((IForceModule) ModuleRegistry.create(CurveModule.id));
    }

    public AvatarCurveProjectile(Level pLevel) {
        this(AvatarEntities.AVATAR_CURVE_PROJECTILE_ENTITY_TYPE.get(), pLevel);
    }

}
