package com.amuzil.omegasource.entity.projectile;

import com.amuzil.omegasource.entity.AvatarEntities;
import com.amuzil.omegasource.entity.api.IAvatarProjectile;
import com.amuzil.omegasource.entity.api.IForceModule;
import com.amuzil.omegasource.entity.api.IRenderModule;
import com.amuzil.omegasource.entity.modules.ModuleRegistry;
import com.amuzil.omegasource.entity.modules.entity.TimeoutModule;
import com.amuzil.omegasource.entity.modules.force.MoveModule;
import com.amuzil.omegasource.entity.modules.render.PhotonModule;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TraceableEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.level.Level;


public class AvatarDirectProjectile extends AvatarProjectile {

    public AvatarDirectProjectile(EntityType<? extends AvatarDirectProjectile> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        addForceModule((IForceModule) ModuleRegistry.create(MoveModule.id));
    }

    public AvatarDirectProjectile(Level pLevel) {
        this(AvatarEntities.AVATAR_DIRECT_PROJECTILE_ENTITY_TYPE.get(), pLevel);
    }

}
