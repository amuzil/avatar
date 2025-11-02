package com.amuzil.magus.skill;

import com.amuzil.magus.registry.Registries;
import net.minecraft.resources.ResourceLocation;


public abstract class SkillCategory {
    protected final String name;
    protected final ResourceLocation id;

    public SkillCategory(String modId, String name) {
        this.id = ResourceLocation.fromNamespaceAndPath(modId, name);
        this.name = name;
        Registries.registerSkillCategory(this);
    }

    public ResourceLocation getId() {
        return id;
    }

    public String name() { return name; }
}
