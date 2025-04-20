package com.amuzil.omegasource.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;


public class Bender implements IBender {
    private final LivingEntity entity;
    private boolean isDirty = true; // Flag to indicate if data was changed
    private String element = "fire"; // Currently active element
    private HashMap<String, Integer> elementsStat = new HashMap<>();

    public Bender(LivingEntity entity) {
        this.entity = entity;
    }

    @Override
    public String getElement() {
        return element;
    }

    @Override
    public void setElement(String element) {
        this.element = element;
        markDirty();
    }

    @Override
    public void markDirty() {
        this.isDirty = true;
    }

    @Override
    public void markClean() {
        this.isDirty = false;
    }

    @Override
    public boolean isDirty() {
        return this.isDirty;
    }

    @Override
    public CompoundTag serializeNBT() {
        System.out.println("[Bender] Serializing NBT: " + element);
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
