package com.amuzil.omegasource.api.magus.capability;

import com.amuzil.omegasource.api.magus.capability.entity.Data;
import com.amuzil.omegasource.api.magus.capability.entity.LivingDataCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;


public class CapabilityHandler {
    public static final Capability<Data> LIVING_DATA = CapabilityManager.get(new CapabilityToken<>(){});

    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.register(Data.class);
    }

    public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> e) {
        if (e.getObject() instanceof LivingEntity) {
            //capabilities all living entities get.
            //TODO: Add requirement to check against a list of compatible entities.
            //E.g custom npcs, or specific mobs you want to be able to use Skills.
            e.addCapability(LivingDataCapability.ID, new LivingDataCapability.LivingDataProvider());
            if (e.getObject() instanceof Player) {
                //capabilities just players get.
            }
        }
    }

    @Nullable
    public static <T> T getCapability(Entity entity, Capability<T> capability) {
        if (entity == null) return null;
        return entity.getCapability(capability).orElse(null);
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        player.getCapability(LIVING_DATA).ifPresent(bender -> {
            CompoundTag capData = bender.serializeNBT();
            player.getPersistentData().put("MagiCap", capData);
        });
    }


    public static void init() {
        // Prevents class loading exceptions
        LivingDataCapability.LivingDataProvider.init();
    }
}
