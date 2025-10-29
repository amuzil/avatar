package com.amuzil.omegasource.entity.renderer;

import net.minecraft.world.phys.Vec3;

public final class Vertex {
    public Vec3 position;
    public Vec3 normal;

    public Vertex(Vec3 position, Vec3 normal) {
        this.position = position;
        this.normal = normal;
    }
}
