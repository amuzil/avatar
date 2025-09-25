package com.amuzil.omegasource.utils.physics.core;

import net.minecraftforge.api.distmarker.Dist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Lightweight ForceSystem that builds a coarse broad-phase and produces potential cloud pairs.
 */
public class ForceSystem {

    // potential coarse collision pairs discovered each tick
    private final List<CloudPair> potentialPairs = new ArrayList<>();
    private final ForceGrid<ForceCloud> cloudGrid;
    //TODO: Change this to use a double SpatialGrid approach. One grid stores ForceClouds at the system level,
    // and checks what to compare against. The other adds in data at the ForcePoint level, and is used for finer
    // collisions or visuals.
    // Used to determine what to run on. We want both client and server-side annotations, if possible.
    Dist side;
    private List<ForceEmitter> emitters;

    /**
     * Default constructor - creates a cloud grid with a reasonable default cell size.
     */
    public ForceSystem() {
        this(null, 8.0); // default cell size of 8 world units for coarse culling
    }

    /**
     * Construct with explicit side and cell size.
     */
    public ForceSystem(Dist side, double cloudCellSize) {
        this.side = side;
        this.cloudGrid = new ForceGrid<>(cloudCellSize);
        this.emitters = new ArrayList<>();
    }

    public Dist side() {
        return this.side;
    }

    /**
     * Register an emitter with the system.
     */
    public void addEmitter(ForceEmitter e) {
        if (e == null) return;
        if (emitters == null) emitters = new ArrayList<>();
        if (!emitters.contains(e)) emitters.add(e);
    }

    public void removeEmitter(ForceEmitter e) {
        if (emitters == null) return;
        emitters.remove(e);
    }

    /**
     * Main per-frame tick. Populates the coarse cloud grid, then ticks emitters.
     * Detailed collision/damage dispatch should be added after cloud updates.
     */
    public void tick(double dt) {
        if (emitters == null || emitters.isEmpty()) return;

        // rebuild cloud grid each frame (coarse broad-phase). Clear previous contents first.
        cloudGrid.clear();
        potentialPairs.clear();

        // Insert each cloud into the coarse grid if possible
        for (ForceEmitter e : new ArrayList<>(emitters)) {
            if (e == null) continue;
            try {
                // Use the safe accessor to iterate the emitter's clouds
                for (ForceCloud cloud : e.getClouds()) {
                    if (cloud == null) continue;
                    try {
                        // insert cloud into coarse grid based on cloud.pos()
                        cloudGrid.insert(cloud, cloud.pos());
                    } catch (Exception ex) {
                        // If insertion fails for any cloud (missing pos, etc.), skip it but continue.
                    }
                }
            } catch (Exception ignored) {
            }
        }

        // Tick emitters (which in turn tick their clouds and build vector fields)
        for (ForceEmitter e : new ArrayList<>(emitters)) {
            if (e == null) continue;
            e.tick(dt);
        }

        // --- New: cell-oriented broad-phase using ForceGrid.entries() and neighborKeys() ---
        Map<Long, List<ForceCloud>> entries = cloudGrid.entries();
        // Iterate over each occupied cell, and compare with neighbor cells whose key >= currentKey to avoid duplicates.
        for (Map.Entry<Long, List<ForceCloud>> cellEntry : entries.entrySet()) {
            long keyA = cellEntry.getKey();
            List<ForceCloud> bucketA = cellEntry.getValue();
            if (bucketA == null || bucketA.isEmpty()) continue;

            // neighbors includes same cell and adjacent cells (radius=1)
            List<Long> neighborKeys = cloudGrid.neighborKeys(keyA, 1);
            for (Long keyB : neighborKeys) {
                // enforce ordering to avoid duplicate unordered pairs
                if (keyB < keyA) continue;

                List<ForceCloud> bucketB = entries.get(keyB);
                if (bucketB == null || bucketB.isEmpty()) continue;

                if (keyA == keyB) {
                    // intra-bucket: compare each pair once
                    int n = bucketA.size();
                    for (int i = 0; i < n; i++) {
                        ForceCloud c = bucketA.get(i);
                        if (c == null) continue;
                        Vec3Wrapper posC = safePos(c);
                        if (posC == null) continue;
                        for (int j = i + 1; j < n; j++) {
                            ForceCloud nCloud = bucketA.get(j);
                            if (nCloud == null) continue;
                            try {
                                if (c.id() == nCloud.id()) continue;
                            } catch (Exception ignored) {
                            }
                            Vec3Wrapper posN = safePos(nCloud);
                            if (posN == null) continue;
                            double dist = posC.distanceTo(posN);
                            potentialPairs.add(new CloudPair(c, nCloud, dist));
                        }
                    }
                } else {
                    // inter-bucket: all combinations
                    for (ForceCloud c : bucketA) {
                        if (c == null) continue;
                        Vec3Wrapper posC = safePos(c);
                        if (posC == null) continue;
                        for (ForceCloud nCloud : bucketB) {
                            if (nCloud == null) continue;
                            try {
                                if (c.id() == nCloud.id()) continue;
                            } catch (Exception ignored) {
                            }
                            Vec3Wrapper posN = safePos(nCloud);
                            if (posN == null) continue;
                            double dist = posC.distanceTo(posN);
                            potentialPairs.add(new CloudPair(c, nCloud, dist));
                        }
                    }
                }
            }
        }

        // TODO: Pass potentialPairs into narrow-phase collision solver and damage/knockback dispatch (server-side).
    }

    /**
     * Collects all clouds from all emitters into a single list.
     */
    private List<ForceCloud> collectAllClouds() {
        List<ForceCloud> out = new ArrayList<>();
        if (emitters == null) return out;
        for (ForceEmitter e : emitters) {
            if (e == null) continue;
            try {
                out.addAll(e.getClouds());
            } catch (Exception ignored) {
            }
        }
        return out;
    }

    /**
     * Safe wrapper to read cloud position without throwing.
     */
    private Vec3Wrapper safePos(ForceCloud c) {
        try {
            if (c == null) return null;
            if (c.pos() == null) return null;
            return new Vec3Wrapper(c.pos().x, c.pos().y, c.pos().z);
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * Returns the potential coarse cloud pairs discovered in the last tick. May be empty.
     */
    public List<CloudPair> getPotentialCloudPairs() {
        return Collections.unmodifiableList(potentialPairs);
    }

    /**
     * Simple value-type representing a pair of clouds flagged by the broad-phase.
     * Users should perform narrow-phase tests and event dispatch based on these.
     */
    public static class CloudPair {
        public final ForceCloud a;
        public final ForceCloud b;
        public final double centerDistance;

        public CloudPair(ForceCloud a, ForceCloud b, double centerDistance) {
            this.a = a;
            this.b = b;
            this.centerDistance = centerDistance;
        }
    }

    // Minimal internal Vec3 wrapper to avoid importing Minecraft Vec3 across many contexts here.
    private static class Vec3Wrapper {
        final double x, y, z;

        Vec3Wrapper(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        double distanceTo(Vec3Wrapper o) {
            double dx = x - o.x, dy = y - o.y, dz = z - o.z;
            return Math.sqrt(dx * dx + dy * dy + dz * dz);
        }
    }
}
