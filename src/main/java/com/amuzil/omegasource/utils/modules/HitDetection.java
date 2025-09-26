package com.amuzil.omegasource.utils.modules;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BasePressurePlateBlock;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;


public class HitDetection {

//    /**
//     * Short raytrace detector: shoots a ray from eye position forward up to {@code maxDistance}
//     * and collects any entities intersecting that ray, filtered by {@code filter}.
//     */
//    public static <T extends Entity> List<T> rayTraceEntities(
//            Entity source, double maxDistance, Predicate<Entity> filter, Class<T> type
//    ) {
//        Vec3 eye   = source.getEyePosition(1f);
//        Vec3 look  = source.getLookAngle();
//        Vec3 end   = eye.add(look.scale(maxDistance));
//        HitResult result = source.level().clip(new ClipContext(
//                eye, end,
//                ClipContext.Block.OUTLINE,
//                ClipContext.Fluid.NONE,
//                source
//        ));
//
//        AABB rayBox = source.getBoundingBox().expandTowards(look.scale(maxDistance)).inflate(1.0);
//        List<Entity> candidates = source.level().getEntities(source, rayBox, filter);
//        List<T> hits = new ArrayList<>();
//        for (Entity e : candidates) {
//            if (!type.isInstance(e)) continue;
//            AABB bb = e.getBoundingBox().inflate(0.1);
//            if (bb.clip(eye, end) != HitResult.Type.MISS) {
//                hits.add(type.cast(e));
//            }
//        }
//        return hits;
//    }

    /**
     * Returns entities within a scaled version of {@code source}'s bounding box.
     * {@code scale} of 1.0 = original box, <1 shrinks, >1 expands.
     */
    public static <T extends Entity> List<T> getEntitiesWithinScaledBox(
            Entity source, double scale, Predicate<Entity> filter, Class<T> type
    ) {
        AABB box = source.getBoundingBox();
        Vec3 center = box.getCenter();
        double hx = box.getXsize()  * scale * 0.5;
        double hy = box.getYsize()  * scale * 0.5;
        double hz = box.getZsize()  * scale * 0.5;
        AABB scaled = new AABB(
                center.x - hx, center.y - hy, center.z - hz,
                center.x + hx, center.y + hy, center.z + hz
        );
        List<T> out = new ArrayList<>();
        for (Entity e : source.level().getEntities(source, scaled, filter)) {
            if (type.isInstance(e)) out.add(type.cast(e));
        }
        return out;
    }

    /**
     * T
     * @param source
     * @param growth Rather than scaling the bb, this value is merely added in all directions.
     * @param filter
     * @param type
     * @return
     * @param <T>
     */
    public static <T extends Entity> List<T> getEntitiesWithinBox(
            Entity source, double growth, Predicate<Entity> filter, Class<T> type
    ) {
        AABB box = source.getBoundingBox();
        List<T> out = new ArrayList<>();
        AABB newBox = box.inflate(growth / 2);
        for (Entity e : source.level().getEntities(source, newBox, filter)) {
            if (type.isInstance(e)) out.add(type.cast(e));
        }
        return out;
    }

    /**
     * Returns entities within {@code radius} blocks (Manhattan) around {@code source}.
     * Useful for area effects like redstone range checks, etc.
     */
    public static <T extends Entity> List<T> getEntitiesWithinBlockRadius(
            Entity source, int radius, Predicate<Entity> filter, Class<T> type
    ) {
        BlockPos pos = source.blockPosition();
        AABB range = new AABB(
                pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius,
                pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius
        );
        List<T> out = new ArrayList<>();
        for (Entity e : source.level().getEntities(source, range, filter)) {
            if (type.isInstance(e)) out.add(type.cast(e));
        }
        return out;
    }

    /**
     * Detects nearby “redstone” interactables (levers, buttons, pressure plates)
     * within {@code radius} blocks of {@code source}. Returns their BlockPos locations.
     */
    public static List<BlockPos> detectRedstone(
            Level level, BlockPos origin, int radius
    ) {
        List<BlockPos> hits = new ArrayList<>();
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                for (int dz = -radius; dz <= radius; dz++) {
                    BlockPos pos = origin.offset(dx, dy, dz);
                    BlockState state = level.getBlockState(pos);
                    if (state.getBlock() instanceof LeverBlock
                            || state.getBlock() instanceof ButtonBlock
                            || state.getBlock() instanceof BasePressurePlateBlock) {
                        hits.add(pos);
                    }
                }
            }
        }
        return hits;
    }
}
