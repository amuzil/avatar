package com.amuzil.av3.renderer.sdf.transforms;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Transform {
    public final Vector3f position = new Vector3f();
    public final Quaternionf rotation = new Quaternionf(); // identity
    public final Vector3f scale = new Vector3f(1,1,1);

    private final Matrix4f toWorld = new Matrix4f();
    private final Matrix4f invWorld = new Matrix4f();

    public Transform() {}

    public Transform set(Vector3f p, Quaternionf r, Vector3f s) {
        position.set(p); rotation.set(r); scale.set(s);
        return this;
    }

    public Transform computeMatrices() {
        toWorld.identity()
                .translate(position)
                .rotate(rotation)
                .scale(scale);
        invWorld.set(toWorld).invert();
        return this;
    }

    public void worldToLocal(Vector3f world, Vector3f outLocal) {
        outLocal.set(world).mulPosition(invWorld);
    }
}
