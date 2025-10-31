package com.amuzil.omegasource.entity.renderer;

import org.joml.Vector3f;

public final class Vertex {
    public Vector3f position;
    public Vector3f normal;

    public Vertex(Vector3f position, Vector3f normal) {
        this.position = position;
        this.normal = normal;
    }
}
