package com.amuzil.av3.entity.physics;

import com.amuzil.av3.entity.AvatarEntities;
import com.amuzil.av3.entity.api.IForceModule;
import com.amuzil.av3.entity.api.modules.ModuleRegistry;
import com.amuzil.av3.entity.api.modules.force.MoveModule;
import com.amuzil.caliber.api.EntityPhysicsElement;
import com.amuzil.caliber.physics.bullet.collision.body.EntityRigidBody;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;


public class AvatarRigidBlock extends AvatarBlockEntity implements EntityPhysicsElement {
    private final EntityRigidBody rigidBody;

    public AvatarRigidBlock(EntityType<? extends AvatarRigidBlock> type, Level level) {
        super(type, level);
        this.rigidBody = new EntityRigidBody(this);
        addForceModule((IForceModule) ModuleRegistry.create(MoveModule.id));
    }

    public AvatarRigidBlock(Level pLevel) {
        this(AvatarEntities.AVATAR_RIGID_BLOCK_ENTITY_TYPE.get(), pLevel);
    }

    @Override
    public @Nullable EntityRigidBody getRigidBody() {
        return this.rigidBody;
    }
}
