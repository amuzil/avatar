package com.amuzil.av3.utils.bending;

import net.minecraft.world.entity.Entity;
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

    public static Vec3 getPivot(Entity entity, float scale) {
        // Calculate the pivot point in front of the player (avg scale: 1.7)
        Vec3[] pose = new Vec3[]{entity.position(), entity.getLookAngle()};
        pose[1] = pose[1].scale((scale)).add((0), (entity.getEyeHeight()), (0));
        return pose[1].add(pose[0]);
    }

    // Right side pivot methods based on eye position.
    public static Vec3 getRightPivot(Entity entity, float scale) {
        return getRightPivot(entity, 0.8f, scale);
    }

    public static Vec3 getRightPivot(Entity entity, float rightScale, float scale) {
        Vec3 eyePos = entity.getEyePosition();
        return getRightPivot(entity, eyePos, rightScale, scale);
    }

    // This works for any given origin position
    public static Vec3 getRightPivot(Entity entity, Vec3 origin, float rightScale, float scale) {
        Vec3 look = entity.getLookAngle().normalize();
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 right = look.cross(up).normalize(); // cross product gives right vector
        return origin.add(right.scale(rightScale)).add(look.scale(scale));
    }

    public static Vec3 getLeftPivot(Entity entity, Vec3 origin, float leftScale, float scale) {
        return getRightPivot(entity, origin, leftScale * -1, scale);
    }

    public static Vec3 getRightPivot(Entity entity, float scale, double offset) {
        Vec3 eyePos = entity.getEyePosition();
        Vec3 look = entity.getLookAngle().normalize();
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 right = look.cross(up).normalize(); // cross product gives right vector
        return eyePos.add(right.scale(offset)).add(look.scale(scale));
    }

}
