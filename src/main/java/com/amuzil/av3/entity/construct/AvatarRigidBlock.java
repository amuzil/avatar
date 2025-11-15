package com.amuzil.av3.entity.construct;

import com.amuzil.av3.entity.AvatarEntities;
import com.amuzil.av3.entity.api.IForceModule;
import com.amuzil.av3.entity.api.modules.ModuleRegistry;
import com.amuzil.av3.entity.api.modules.force.ControlModule;
import com.amuzil.caliber.api.EntityPhysicsElement;
import com.amuzil.caliber.physics.bullet.collision.body.EntityRigidBody;
import com.amuzil.caliber.physics.bullet.math.Convert;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;


public class AvatarRigidBlock extends AvatarConstruct implements EntityPhysicsElement {
    private final EntityRigidBody rigidBody;

    public AvatarRigidBlock(EntityType<? extends AvatarRigidBlock> type, Level level) {
        super(type, level);
        this.rigidBody = new EntityRigidBody(this);
        addForceModule((IForceModule) ModuleRegistry.create(ControlModule.id));
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
        Entity owner = this.getOwner();
        if (owner == null) return;

        // Calculate right pivot position
        Vec3 eyePos = owner.getEyePosition();
        Vec3 look = owner.getLookAngle().normalize();
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 right = look.cross(up).normalize(); // cross product gives right vector
        double sideOffset = 0.7;  // how far to the right
        Vec3 newPos = eyePos
                .add(right.scale(sideOffset))
                .add(look.scale(scale));

        // Calculate rotation to match owner's look direction
        Matrix3f mat = new Matrix3f();
        mat.fromAxes(Convert.toBullet(right), Convert.toBullet(up), Convert.toBullet(look)); // columns = basis vectors
        Quaternion q = new Quaternion();
        q.fromRotationMatrix(mat);

        rigidBody.setPhysicsLocation(Convert.toBullet(newPos));
        rigidBody.setPhysicsRotation(q);
    }
}
