package com.amuzil.magus.physics.core;

import com.amuzil.carryon.physics.bullet.collision.space.MinecraftSpace;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ForceEmitter {

    private final ForceSystem system;
    private final UUID ownerUuid;
    private final String key; // e.g. "fire_stream_main"

    // Track clouds by ID so ForceSystem stays canonical source of truth.
    private final List<String> cloudIds = new ArrayList<>();

    // Optional debug/visualisation fields. You can drop this entirely if you want.
    private final List<Vec3[][][]> fields = new ArrayList<>();

    public ForceEmitter(ForceSystem system, UUID ownerUuid, String key) {
        this.system = system;
        this.ownerUuid = ownerUuid;
        this.key = key;
    }

    public String key() { return key; }

    public List<String> getCloudIds() {
        return cloudIds;
    }

    public List<Vec3[][][]> vectorFields() {
        return fields;
    }

    /**
     * Spawn + register a new cloud through the system, and attach it to this emitter.
     */
//    public ForceCloud spawnCloud(ForceCloudSpec spec) {
//        // spec should include: type, grid params, lifetime, maybe attachedEntityUuid
//        ForceCloud cloud = system.spawnCloud(
//                ownerUuid,
//                spec.attachedEntityUuid(),
//                spec.type(),
//                spec
//        );
//        cloud.setEmitterKey(this.key); // optional, if you want reverse mapping
//        cloudIds.add(cloud.id);
//        return cloud;
//    }

    /**
     * Remove a cloud controlled by this emitter.
     */
    public void removeCloud(String cloudId) {
        if (cloudIds.remove(cloudId)) {
            system.removeClouds(cloudId);
        }
    }

    /**
     * Kill everything this emitter owns.
     */
    public void destroyAllClouds() {
        system.removeClouds(cloudIds);
        cloudIds.clear();
    }

    /**
     * Optional: per-emitter tick logic (NOT physics).
     * Good place for timed spawning, channel logic, etc.
     */
    public void tick(double dt) {
        fields.clear();

//        for (String id : cloudIds) {
//            ForceCloud cloud = system.getCloud(id);
//            if (cloud == null) continue;
//
//            // If you really want per-emitter vector fields, do it here:
//            // (this is debug / secondary, not core physics)
//            // Vec3[][][] field = cloud.buildVectorField(
//            //         cloud.pos(),
//            //         16, 16, 16,
//            //         0.5
//            // );
//            // fields.add(field);
//        }
//    }
    }
}
