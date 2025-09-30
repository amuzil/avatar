package com.amuzil.omegasource.entity.modules.force;

import com.amuzil.omegasource.entity.AvatarEntity;
import com.amuzil.omegasource.entity.api.IForceModule;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;


public class MoveModule implements IForceModule {

public static String id = MoveModule.class.getSimpleName();

    @Override
    public String id() {
        return id;
    }

    @Override
    public void init(AvatarEntity entity) {}

    @Override
    public void tick(AvatarEntity entity) {

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

        deltaMovement = entity.getDeltaMovement();
        double d5 = deltaMovement.x;
        double d6 = deltaMovement.y;
        double d1 = deltaMovement.z;

        double d7 = entity.getX() + d5;
        double d2 = entity.getY() + d6;
        double d3 = entity.getZ() + d1;
        double d4 = deltaMovement.horizontalDistance();

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
