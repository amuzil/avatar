// ============================================================================
// MinecraftSpace.java  (only change: no addCollisionListener; forwards to generator)
// ============================================================================
package com.amuzil.caliber.physics.bullet.collision.space;

import com.amuzil.caliber.api.event.collision.CollisionEvent;
import com.amuzil.caliber.api.event.space.PhysicsSpaceEvent;
import com.amuzil.caliber.physics.bullet.collision.body.ElementRigidBody;
import com.amuzil.caliber.physics.bullet.collision.body.EntityRigidBody;
import com.amuzil.caliber.physics.bullet.collision.body.TerrainRigidBody;
import com.amuzil.caliber.physics.bullet.collision.space.cache.ChunkCache;
import com.amuzil.caliber.physics.bullet.collision.space.generator.PlayerCollisionGenerator;
import com.amuzil.caliber.physics.bullet.collision.space.generator.TerrainGenerator;
import com.amuzil.caliber.physics.bullet.collision.space.storage.SpaceStorage;
import com.amuzil.caliber.physics.bullet.thread.PhysicsThread;
import com.amuzil.caliber.physics.network.CaliberNetwork;
import com.amuzil.caliber.physics.network.impl.SendRigidBodyMovementPacket;
import com.amuzil.caliber.physics.network.impl.SendRigidBodyPropertiesPacket;
import com.jme3.bullet.PhysicsSoftSpace;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class MinecraftSpace extends PhysicsSoftSpace implements PhysicsCollisionListener {
    private final CompletableFuture<?>[] futures = new CompletableFuture[3];
    private final Map<BlockPos, TerrainRigidBody> terrainMap;
    private final PhysicsThread thread;
    private final Level level;
    private final ChunkCache chunkCache;

    private volatile boolean stepping;
    private final Set<SectionPos> previousBlockUpdates;

    public static MinecraftSpace get(Level level) {
        return ((SpaceStorage) level).getSpace();
    }

    public static Optional<MinecraftSpace> getOptional(Level level) {
        return Optional.ofNullable(get(level));
    }

    public MinecraftSpace(PhysicsThread thread, Level level) {
        super(BroadphaseType.DBVT);
        this.thread = thread;
        this.level = level;
        this.previousBlockUpdates = new HashSet<>();
        this.chunkCache = ChunkCache.create(this);
        this.terrainMap = new ConcurrentHashMap<>();
        this.setGravity(new Vector3f(0, -9.807f, 0));
        // NOTE: no addCollisionListener; collision(...) already fires for this class.
        this.setAccuracy(1f / 60f);
    }

    public void step() {
        MinecraftSpace.get(this.level).getRigidBodiesByClass(ElementRigidBody.class)
                .forEach(ElementRigidBody::updateFrame);

        if (!this.isStepping() && !this.isEmpty()) {
            this.stepping = true;

            for (var rigidBody : this.getRigidBodiesByClass(ElementRigidBody.class)) {
                if (!rigidBody.terrainLoadingEnabled()) continue;
                for (var blockPos : this.previousBlockUpdates) {
                    if (rigidBody.isNear(blockPos)) {
                        rigidBody.activate();
                        break;
                    }
                }
            }
            this.previousBlockUpdates.clear();

            this.chunkCache.refreshAll();

            // Step 3 times per tick, re-evaluating forces each step
            for (int i = 0; i < 3; ++i) {
                this.futures[i] = CompletableFuture.runAsync(() -> {
                    // --- pre substep (clear per-substep accumulators) ---
                    PlayerCollisionGenerator.preStep(this);

                    // --- world step event ---
                    NeoForge.EVENT_BUS.post(new PhysicsSpaceEvent.Step(this));

                    // --- Bullet substep ---
                    this.update(1 / 60f);

                    // --- post substep (reserved for future) ---
                    PlayerCollisionGenerator.postStep(this);
                }, this.getWorkerThread());
            }

            CompletableFuture.allOf(this.futures).thenRun(() -> {
                // publish current support state to player mixins once all substeps are done
                PlayerCollisionGenerator.publish(this);
                this.stepping = false;
            });
        }
    }

    @Override
    public void addCollisionObject(PhysicsCollisionObject collisionObject) {
        if (!collisionObject.isInWorld()) {
            if (collisionObject instanceof ElementRigidBody rigidBody) {
                NeoForge.EVENT_BUS.post(new PhysicsSpaceEvent.ElementAdded(this, rigidBody));

                if (!rigidBody.isInWorld()) {
                    rigidBody.activate();
                    rigidBody.getFrame().set(
                            rigidBody.getPhysicsLocation(new Vector3f()),
                            rigidBody.getPhysicsLocation(new Vector3f()),
                            rigidBody.getPhysicsRotation(new Quaternion()),
                            rigidBody.getPhysicsRotation(new Quaternion())
                    );
                    rigidBody.updateBoundingBox();
                }

                if (this.isServer() && rigidBody instanceof EntityRigidBody entityRigidBody) {
                    CaliberNetwork.sendToPlayersTrackingEntity(entityRigidBody.getElement().cast(),
                            new SendRigidBodyMovementPacket(entityRigidBody));
                    CaliberNetwork.sendToPlayersTrackingEntity(entityRigidBody.getElement().cast(),
                            new SendRigidBodyPropertiesPacket(entityRigidBody));
                }
            } else if (collisionObject instanceof TerrainRigidBody terrain) {
                this.terrainMap.put(terrain.getBlockPos(), terrain);
            }

            super.addCollisionObject(collisionObject);
        }
    }

    @Override
    public void removeCollisionObject(PhysicsCollisionObject collisionObject) {
        if (collisionObject.isInWorld()) {
            super.removeCollisionObject(collisionObject);

            if (collisionObject instanceof ElementRigidBody rigidBody)
                NeoForge.EVENT_BUS.post(new PhysicsSpaceEvent.ElementRemoved(this, rigidBody));
            else if (collisionObject instanceof TerrainRigidBody terrain)
                this.removeTerrainObjectAt(terrain.getBlockPos());
        }
    }

    public boolean isServer() { return this.getWorkerThread().getParentExecutor() instanceof MinecraftServer; }
    public boolean isStepping() { return this.stepping; }
    public void doBlockUpdate(BlockPos blockPos) { this.previousBlockUpdates.add(SectionPos.of(blockPos)); }

    public void wakeNearbyElementRigidBodies(BlockPos blockPos) {
        for (var rigidBody : this.getRigidBodiesByClass(ElementRigidBody.class)) {
            if (!rigidBody.terrainLoadingEnabled()) continue;
            if (rigidBody.isNear(blockPos)) rigidBody.activate();
        }
    }

    public Map<BlockPos, TerrainRigidBody> getTerrainMap() { return new HashMap<>(this.terrainMap); }
    public Optional<TerrainRigidBody> getTerrainObjectAt(BlockPos blockPos) { return Optional.ofNullable(this.terrainMap.get(blockPos)); }
    public void removeTerrainObjectAt(BlockPos blockPos) {
        final var removed = this.terrainMap.remove(blockPos);
        if (removed != null) this.removeCollisionObject(removed);
    }

    public <T> List<T> getRigidBodiesByClass(Class<T> type) {
        var out = new ArrayList<T>();
        for (var body : getRigidBodyList()) {
            if (type.isAssignableFrom(body.getClass())) out.add(type.cast(body));
        }
        return out;
    }

    public PhysicsThread getWorkerThread() { return this.thread; }
    public Level level() { return this.level; }
    public ChunkCache getChunkCache() { return this.chunkCache; }

    @Override
    public void collision(PhysicsCollisionEvent event) {
        // forward to the player contact adapter
        PlayerCollisionGenerator.onCollision(this, event);

        float impulse = event.getAppliedImpulse();

        if (event.getObjectA() instanceof ElementRigidBody rigidBodyA && event.getObjectB() instanceof ElementRigidBody rigidBodyB)
            NeoForge.EVENT_BUS.post(new CollisionEvent(CollisionEvent.Type.ELEMENT, rigidBodyA, rigidBodyB, impulse));
        else if (event.getObjectA() instanceof TerrainRigidBody terrain && event.getObjectB() instanceof ElementRigidBody rigidBody)
            NeoForge.EVENT_BUS.post(new CollisionEvent(CollisionEvent.Type.BLOCK, rigidBody, terrain, impulse));
        else if (event.getObjectA() instanceof ElementRigidBody rigidBody && event.getObjectB() instanceof TerrainRigidBody terrain)
            NeoForge.EVENT_BUS.post(new CollisionEvent(CollisionEvent.Type.BLOCK, rigidBody, terrain, impulse));
    }
}