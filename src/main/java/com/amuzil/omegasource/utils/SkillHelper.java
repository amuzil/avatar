package com.amuzil.omegasource.utils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class SkillHelper {

    public static boolean canEarthBend(LivingEntity entity) {
        return getDistanceToGround(entity) <= 2.0;
    }

    public static double getDistanceToGround(LivingEntity entity) {
        Level level = entity.level();

        // Start just below player's feet
        Vec3 start = entity.position();
        Vec3 end = start.subtract(0, 5, 0); // 5-block search range (adjust as needed)

        ClipContext context = new ClipContext(
                start,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                entity
        );

        BlockHitResult result = level.clip(context);

        if (result.getType() == HitResult.Type.BLOCK)
            return entity.getY() - result.getLocation().y;

        // No block found below within 5 blocks
        return Double.MAX_VALUE;
    }

}
