package com.amuzil.carryon.physics.event;

import com.amuzil.carryon.api.EntityPhysicsElement;
import com.amuzil.carryon.physics.bullet.collision.body.EntityRigidBody;
import com.amuzil.carryon.physics.bullet.collision.space.MinecraftSpace;
import com.amuzil.carryon.physics.bullet.collision.space.generator.EntityCollisionGenerator;
import com.amuzil.carryon.physics.bullet.collision.space.supplier.entity.ClientEntitySupplier;
import com.amuzil.carryon.physics.bullet.collision.space.supplier.level.ClientLevelSupplier;
import com.amuzil.carryon.physics.bullet.thread.PhysicsThreadStore;
import com.amuzil.carryon.physics.network.CarryonNetwork;
import com.amuzil.carryon.physics.network.impl.SendRigidBodyMovementPacket;
import com.amuzil.carryon.physics.utils.debug.CollisionObjectDebugger;
import com.jme3.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@SuppressWarnings({"removal", "deprecation"})
public final class ClientEventHandler {
    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel().isClientSide) {
            MinecraftSpace space = MinecraftSpace.get(event.getLevel());
            space.step();
            EntityCollisionGenerator.step(space);

            for (var rigidBody : space.getRigidBodiesByClass(EntityRigidBody.class)) {
                var player = Minecraft.getInstance().player;

                /* Movement */
                if (rigidBody.isActive() && rigidBody.isPositionDirty() && player != null && player.equals(rigidBody.getPriorityPlayer()))
                    CarryonNetwork.sendToPlayersTrackingEntity(rigidBody.getElement().cast(), new SendRigidBodyMovementPacket(rigidBody));

                /* Set entity position */
                var location = rigidBody.getFrame().getLocation(new Vector3f(), 1.0f);
                rigidBody.getElement().cast().absMoveTo(location.x, location.y, location.z);
            }
        }
    }

    public static void onGameJoin(Minecraft minecraft) {
        PhysicsThreadStore.INSTANCE.createClientThread(minecraft, Thread.currentThread(), new ClientLevelSupplier(minecraft), new ClientEntitySupplier());
    }

    public static void onDisconnect(Minecraft minecraft, ClientLevel level) {
        PhysicsThreadStore.INSTANCE.destroyClientThread();
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        PhysicsThreadStore.checkThrowable(PhysicsThreadStore.INSTANCE.getClientThread());
    }

    @SubscribeEvent
    public static void onDebugRender(RenderLevelStageEvent event) {
        if (CollisionObjectDebugger.isEnabled()) {
            Minecraft mc = Minecraft.getInstance();
            CollisionObjectDebugger.renderSpace(MinecraftSpace.get(mc.level), event.getPoseStack(), event.getPartialTick().getGameTimeDeltaTicks());
        }
    }

    @SubscribeEvent
    public static void onEntityLoad(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            Entity entity = event.getEntity();
            if (EntityPhysicsElement.is(entity)) {
                Level level = entity.level();
                MinecraftSpace.getOptional(level).ifPresent(space -> {
                    space.getWorkerThread().execute(() -> space.addCollisionObject(EntityPhysicsElement.get(entity).getRigidBody()));
                });
            }
        }
    }

    @SubscribeEvent
    public static void onEntityUnload(EntityLeaveLevelEvent event) {
        if (event.getLevel().isClientSide()) {
            Entity entity = event.getEntity();
            if (EntityPhysicsElement.is(entity)) {
                Level level = entity.level();
                MinecraftSpace.getOptional(level).ifPresent(space -> {
                    space.getWorkerThread().execute(() -> space.removeCollisionObject(EntityPhysicsElement.get(entity).getRigidBody()));
                });
            }
        }
    }
}