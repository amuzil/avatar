package com.amuzil.omegasource.entity.renderer;

import net.minecraft.world.phys.Vec3;

import java.util.List;

public final class MeshData {
    public final List<Vec3> vertices;
    public final List<Vec3> normals;
    public MeshData(List<Vec3> vertices, List<Vec3> normals) {
        this.vertices = vertices;
        this.normals = normals;
    }
}
