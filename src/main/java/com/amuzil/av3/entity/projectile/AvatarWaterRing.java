package com.amuzil.av3.entity.projectile;

import com.amuzil.av3.entity.AvatarEntities;
import com.amuzil.av3.entity.construct.AvatarConstruct;
import com.amuzil.av3.renderer.sdf.IHasSDF;
import com.amuzil.av3.renderer.sdf.SDFScene;
import com.amuzil.av3.renderer.sdf.SignedDistanceFunction;
import com.amuzil.av3.renderer.sdf.channels.Channels;
import com.amuzil.av3.renderer.sdf.channels.IVec3Channel;
import com.amuzil.av3.renderer.sdf.shapes.SDFTorus;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;

public class AvatarWaterRing extends AvatarConstruct implements IHasSDF {


    private SDFScene root;
    private SDFTorus ring;

    public AvatarWaterRing(EntityType<AvatarWaterRing> entityType, Level pLevel) {
        super(entityType, pLevel);
    }

    public AvatarWaterRing(Level pLevel) {
        this(AvatarEntities.AVATAR_WATER_RING_TYPE.get(), pLevel);
        this.setBlockState(Blocks.WATER.defaultBlockState());
    }


    @Override
    public SignedDistanceFunction rootSDF() {
        float size = 2f;
        IVec3Channel look = Channels.constantVec3(lookDirection().x, lookDirection().y, lookDirection().z);
        IVec3Channel scale = Channels.constantVec3(lookDirection().x * size / 4, lookDirection().y * size / 4, lookDirection().z * size / 4);

        if (ring == null)
            ring = new SDFTorus();


        float mult = 0.5f + 0.5f * sourceLevel() / maxSource();
        ring.majorRadius = Channels.constant((depth() + width()) / 2 * mult);
        ring.minorRadius = Channels.constant((depth() + width()) / 2 * mult * 0.25f);
        ring.thickness = Channels.constant(height() * mult);
        root = new SDFScene().add(ring);
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
        Vec3 newPos = owner.position().add(0, owner.getEyeHeight() / 2, 0);
        control(newPos, 0.5f);
        lookDirection(owner.getLookAngle().toVector3f());
    }

    @Override
    public void control(Vec3 pos, float motion) {
        Vec3 dir = pos.subtract(position()).scale(motion);
        this.setDeltaMovement(dir);
    }
}

