package com.amuzil.omegasource.api.magus.skill.traits.entitytraits;

import com.amuzil.omegasource.api.magus.skill.traits.DataTrait;
import net.minecraft.nbt.CompoundTag;


/**
 * This serves as a generic wrapper for any kind of meter/energy bar for the player.
 * Chi, mana, qi, nen, stamina, whatever. Other such classes should extend this, because this is the class that will be
 * ticked and adjusted in Magi.java
 *
 * Example of a custom implementation: Chi.java for the Avatar Mod rework.
 * Instead of just the values contained in this class, it will also track your "available" amount of energy, and adjust
 * based on that.
 *
 * Of course, if you need more specific custom behaviour in terms of when to regenerate, how to regenerate energy, e.t.c,
 * then make a child class extending Magi and add your logic there.
 */
public class EnergyTrait implements DataTrait {
    private String name;
    private double maxEnergy;
    private double currentEnergy;
    private boolean isDirty = false;

    public EnergyTrait(String name, double maxEnergy) {
        this.name = name;
        this.maxEnergy = maxEnergy;
        this.currentEnergy = maxEnergy;
    }

    public void setCurrentEnergy(double currentEnergy) {
        this.currentEnergy = currentEnergy;
        markDirty();
    }

    public void changeEnergy(double changeAmount) {
        setCurrentEnergy(currentEnergy + changeAmount);
    }

    public void setMaxEnergy(double maxEnergy) {
        this.maxEnergy = maxEnergy;
        markDirty();
    }

    public double getMaxEnergy() {
        return this.maxEnergy;
    }

    public double getCurrentEnergy() {
        return this.currentEnergy;
    }

    @Override
    public String name() {
        return name;
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
        CompoundTag tag = new CompoundTag();
        tag.putString("name", name());
        tag.putDouble("max_energy", maxEnergy);
        tag.putDouble("current_energy", currentEnergy);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.name = nbt.getString("name");
        this.maxEnergy = nbt.getDouble("max_energy");
        this.currentEnergy = nbt.getDouble("current_energy");
    }
}
