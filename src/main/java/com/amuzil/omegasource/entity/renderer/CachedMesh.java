package com.amuzil.omegasource.entity.renderer;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;
import java.util.Objects;

public final class CachedMesh {
    final List<Triangle> triangles;
    final long builtAtMs;
    CachedMesh(List<Triangle> p, long t) {
        this.triangles = Objects.requireNonNull(p);
        this.builtAtMs = t;
    }
}
