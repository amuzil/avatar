package com.amuzil.omegasource.entity.modules.force;

import com.amuzil.omegasource.bending.BendingForms;
import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.entity.AvatarProjectile;
import com.amuzil.omegasource.entity.modules.IForceModule;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MoveModule implements IForceModule {
    String id = "move";

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {}

    @Override
    public void tick(AvatarEntity entity) {

        if (entity.level().isClientSide) {
            if (entity instanceof AvatarProjectile projectile) {
                projectile.startEffect(BendingForms.STRIKE);
            }
        }

        boolean flag = entity.physics();
        Vec3 deltaMovement = entity.getDeltaMovement();
        if (entity.xRotO == 0.0F && entity.yRotO == 0.0F) {
            double distance = deltaMovement.horizontalDistance();
            entity.setYRot((float) (Mth.atan2(deltaMovement.x, deltaMovement.z) * (double) (180F / (float) Math.PI)));
            entity.setXRot((float) (Mth.atan2(deltaMovement.y, distance) * (double) (180F / (float) Math.PI)));
            entity.yRotO = entity.getYRot();
            entity.xRotO = entity.getXRot();
        }

        BlockPos blockpos = entity.blockPosition();
        BlockState blockstate = entity.level().getBlockState(blockpos);
        if (!blockstate.isAir() && !flag) {
            VoxelShape voxelshape = blockstate.getCollisionShape(entity.level(), blockpos);
            if (!voxelshape.isEmpty()) {
                Vec3 vec31 = entity.position();

                for (AABB aabb : voxelshape.toAabbs()) {
                    if (aabb.move(blockpos).contains(vec31)) {
                        break;
                    }
                }
            }
        }


//        Vec3 pos = entity.position();
//        Vec3 delta = pos.add(deltaMovement);
//        HitResult hitresult = entity.level().clip(new ClipContext(pos, delta, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this));
//        if (hitresult.getType() != HitResult.Type.MISS) {
//            delta = hitresult.getLocation();
//        }

//        while (!entity.isRemoved()) {
//            EntityHitResult entityhitresult = entity.findHitEntity(pos, delta);
//            if (entityhitresult != null) {
//                hitresult = entityhitresult;
//            }
//
//            if (hitresult != null && hitresult.getType() == HitResult.Type.ENTITY) {
//                assert hitresult instanceof EntityHitResult;
//                Entity entity = ((EntityHitResult)hitresult).getEntity();
//                Entity owner = this.getOwner();
//                if (entity instanceof Player && owner instanceof Player && !((Player)owner).canHarmPlayer((Player)entity)) {
//                    hitresult = null;
//                    entityhitresult = null;
//                }
//            }
//
//            if (hitresult != null && hitresult.getType() != HitResult.Type.MISS && !flag) {
//                if (net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult))
//                    break;
//                this.onHit(hitresult);
//                this.hasImpulse = true;
//                break;
//            }
//
//            if (entityhitresult == null) {
//                break;
//            }
//
//            hitresult = null;
//        }
        deltaMovement = entity.getDeltaMovement();
        double d5 = deltaMovement.x;
        double d6 = deltaMovement.y;
        double d1 = deltaMovement.z;

        double d7 = entity.getX() + d5;
        double d2 = entity.getY() + d6;
        double d3 = entity.getZ() + d1;
        double d4 = deltaMovement.horizontalDistance();
//        if (flag) {
//            entity.setYRot((float) (Mth.atan2(-d5, -d1) * (double) (180F / (float) Math.PI)));
//        } else {
//            entity.setYRot((float) (Mth.atan2(d5, d1) * (double) (180F / (float) Math.PI)));
//        }
//
//        entity.setXRot((float) (Mth.atan2(d6, d4) * (double) (180F / (float) Math.PI)));
//        entity.setXRot(AvatarEntity.lerpRotation(entity.xRotO, entity.getXRot()));
//        entity.setYRot(AvatarEntity.lerpRotation(entity.yRotO, entity.getYRot()));

        if (!entity.isNoGravity() && !flag) {
            Vec3 vec34 = entity.getDeltaMovement();
            entity.setDeltaMovement(vec34.x, vec34.y - (double) 0.05F, vec34.z);
        }

        entity.setPos(d7, d2, d3);
        entity.checkBlocks();
    }

    @Override
    public void save(CompoundTag nbt) {
        nbt.putString("ID", id);
    }

    @Override
    public void load(CompoundTag nbt) {
        this.id = nbt.getString("ID");
    }
}
