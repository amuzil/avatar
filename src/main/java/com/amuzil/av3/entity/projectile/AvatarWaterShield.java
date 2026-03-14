package com.amuzil.av3.entity.projectile;

import com.amuzil.av3.entity.AvatarEntities;
import com.amuzil.av3.entity.api.IHasHealth;
import com.amuzil.av3.entity.construct.AvatarConstruct;
import com.amuzil.av3.renderer.sdf.IHasSDF;
import com.amuzil.av3.renderer.sdf.SDFScene;
import com.amuzil.av3.renderer.sdf.SignedDistanceFunction;
import com.amuzil.av3.renderer.sdf.channels.Channels;
import com.amuzil.av3.renderer.sdf.channels.IVec3Channel;
import com.amuzil.av3.renderer.sdf.shapes.SDFDisk;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

public class AvatarWaterShield extends AvatarConstruct implements IHasSDF, IHasHealth {

    private SDFScene root;
    private SDFDisk shield;

    public AvatarWaterShield(EntityType<AvatarWaterShield> entityType, Level pLevel) {
        super(entityType, pLevel);
    }

    public AvatarWaterShield(Level pLevel) {
        this(AvatarEntities.AVATAR_WATER_SHIELD_TYPE.get(), pLevel);
        this.setBlockState(Blocks.WATER.defaultBlockState());
    }



    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
    }

    /**
     * Gets the current health of this entity. Should be non-negative.
     */
    @Override
    public float health() {
        return sourceLevel();
    }

    /**
     * Sets the current health of this entity. Should be non-negative.
     *
     * @param health
     */
    @Override
    public void health(float health) {
        sourceLevel(health);
    }

    /**
     * Gets the maximum health of this entity. Should be positive.
     */
    @Override
    public float maxHealth() {
        return maxSource();
    }

    /**
     * Sets the maximum health of this entity. Should be positive.
     *
     * @param maxHealth
     */
    @Override
    public void maxHealth(float maxHealth) {
        maxSource(maxHealth);
    }

    @Override
    public void hurt(float damage) {
        health(health() - damage);
    }

    @Override
    public void heal(float amount) {
        health(health() + amount);
    }

    @Override
    public boolean noHealth() {
        return health() <= 0;
    }

    /**
     * If max health is -1, then we don't want things to affect this.
     *
     * @return
     */
    @Override
    public boolean hurtable() {
        return maxHealth() > -1;
    }

    @Override
    public SignedDistanceFunction rootSDF() {
        float size = 2f;
        IVec3Channel look = Channels.constantVec3(lookDirection().x, lookDirection().y, lookDirection().z);
        IVec3Channel scale = Channels.constantVec3(lookDirection().x * size / 4, lookDirection().y * size / 4, lookDirection().z * size / 4);

        if (shield == null)
            shield = new SDFDisk();

        shield.a.rot = Channels.alignAxisToDir(
                new Vector3f(0, 1, 0),                  // capsule axis in *local* space
                look  // world-space direction
        );
        float mult = sourceLevel() / maxSource();
        shield.radius = Channels.constant((width() + height()) * mult);
        shield.thickness = Channels.constant(depth() * mult);
        shield.a.pos = scale;
        root = new SDFScene().add(shield);
        root.unionK = 0.9f;
        return root;
    }

    @Override
    public boolean physics() {
        return super.physics();
    }

    @Override
    public void control(float scale) {
        Entity owner = this.getOwner();
        if (owner == null) return;
        Vec3[] pose = new Vec3[]{owner.position(), owner.getLookAngle()};
        pose[1] = pose[1].scale((scale)).add((0), (owner.getEyeHeight()), (0));
        Vec3 newPos = pose[1].add(pose[0]);
        control(newPos, 0.5f);
        lookDirection(owner.getLookAngle().toVector3f());
    }

    @Override
    public void control(Vec3 pos, float motion) {
        Vec3 dir = pos.subtract(position()).scale(motion);
        this.setDeltaMovement(dir);
    }
}
