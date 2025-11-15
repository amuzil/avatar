package com.amuzil.carryon.physics.bullet.collision.space;

import com.amuzil.av3.network.AvatarNetwork;
import com.amuzil.carryon.api.event.collision.CollisionEvent;
import com.amuzil.carryon.api.event.space.PhysicsSpaceEvent;
import com.amuzil.carryon.physics.bullet.collision.body.ElementRigidBody;
import com.amuzil.carryon.physics.bullet.collision.body.EntityRigidBody;
import com.amuzil.carryon.physics.bullet.collision.body.TerrainRigidBody;
import com.amuzil.carryon.physics.bullet.collision.space.cache.ChunkCache;
import com.amuzil.carryon.physics.bullet.collision.space.generator.TerrainGenerator;
import com.amuzil.carryon.physics.bullet.collision.space.storage.SpaceStorage;
import com.amuzil.carryon.physics.bullet.thread.PhysicsThread;
import com.amuzil.carryon.physics.network.CarryonNetwork;
import com.amuzil.carryon.physics.network.impl.ForceCloudSpawnPacket;
import com.amuzil.carryon.physics.network.impl.SendRigidBodyMovementPacket;
import com.amuzil.carryon.physics.network.impl.SendRigidBodyPropertiesPacket;
import com.amuzil.magus.physics.core.ForceCloud;
import com.amuzil.magus.physics.core.ForceSystem;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This is the main physics simulation used by Carryon. Each bullet simulation
 * update happens asynchronously while all the setup, input, or otherwise
 * user defined behavior happens on the game logic thread. <br>
 * <br>
 * It is also worth noting that another simulation step will not be performed if
 * the last step has taken longer than 50ms and is still executing upon the next
 * tick. This really only happens if you are dealing with an ungodly amount of
 * rigid bodies or your computer is slo.
 *
 * @see PhysicsThread
 * @see PhysicsSpaceEvent
 */
@SuppressWarnings("deprecation")
public class MinecraftSpace extends PhysicsSpace implements PhysicsCollisionListener {
    private final CompletableFuture<?>[] futures = new CompletableFuture[3];
    private final Map<BlockPos, TerrainRigidBody> terrainMap;
    private final PhysicsThread thread;
    private final Level level;
    private final ChunkCache chunkCache;
    private final Set<SectionPos> previousBlockUpdates;
    // Handles non-rigid body forces like gravity, drag, and buoyancy
    private final ForceSystem forceSystem;
    private volatile boolean stepping;

    public MinecraftSpace(PhysicsThread thread, Level level) {
        super(BroadphaseType.DBVT);
        this.thread = thread;
        this.level = level;
        this.previousBlockUpdates = new HashSet<>();
        this.chunkCache = ChunkCache.create(this);
        this.terrainMap = new ConcurrentHashMap<>();
        this.setGravity(new Vector3f(0, -9.807f, 0));
        this.addCollisionListener(this);
        this.setAccuracy(1f / 60f);
        this.forceSystem = new ForceSystem(this);
    }

    /**
     * Allows users to retrieve the {@link MinecraftSpace} associated with any given
     * {@link Level} object (client or server).
     *
     * @param level the level to get the physics space from
     * @return the {@link MinecraftSpace}
     */
    public static MinecraftSpace get(Level level) {
        return ((SpaceStorage) level).getSpace();
    }

    public static Optional<MinecraftSpace> getOptional(Level level) {
        return Optional.ofNullable(get(level));
    }

    /**
     * This method performs the following steps:
     * <ul>
     * <li>Fires world step events in {@link PhysicsSpaceEvent}.</li>
     * <li>Steps {@link ElementRigidBody}s.</li>
     * <li>Steps the simulation asynchronously.</li>
     * <li>Triggers collision events.</li>
     * </ul>
     * <p>
     * Additionally, none of the above steps execute when either the world is empty
     * (no {@link PhysicsRigidBody}s) or when the game is paused.
     *
     * @see TerrainGenerator
     * @see PhysicsSpaceEvent
     */
    public void step() {
        // Keep existing rigidbody per-frame update
        MinecraftSpace.get(this.level)
                .getRigidBodiesByClass(ElementRigidBody.class)
                .forEach(ElementRigidBody::updateFrame);

        // If we're already mid-step, don't schedule another
        if (this.stepping) {
            return;
        }

        // Nothing to simulate: no rigid bodies and no force clouds
        if (this.isEmpty() && this.forceSystem.clouds().isEmpty()) {
            return;
        }

        this.stepping = true;

        // Run the whole physics + force system step on the physics worker as ONE job
        CompletableFuture.runAsync(() -> {
            try {
                // Wake nearby rigidbodies based on recent block updates
                for (var rigidBody : this.getRigidBodiesByClass(ElementRigidBody.class)) {
                    if (!rigidBody.terrainLoadingEnabled()) {
                        continue;
                    }
                    for (var blockPos : this.previousBlockUpdates) {
                        if (rigidBody.isNear(blockPos)) {
                            rigidBody.activate();
                            break;
                        }
                    }
                }
                this.previousBlockUpdates.clear();

                this.chunkCache.refreshAll();

                // Substeps for Bullet + ForceSystem, all on the SAME thread
                final float subDt    = 1f / 60f;
                final int   subSteps = 3;

                for (int i = 0; i < subSteps; i++) {
                    // Bullet collision events
                    this.distributeEvents();

                    // World Step event
                    NeoForge.EVENT_BUS.post(new PhysicsSpaceEvent.Step(this));

                    // Bullet integration
                    this.update(subDt);

                    // Your particle / force system
                    this.forceSystem.tick(subDt);
                }

                // Optional debug
//            int clouds = 0;
//            for (ForceCloud cloud : forceSystem.clouds()) {
//                clouds++;
//                System.out.println("[Physics] Cloud " + System.identityHashCode(cloud)
//                        + " has " + cloud.points().size() + " points.");
//            }
//            System.out.println("[Physics] Total clouds: " + clouds);

            } finally {
                this.stepping = false;
            }
        }, this.getWorkerThread());
    }

