package com.amuzil.av3.entity.physics;

import com.amuzil.av3.entity.AvatarEntities;
import com.amuzil.av3.entity.api.IForceModule;
import com.amuzil.av3.entity.api.modules.ModuleRegistry;
import com.amuzil.av3.entity.api.modules.force.MoveModule;
import com.amuzil.caliber.api.EntityPhysicsElement;
import com.amuzil.caliber.physics.bullet.collision.body.EntityRigidBody;
import com.amuzil.caliber.physics.bullet.math.Convert;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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

    @Override
    public void shoot(Vec3 location, Vec3 direction, double speed, double inAccuracy) {
        setPos(location);
        Vec3 vec3 = direction.normalize().scale(200);
        rigidBody.applyCentralImpulse(Convert.toBullet(vec3));
    }

    @Override
    public void control(float scale) {

    }
}
