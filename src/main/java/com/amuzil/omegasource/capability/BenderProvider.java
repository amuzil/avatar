package com.amuzil.omegasource.capability;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.bending.Bender;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;


public class BenderProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "bender");
    private final Bender bender = new Bender();
    private final LazyOptional<IBender> optional = LazyOptional.of(() -> bender);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        return cap == AvatarCapabilities.BENDER ? optional.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        System.out.println("[Bender] Serializing NBT: " + bender.getElement());
        return bender.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        bender.deserializeNBT(nbt);
    }

    public void invalidate() {
        optional.invalidate();
    }
}
