package com.amuzil.av3.utils.bending;

import com.amuzil.av3.bending.element.Element;
import com.amuzil.av3.entity.projectile.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public final class ProjectileFactory {

    public static @NotNull AvatarDirectProjectile createDirectProjectile(
            Level level, Element element, LivingEntity owner, int lifetime, float size, String fxName) {
        AvatarDirectProjectile projectile = new AvatarDirectProjectile(level);
        setupProjectile(projectile, element, owner, lifetime, size, fxName);
        return projectile;
    }

    public static @NotNull AvatarCurveProjectile createCurveProjectile(
            Level level, Element element, LivingEntity owner, int lifetime, float size, String fxName) {
        AvatarCurveProjectile projectile = new AvatarCurveProjectile(level);
        setupProjectile(projectile, element, owner, lifetime, size, fxName);
        return projectile;
    }

    public static @NotNull AvatarOrbitProjectile createOrbitProjectile(
            Level level, Element element, LivingEntity owner, int lifetime, float size, String fxName) {
        AvatarOrbitProjectile projectile = new AvatarOrbitProjectile(level);
        setupProjectile(projectile, element, owner, lifetime, size, fxName);
        projectile.setPos(owner.position().add(0, owner.getEyeHeight(), 0));
        return projectile;
    }

    public static @NotNull AvatarBoundProjectile createBoundProjectile(
            Level level, Element element, LivingEntity owner, int lifetime, float size, String fxName) {
        AvatarBoundProjectile projectile = new AvatarBoundProjectile(level);
        setupProjectile(projectile, element, owner, lifetime / 3, size, fxName);
        projectile.setPos(owner.position());
        return projectile;
    }

    // Generic AvatarProjectile setup
    private static void setupProjectile(
            AvatarProjectile projectile, Element element, LivingEntity owner, int lifetime, float size, String fxName) {
        projectile.setElement(element);
        projectile.setOwner(owner);
        projectile.setMaxLifetime(lifetime);
        projectile.setWidth(size);
        projectile.setHeight(size);
        projectile.setFX(fxName);
        projectile.setNoGravity(true);
        projectile.setDamageable(false);
    }
}
