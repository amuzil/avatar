package com.amuzil.av3.entity.api.modules.force;

import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.IForceModule;
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
        Vec3 delta = entity.getDeltaMovement();
        if (entity.xRotO == 0.0F && entity.yRotO == 0.0F) {
            double distance = delta.horizontalDistance();
            entity.setYRot((float) (Mth.atan2(delta.x, delta.z) * (double) (180F / (float) Math.PI)));
            entity.setXRot((float) (Mth.atan2(delta.y, distance) * (double) (180F / (float) Math.PI)));
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

        delta = entity.getDeltaMovement();
        double finalX = entity.getX() + delta.x;
        double finalY = entity.getY() + delta.y;
        double finalZ = entity.getZ() + delta.z;

        if (!entity.isNoGravity() && !flag)
            entity.setDeltaMovement(delta.x, delta.y - (double) 0.05F, delta.z);

        entity.setPos(finalX, finalY, finalZ);
        entity.checkBlocks();
    }

    @Override
    public void save(CompoundTag nbt) {
        nbt.putString("ID", id);
    }

    @Override
    public void load(CompoundTag nbt) {
        id = nbt.getString("ID");
    }
}
