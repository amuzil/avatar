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
import com.amuzil.carryon.physics.packet.RayonPacketHandlers;
import com.amuzil.carryon.physics.packet.impl.SendRigidBodyMovementPacket;
import com.amuzil.carryon.physics.packet.impl.SendRigidBodyPropertiesPacket;
import com.amuzil.carryon.physics.utils.maths.Utilities;
import com.jme3.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@SuppressWarnings("deprecation")
public final class ServerEventHandler {
    public static void onBlockUpdate(Level level, BlockState blockState, BlockPos blockPos) {
        MinecraftSpace.getOptional(level).ifPresent(space -> space.doBlockUpdate(blockPos));
    }

    @SubscribeEvent
    public static void onServerStart(ServerAboutToStartEvent event) {
        PhysicsThreadStore.INSTANCE.createServerThread(event.getServer(), Thread.currentThread(), new ServerLevelSupplier(event.getServer()), new ServerEntitySupplier());
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        PhysicsThreadStore.INSTANCE.destroyServerThread();
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END)
            PhysicsThreadStore.checkThrowable(PhysicsThreadStore.INSTANCE.getServerThread());
    }

    @SubscribeEvent
    public static void onStartLevelTick(TickEvent.LevelTickEvent event) {
        if (!event.level.isClientSide && event.phase == TickEvent.Phase.START) {
            MinecraftSpace space = MinecraftSpace.get(event.level);
            space.step();

            EntityCollisionGenerator.step(space);

            for (var rigidBody : space.getRigidBodiesByClass(EntityRigidBody.class)) {
                if (rigidBody.isActive()) {
                    /* Movement */
                    if (rigidBody.isPositionDirty())
                        RayonPacketHandlers.MAIN.send(PacketDistributor.TRACKING_ENTITY.with(rigidBody.getElement()::cast), new SendRigidBodyMovementPacket(rigidBody));

                    /* Properties */
                    if (rigidBody.arePropertiesDirty())
                        RayonPacketHandlers.MAIN.send(PacketDistributor.TRACKING_ENTITY.with(rigidBody.getElement()::cast), new SendRigidBodyPropertiesPacket(rigidBody));
                }

                /* Set entity position */
                var location = rigidBody.getFrame().getLocation(new Vector3f(), 1.0f);
                rigidBody.getElement().cast().absMoveTo(location.x, location.y, location.z);
            }
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (event.getLevel() instanceof Level level) {
            MinecraftSpace space = PhysicsThreadStore.INSTANCE.createPhysicsSpace(level);
            ((SpaceStorage)level).setSpace(space);
            NeoForge.EVENT_BUS.post(new PhysicsSpaceEvent.Init(space));
        }
    }

    @SubscribeEvent
    public static void onElementAddedToSpace(PhysicsSpaceEvent.ElementAdded event) {
        if (event.getRigidBody() instanceof EntityRigidBody entityBody) {
            final var pos = entityBody.getElement().cast().position();
            entityBody.setPhysicsLocation(Convert.toBullet(pos));
        }
    }

    @SubscribeEvent
    public static void onStartTrackingEntity(PlayerEvent.StartTracking event) {
        Entity entity = event.getTarget();
        if (EntityPhysicsElement.is(entity)) {
            var space = MinecraftSpace.get(entity.level());
            space.getWorkerThread().execute(() -> space.addCollisionObject(EntityPhysicsElement.get(entity).getRigidBody()));
        }
    }

    @SubscribeEvent
    public static void onStopTrackingEntity(PlayerEvent.StopTracking event) {
        Entity entity = event.getTarget();
        if (EntityPhysicsElement.is(entity) && Utilities.getTracking(entity).isEmpty()) {
            var space = MinecraftSpace.get(entity.level());
            space.getWorkerThread().execute(() -> space.removeCollisionObject(EntityPhysicsElement.get(entity).getRigidBody()));
        }
    }
}