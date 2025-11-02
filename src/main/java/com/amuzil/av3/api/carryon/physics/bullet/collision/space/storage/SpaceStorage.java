package com.amuzil.av3.api.carryon.physics.bullet.collision.space.storage;

import com.amuzil.av3.api.carryon.physics.bullet.collision.space.MinecraftSpace;
import net.minecraft.world.level.Level;

/**
 * Used for storing a {@link MinecraftSpace} within any {@link Level} object.
 */
public interface SpaceStorage {
    void setSpace(MinecraftSpace space);

    MinecraftSpace getSpace();
}