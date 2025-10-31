package com.amuzil.omegasource.entity.renderer;

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
