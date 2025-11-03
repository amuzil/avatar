package com.amuzil.av3.capability;

import com.amuzil.av3.Avatar;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;


public class BenderProvider implements ICapabilityProvider<Player, @Nullable Void, IBender>, INBTSerializable<CompoundTag> {

    private final Bender bender = new Bender();

    public void invalidate() {
        optional.invalidate();
    }

    @Override
    public @Nullable IBender getCapability(Player player, @Nullable Void ctx) {
        return bender;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.Provider provider) {
        return bender.serializeNBT(provider);
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        bender.deserializeNBT(provider, tag);
    }
}
