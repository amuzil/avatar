package com.amuzil.omegasource.bending;

import com.amuzil.omegasource.capability.AvatarCapabilities;
import com.amuzil.omegasource.capability.IBender;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;


public class Bender implements IBender {
    private String element = "fire"; // default

    @Override
    public String getElement() {
        return element;
    }

    @Override
    public void setElement(String element) {
        this.element = element;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Element", element);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        System.out.println("[Bender] Deserializing NBT: " + tag.getString("Element"));
        this.element = tag.getString("Element");
    }

    @Override
    public String toString() {
        return "Bender[ " + element + " ]";
    }

    public static IBender getBender(LivingEntity entity) {
        return entity.getCapability(AvatarCapabilities.BENDER)
                .orElse(null);
    }
}
