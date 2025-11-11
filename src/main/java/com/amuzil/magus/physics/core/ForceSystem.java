package com.amuzil.magus.physics.core;

import com.amuzil.carryon.physics.bullet.collision.space.MinecraftSpace;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;


public class ForceSystem {

    private final MinecraftSpace space;
    private final ExecutorService workerPool;
    private final List<ForceCloud> clouds = new ArrayList<>();

    // global cell size for all clouds in this space
    private final double cellSize = 0.25;

    // simple collision params – tune these later
    private final double selfRestRadius = cellSize / 4;
    private final double selfStiffness = 50.0;
    private final double selfDamping  = 5.0;

    private final double crossRestRadius = cellSize / 2;
    private final double crossStiffness  = 80.0;
    private final double crossDamping    = 8.0;

    public ForceSystem(MinecraftSpace space) {
        this.space = space;
        // Not multithreading here for now because this is automatically threaded with MinecraftSpace
        this.workerPool = null;
    }

    public ForceCloud createCloud(int type, int maxPoints, Vec3 pos, Vec3 vel, Vec3 force) {
        // choose some grid dims; you can make this smarter later
        ForceCloud cloud = new ForceCloud(
                type,
                maxPoints,
                pos,
                vel,
                force,
                workerPool
        );
        this.clouds.add(cloud);
        return cloud;
    }

    public void addCloud(ForceCloud cloud) {
        if (!clouds.contains(cloud)) {
            clouds.add(cloud);
        }
    }

    public void removeCloud(ForceCloud cloud) {
        clouds.remove(cloud);
    }

    public List<ForceCloud> clouds() {
        return clouds;
    }

    /**
     * Main per-tick update. Call this from MinecraftSpace.step().
     */
    public void tick(double dt) {
        if (clouds.isEmpty()) return;

        // 1) tick each cloud (modules + integration + bounds)
        for (ForceCloud cloud : clouds) {
//            System.out.println("Cloud Ticking.");
            cloud.tick(dt);
            cloud.rebuildSpatialGrid();
        }

        // 2) rebuild each cloud's point grid
//        for (ForceCloud cloud : clouds) {
//            cloud.rebuildSpatialGrid();
//        }

//         3) self-collisions
//        for (ForceCloud cloud : clouds) {
//            cloud.resolveSelfCollisions(selfRestRadius, selfStiffness, selfDamping);
//        }

//         4) cloud-cloud collisions. n^2 for now; optimize later if needed
        int n = clouds.size();
        for (int i = 0; i < n; i++) {
            ForceCloud a = clouds.get(i);
            for (int j = i + 1; j < n; j++) {
                ForceCloud b = clouds.get(j);
                if (!a.boundsOverlap(b)) continue;
                // Rebuilds the grid for collision
//                System.out.println("Hey?");
//                b.rebuildSpatialGrid();
//                a.rebuildSpatialGrid();
                collideClouds(a, b);
            }
        }

        clouds.removeIf(ForceCloud::isDead);
    }

    private void collideClouds(ForceCloud a, ForceCloud b) {
        double r = crossRestRadius;

        for (ForcePoint p : a.points()) {
            List<ForcePoint> neighbours = b.grid().queryRadius(p.pos(), r);
            for (ForcePoint q : neighbours) {
                ForceCloud.resolvePair(p, q, r, crossStiffness, crossDamping);
            }
        }
    }
}

