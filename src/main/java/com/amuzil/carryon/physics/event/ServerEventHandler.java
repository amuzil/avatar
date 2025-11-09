package com.amuzil.carryon.physics.event;

import com.amuzil.carryon.api.EntityPhysicsElement;
import com.amuzil.carryon.api.event.space.PhysicsSpaceEvent;
import com.amuzil.carryon.physics.bullet.collision.body.EntityRigidBody;
import com.amuzil.carryon.physics.bullet.collision.space.MinecraftSpace;
import com.amuzil.carryon.physics.bullet.collision.space.generator.EntityCollisionGenerator;
import com.amuzil.carryon.physics.bullet.collision.space.storage.SpaceStorage;
import com.amuzil.carryon.physics.bullet.collision.space.supplier.entity.ServerEntitySupplier;
import com.amuzil.carryon.physics.bullet.collision.space.supplier.level.ServerLevelSupplier;
import com.amuzil.carryon.physics.bullet.math.Convert;
import com.amuzil.carryon.physics.bullet.thread.PhysicsThreadStore;
import com.amuzil.carryon.physics.network.CarryonNetwork;
import com.amuzil.carryon.physics.network.impl.SendRigidBodyMovementPacket;
import com.amuzil.carryon.physics.network.impl.SendRigidBodyPropertiesPacket;
import com.amuzil.carryon.physics.utils.maths.Utilities;
import com.jme3.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

public final class ServerEventHandler {

    /** Called when a block updates — physics should rebuild around it. */
    public static void onBlockUpdate(Level level, BlockState blockState, BlockPos blockPos) {
        MinecraftSpace.getOptional(level).ifPresent(space -> space.doBlockUpdate(blockPos));
    }

    /** Fired when the server is starting — create the physics thread. */
    @SubscribeEvent
    public static void onServerStart(ServerAboutToStartEvent event) {
        PhysicsThreadStore.INSTANCE.createServerThread(
                event.getServer(),
                Thread.currentThread(),
                new ServerLevelSupplier(event.getServer()),
                new ServerEntitySupplier()
        );
    }

    /** Fired when the server stops — cleanup physics threads. */
    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        PhysicsThreadStore.INSTANCE.destroyServerThread();
    }

    /** Server tick — check if physics thread threw an exception. */
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        PhysicsThreadStore.checkThrowable(PhysicsThreadStore.INSTANCE.getServerThread());
    }

    /** Level tick — run physics simulation each tick for that level. */
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Pre event) {
        Level level = event.getLevel();
        if (!level.isClientSide) {
            MinecraftSpace space = MinecraftSpace.get(level);
            space.step();
            EntityCollisionGenerator.step(space);

            for (var rigidBody : space.getRigidBodiesByClass(EntityRigidBody.class)) {
                if (rigidBody.isActive()) {

                    // Movement sync
                    if (rigidBody.isPositionDirty()) {
                        CarryonNetwork.sendToPlayersTrackingEntity(
                                rigidBody.getElement().cast(),
                                new SendRigidBodyMovementPacket(rigidBody)
                        );
                    }

                    // Properties sync
                    if (rigidBody.arePropertiesDirty()) {
                        CarryonNetwork.sendToPlayersTrackingEntity(
                                rigidBody.getElement().cast(),
                                new SendRigidBodyPropertiesPacket(rigidBody)
                        );
                    }
                }

                // Update entity position to physics body location
                var location = rigidBody.getFrame().getLocation(new Vector3f(), 1.0f);
                rigidBody.getElement().cast().absMoveTo(location.x, location.y, location.z);
            }
        }
    }

    /** When a level loads — create its MinecraftSpace for physics. */
    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof Level level) {
            MinecraftSpace space = PhysicsThreadStore.INSTANCE.createPhysicsSpace(level);
            ((SpaceStorage) level).setSpace(space);
            NeoForge.EVENT_BUS.post(new PhysicsSpaceEvent.Init(space));
        }
    }

    /** When a rigid body element is added to a physics space. */
    @SubscribeEvent
    public static void onElementAddedToSpace(PhysicsSpaceEvent.ElementAdded event) {
        if (event.getRigidBody() instanceof EntityRigidBody entityBody) {
            var pos = entityBody.getElement().cast().position();
            entityBody.setPhysicsLocation(Convert.toBullet(pos));
        }
    }

    /** When a player starts tracking an entity, add it to their physics updates. */
    @SubscribeEvent
    public static void onStartTrackingEntity(PlayerEvent.StartTracking event) {
        Entity entity = event.getTarget();
        if (EntityPhysicsElement.is(entity)) {
            var space = MinecraftSpace.get(entity.level());
            space.getWorkerThread().execute(() ->
                    space.addCollisionObject(EntityPhysicsElement.get(entity).getRigidBody()));
        }
    }

    /** When a player stops tracking an entity, remove physics sync if no one else is tracking. */
    @SubscribeEvent
    public static void onStopTrackingEntity(PlayerEvent.StopTracking event) {
        Entity entity = event.getTarget();
        if (EntityPhysicsElement.is(entity) && Utilities.getTracking(entity).isEmpty()) {
            var space = MinecraftSpace.get(entity.level());
            space.getWorkerThread().execute(() ->
                    space.removeCollisionObject(EntityPhysicsElement.get(entity).getRigidBody()));
        }
    }
}
