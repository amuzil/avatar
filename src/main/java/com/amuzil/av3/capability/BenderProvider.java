package com.amuzil.av3.capability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

@Deprecated
public class BenderProvider implements ICapabilityProvider<Player, @Nullable Void, IBender>, INBTSerializable<CompoundTag> {

    private final Bender bender;

    public BenderProvider(Player entity) {
        this.bender = new Bender(entity);
    }

    @Override
    public @Nullable IBender getCapability(@NotNull Player player, @Nullable Void ctx) {
        return bender;
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        return bender.serializeNBT(provider);
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        bender.deserializeNBT(provider, tag);
    }
}
