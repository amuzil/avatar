package com.amuzil.omegasource.entity.renderer.sdf;

import org.joml.Vector3f;

public interface SignedDistanceFunction {
    /** Return signed distance at world-space p and time t. Negative = inside. */
    float sd(Vector3f pWorld, float t);

    /** Optional tight AABB for culling (entity local space). */
    default AABB aabb() { return AABB.INF; }

    public final class AABB {
        public final float minX,minY,minZ,maxX,maxY,maxZ;
        public static final AABB INF = new AABB(-1e9f,-1e9f,-1e9f, 1e9f,1e9f,1e9f);
        public AABB(float minX,float minY,float minZ,float maxX,float maxY,float maxZ){
            this.minX=minX; this.minY=minY; this.minZ=minZ;
            this.maxX=maxX; this.maxY=maxY; this.maxZ=maxZ;
        }
        public boolean contains(float x,float y,float z){
            return (x>=minX && y>=minY && z>=minZ && x<=maxX && y<=maxY && z<=maxZ);
        }
    }
}
