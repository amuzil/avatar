package com.amuzil.av3.api.magus.skill.traits.skilltraits;

import com.amuzil.av3.api.magus.skill.traits.SkillTrait;
import net.minecraft.nbt.CompoundTag;


/**
 * Supports an R, G, and B value. Designed for 0 - 1D, but you can pass an int.
 */
public class ColourTrait extends SkillTrait {

    private double r, g, b;

    public ColourTrait(String name, double r, double g, double b) {
        super(name);
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public ColourTrait(int r, int g, int b, String name) {
        super(name);
        this.r = r / 255D;
        this.g = g / 255D;
        this.b = b / 255D;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putDouble("Red", r);
        tag.putDouble("Green", g);
        tag.putDouble("Blue", b);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        r = nbt.getDouble("Red");
        g = nbt.getDouble("Green");
        b = nbt.getDouble("Blue");
    }

    public void setR(double r) {
        this.r = r;
        markDirty();
    }

    public void setG(double g) {
        this.g = g;
        markDirty();
    }

    public void setB(double b) {
        this.b = b;
        markDirty();
    }

    public void setRGB(double r, double g, double b) {
        setR(r);
        setG(g);
        setB(b);
    }

    public double getR() {
        return r;
    }

    public double getG() {
        return g;
    }

    public double getB() { return b;}

    @Override
    public void reset() {
        super.reset();
        //Default colour is white.
        setRGB(1D, 1D, 1D);
    }
}
