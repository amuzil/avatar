package com.amuzil.av3.entity.renderer.sdf.shapes;

import com.amuzil.av3.entity.renderer.sdf.SignedDistanceFunction;
import com.amuzil.av3.entity.renderer.sdf.transforms.AnimatedTransform;
import org.joml.Vector3f;

public class SDFBox implements SignedDistanceFunction {

    public final AnimatedTransform a = new AnimatedTransform();
    public final Vector3f halfExtents = new Vector3f(1,1,1);

    private final Vector3f local = new Vector3f();
    private final Vector3f q = new Vector3f();

    public SDFBox(Vector3f center, Vector3f halfExtents) {
        this.a.xform.position.set(center);
        this.halfExtents.set(halfExtents);
        this.a.xform.computeMatrices();
    }

    @Override
    public float sd(Vector3f pWorld, float time) {
        // Update animation for this frame
        a.update(time);

        // Transform world point to local space
        a.xform.worldToLocal(pWorld, local);

        // Scale-aware distance (so scaling works correctly)
        q.set(Math.abs(local.x) - halfExtents.x,
                Math.abs(local.y) - halfExtents.y,
                Math.abs(local.z) - halfExtents.z);

        // Standard box SDF
        float outside = (float) Math.sqrt(
                Math.max(q.x,0f)*Math.max(q.x,0f) +
                        Math.max(q.y,0f)*Math.max(q.y,0f) +
                        Math.max(q.z,0f)*Math.max(q.z,0f));
        float inside = Math.min(Math.max(q.x, Math.max(q.y, q.z)), 0f);

        // Combine (negative = inside)
        return outside + inside;
    }

    @Override
    public AABB aabb() {
        // Compute local-space AABB and transform it roughly to world
        Vector3f min = new Vector3f(a.xform.position).sub(halfExtents);
        Vector3f max = new Vector3f(a.xform.position).add(halfExtents);
        return new AABB(min.x, min.y, min.z, max.x, max.y, max.z);
    }
}
