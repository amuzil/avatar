package com.amuzil.av3.api.carryon.physics.mixin.common;

import com.amuzil.av3.api.carryon.physics.mixin.common.entity.TrackedEntityMixin;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.server.level.ChunkMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ChunkMap.class)
public interface ChunkMapMixin {
    @Accessor("entityMap")
    Int2ObjectMap<TrackedEntityMixin> rayon$getEntityMap();
}
