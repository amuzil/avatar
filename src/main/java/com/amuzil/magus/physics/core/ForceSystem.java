package com.amuzil.magus.physics.core;

import com.amuzil.caliber.physics.bullet.collision.space.MinecraftSpace;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

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
    private final double selfRestRadius = cellSize;
    private final double selfStiffness = 50.0;
    private final double selfDamping = 5.0;

    private final double crossRestRadius = cellSize;
    private final double crossStiffness = 80.0;
    private final double crossDamping = 80.0;

    public ForceSystem(MinecraftSpace space) {
        this.space = space;
        // Not multithreading here for now because this is automatically threaded with MinecraftSpace
        this.workerPool = null;
    }

    public ForceCloud createCloud(int type, int maxPoints, String id, Entity owner, Vec3 pos, Vec3 vel, Vec3 force) {
        // choose some grid dims; you can make this smarter later
        ForceCloud cloud = new ForceCloud(
                type,
                maxPoints,
                id,
                pos,
                vel,
                force,
                owner.getUUID(),
                workerPool
        );
        return cloud;
    }


    public void spawnCloud(ForceCloud cloud, Entity owner) {
//        space.addForceCloud(cloud, owner);
    }

    public void addCloud(ForceCloud cloud) {
        if (!clouds.contains(cloud)) {
            clouds.add(cloud);
        }
    }

    public void removeClouds(ForceCloud... clouds) {
        this.clouds.removeAll(List.of(clouds));
    }

    public void removeCloud(List<ForceCloud> clouds) {
        this.clouds.removeAll(clouds);
    }

    public void removeClouds(String... clouds) {
        List<String> newC = List.of(clouds);
        this.clouds.removeIf(cloud -> newC.contains(cloud.id()));
    }

    public void removeClouds(List<String> ids) {
        this.clouds.removeIf(cloud -> ids.contains(cloud.id()));
    }

    public void removeCloud(String id) {
        removeClouds(id);
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
//            if (cloud.isDead())
//                Thread.dumpStack();
        }

////         3) self-collisions
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

