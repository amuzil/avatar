package com.amuzil.av3.entity.projectile;

import com.amuzil.av3.entity.AvatarEntities;
import com.amuzil.av3.entity.api.IForceModule;
import com.amuzil.av3.entity.api.modules.ModuleRegistry;
import com.amuzil.av3.entity.api.modules.force.CurveModule;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;


public class AvatarCurveProjectile extends AvatarProjectile {

    public AvatarCurveProjectile(EntityType<AvatarCurveProjectile> entityType, Level pLevel) {
        super(entityType, pLevel);
        addForceModule((IForceModule) ModuleRegistry.create(CurveModule.id));
    }

    public AvatarCurveProjectile(Level pLevel) {
        this(AvatarEntities.AVATAR_CURVE_PROJECTILE_ENTITY_TYPE.get(), pLevel);
    }

}
