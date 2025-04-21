package com.amuzil.omegasource.capability;

import com.amuzil.omegasource.Avatar;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;


public class BenderProvider implements ICapabilitySerializable<CompoundTag> {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "bender");
    private final Bender bender;
    private final LazyOptional<IBender> optional;

    public BenderProvider(LivingEntity entity) {
        this.bender = new Bender(entity);
        this.optional = LazyOptional.of(() -> bender);
    }

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        return cap == AvatarCapabilities.BENDER ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return bender.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        bender.deserializeNBT(tag);
    }

    public void invalidate() {
        optional.invalidate();
    }
}
