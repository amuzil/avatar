package com.amuzil.omegasource.entity.renderer;

import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Objects;

public final class CachedMesh {
    final List<Vec3> positions;
    final List<Vec3> normals;
    final long builtAtMs;
    CachedMesh(List<Vec3> p, List<Vec3> n, long t) {
        this.positions = Objects.requireNonNull(p);
        this.normals   = Objects.requireNonNull(n);
        this.builtAtMs = t;
    }
}
