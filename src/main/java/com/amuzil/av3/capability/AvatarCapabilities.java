package com.amuzil.av3.capability;

import com.amuzil.av3.Avatar;
import com.amuzil.magus.skill.traits.DataTrait;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;


@EventBusSubscriber(modid = Avatar.MOD_ID)
public final class AvatarCapabilities {
    public static final ResourceLocation ID = Avatar.id("bender");
    public static final EntityCapability<IBender, Void> BENDER = EntityCapability.createVoid(ID, IBender.class);

    @SubscribeEvent
    private static void register(RegisterCapabilitiesEvent event) {
        event.registerEntity(BENDER, EntityType.PLAYER, (entity, ctx) -> new BenderProvider(entity));
    }

//    @SubscribeEvent
//    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event) {
//        if (event.getObject() instanceof Player livingEntity) {
//            BenderProvider provider = new BenderProvider(livingEntity);
//            event.addCapability(BenderProvider.ID, provider);
//            event.addListener(provider::invalidate);
//        }
//    }

    // Save data on death
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        player.getPersistentData().put("BenderCap", player.getCapability(BENDER).serializeNBT());
    }

    // Clone data on respawn
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;

        CompoundTag capData = event.getOriginal().getPersistentData().getCompound("BenderCap");
        event.getEntity().getCapability(BENDER).deserializeNBT(capData);
    }
}
