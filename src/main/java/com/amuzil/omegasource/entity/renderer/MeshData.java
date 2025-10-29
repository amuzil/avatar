package com.amuzil.omegasource.entity.renderer;

import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.List;

public final class MeshData {
    public final List<Vector3f> vertices;
    public final List<Vector3f> normals;
    public MeshData(List<Vector3f> vertices, List<Vector3f> normals) {
        this.vertices = vertices;
        this.normals = normals;
    }
}