    public ForceSystem forceSystem() {
        return forceSystem;
    }

    @Override
    public void addCollisionObject(PhysicsCollisionObject collisionObject) {
        if (!collisionObject.isInWorld()) {
            if (collisionObject instanceof ElementRigidBody rigidBody) {
                NeoForge.EVENT_BUS.post(new PhysicsSpaceEvent.ElementAdded(this, rigidBody));

                if (!rigidBody.isInWorld()) {
                    rigidBody.activate();
                    rigidBody.getFrame().set(rigidBody.getPhysicsLocation(new Vector3f()), rigidBody.getPhysicsLocation(new Vector3f()), rigidBody.getPhysicsRotation(new Quaternion()), rigidBody.getPhysicsRotation(new Quaternion()));
                    rigidBody.updateBoundingBox();
                }

                if (this.isServer() && rigidBody instanceof EntityRigidBody entityRigidBody) {
                    CarryonNetwork.sendToPlayersTrackingEntity(entityRigidBody.getElement().cast(), new SendRigidBodyMovementPacket(entityRigidBody));
                    CarryonNetwork.sendToPlayersTrackingEntity(entityRigidBody.getElement().cast(), new SendRigidBodyPropertiesPacket(entityRigidBody));
                }
            } else if (collisionObject instanceof TerrainRigidBody terrain) {
                this.terrainMap.put(terrain.getBlockPos(), terrain);
            }

            super.addCollisionObject(collisionObject);
        }
    }

    public void addForceCloud(ForceCloud forceCloud, Entity spawner) {
        if (forceSystem != null) {
            // TODO: Set frames here and post a spawn event
            if (!forceSystem.clouds().contains(forceCloud)) {
                if (this.isServer()) {
                    // Need to figure out how to sync properly here
                    PacketDistributor.sendToPlayersTrackingEntity(spawner, new ForceCloudSpawnPacket(forceCloud));
                }
                forceSystem.addCloud(forceCloud);
            }
        }
    }

    public void addForceCloud(ForceCloud forceCloud) {
        if (forceSystem != null) {
            // TODO: Set frames here and post a spawn event
            if (!forceSystem.clouds().contains(forceCloud)) {
                forceSystem.addCloud(forceCloud);
            }
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

    public boolean isServer() {
        return this.getWorkerThread().getParentExecutor() instanceof MinecraftServer;
    }

    public boolean isStepping() {
        return this.stepping;
    }

    public void doBlockUpdate(BlockPos blockPos) {
        this.previousBlockUpdates.add(SectionPos.of(blockPos));
    }

    public void wakeNearbyElementRigidBodies(BlockPos blockPos) {
        for (var rigidBody : this.getRigidBodiesByClass(ElementRigidBody.class)) {
            if (!rigidBody.terrainLoadingEnabled())
                continue;

            if (rigidBody.isNear(blockPos))
                rigidBody.activate();
        }
    }

    public Map<BlockPos, TerrainRigidBody> getTerrainMap() {
        return new HashMap<>(this.terrainMap);
    }

    public Optional<TerrainRigidBody> getTerrainObjectAt(BlockPos blockPos) {
        return Optional.ofNullable(this.terrainMap.get(blockPos));
    }

    public void removeTerrainObjectAt(BlockPos blockPos) {
        final var removed = this.terrainMap.remove(blockPos);

        if (removed != null)
            this.removeCollisionObject(removed);
    }

    public <T> List<T> getRigidBodiesByClass(Class<T> type) {
        var out = new ArrayList<T>();

        for (var body : getRigidBodyList()) {
            if (type.isAssignableFrom(body.getClass()))
                out.add(type.cast(body));
        }

        return out;
    }

    public PhysicsThread getWorkerThread() {
        return this.thread;
    }

    public Level level() {
        return this.level;
    }

    public ChunkCache getChunkCache() {
        return this.chunkCache;
    }

    /**
     * Trigger all collision events (e.g. block/element or element/element).
     *
     * @param event the event context
     */
    @Override
    public void collision(PhysicsCollisionEvent event) {
        float impulse = event.getAppliedImpulse();

        /* Element on Element */
        if (event.getObjectA() instanceof ElementRigidBody rigidBodyA && event.getObjectB() instanceof ElementRigidBody rigidBodyB)
            NeoForge.EVENT_BUS.post(new CollisionEvent(CollisionEvent.Type.ELEMENT, rigidBodyA, rigidBodyB, impulse));
            /* Block on Element */
        else if (event.getObjectA() instanceof TerrainRigidBody terrain && event.getObjectB() instanceof ElementRigidBody rigidBody)
            NeoForge.EVENT_BUS.post(new CollisionEvent(CollisionEvent.Type.BLOCK, rigidBody, terrain, impulse));
            /* Element on Block */
        else if (event.getObjectA() instanceof ElementRigidBody rigidBody && event.getObjectB() instanceof TerrainRigidBody terrain)
            NeoForge.EVENT_BUS.post(new CollisionEvent(CollisionEvent.Type.BLOCK, rigidBody, terrain, impulse));
    }
}