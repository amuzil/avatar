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
import com.jme3.math.Vector3f;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.amuzil.av3.utils.bending.SkillHelper.getRightPivot;


public class AvatarRigidBlock extends AvatarConstruct implements EntityPhysicsElement {

    private final EntityRigidBody rigidBody;
    private float defaultMass;

    protected static final EntityDataAccessor<Boolean> RIGID_BODY_DIRTY = SynchedEntityData.defineId(AvatarRigidBlock.class, EntityDataSerializers.BOOLEAN);

    public AvatarRigidBlock(EntityType<? extends AvatarRigidBlock> type, Level level) {
        super(type, level);
        this.rigidBody = new EntityRigidBody(this);
        addForceModule((IForceModule) ModuleRegistry.create(ControlModule.id));
        defaultMass = rigidBody.getMass();
    }

    public AvatarRigidBlock(Level level) {
        this(AvatarEntities.AVATAR_RIGID_BLOCK_ENTITY_TYPE.get(), level);
    }

    @Override
    public @Nullable EntityRigidBody getRigidBody() {
        return this.rigidBody;
    }

    @Override
    public void setOwner(@NotNull Entity owner) {
        super.setOwner(owner);
        if (owner instanceof Player)
            this.rigidBody.prioritize((Player) owner);
    }

    @Override
    public void shoot(Vec3 location, Vec3 direction, double speed, double inAccuracy) {
        setPos(location);
        Vec3 vec3 = direction.normalize().scale(speed);
        rigidBody.applyCentralImpulse(Convert.toBullet(vec3));
    }

    @Override
    public void control(float scale) {
        Entity owner = this.getOwner();
        if (owner == null) return;

        // Calculate right pivot position
        Vec3 look = owner.getLookAngle().normalize();
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 right = look.cross(up).normalize(); // cross product gives right vector
        Vec3 newPos = getRightPivot(owner, scale);

        // Calculate rotation to match owner's look direction
        Matrix3f mat = new Matrix3f();
        mat.fromAxes(Convert.toBullet(right), Convert.toBullet(up), Convert.toBullet(look));
        Quaternion q = new Quaternion();
        q.fromRotationMatrix(mat);

        rigidBody.setPhysicsLocation(Convert.toBullet(newPos));
        rigidBody.setPhysicsRotation(q);
    }

    public boolean isRigidBodyDirty() {
        return entityData.get(RIGID_BODY_DIRTY);
    }

    public void setRigidBodyDirty(boolean isDirty) {
        entityData.set(RIGID_BODY_DIRTY, isDirty);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(RIGID_BODY_DIRTY, false);
    }

    @Override
    public void tick() {
        if (isRigidBodyDirty()) {
            rigidBody.setCollisionShape(this.createShape());
            defaultMass = rigidBody.getMass();
            setRigidBodyDirty(false);
        }

        // Save previous tick position
        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();
        super.tick();
    }

    public void syncFromPhysics() {
        this.setPos(Convert.toVec3(rigidBody.getPhysicsLocation(new Vector3f())));
    }

    public float getDefaultMass() {
        return defaultMass;
    }

    public void setDefaultMass(float mass) {
        defaultMass = mass;
    }

    public void resetMass() {
        rigidBody.setMass(defaultMass);
    }

    public void resetGravity() {
        rigidBody.setGravity(rigidBody.getSpace().getGravity(new Vector3f()));
    }

    public void setKinematic(boolean kinematic) {
        this.resetMass();
        rigidBody.setKinematic(kinematic);
        rigidBody.clearForces();
        this.resetGravity();
    }
}
