package com.amuzil.omegasource.entity.renderer;

import net.minecraft.world.phys.Vec3;

public class PointData {
    public Vec3 pos;          // world-space position (already includes cellSize)
    public float density;
    public int ix, iy, iz;    // grid-space integer coordinates

    public PointData(Vec3 pos, float density, int ix, int iy, int iz) {
        this.pos = pos;
        this.density = density;
        this.ix = ix; this.iy = iy; this.iz = iz;
    }
}
