package com.amuzil.av3.data.attachment;

import com.amuzil.magus.registry.Registries;
import com.amuzil.magus.skill.Skill;
import com.amuzil.magus.skill.SkillCategory;
import com.amuzil.magus.skill.data.SkillCategoryData;
import com.amuzil.magus.skill.data.SkillData;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;


public class BenderData implements IAttachmentSerializer<CompoundTag, BenderData> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int DATA_VERSION = 1; // Update this as your data structure changes
    public final List<SkillCategoryData> skillCategoryData = new ArrayList<>();
    public final Map<String, SkillData> skillDataMap = new HashMap<>();

    public BenderData() {
        // Initialize skill category data
        for (SkillCategory category: Registries.getSkillCategories())
            skillCategoryData.add(new SkillCategoryData(category));
        // Initialize skill data
        for (Skill skill: Registries.getSkills())
            skillDataMap.put(skill.name(), new SkillData(skill));
        LOGGER.info("BenderData INITIAL LOAD");
    }

    @Override
    public BenderData read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
        System.out.println("read BenderData from NBT");
        BenderData data = new BenderData();
        data.deserializeNBT(provider, tag);
        return data;
    }

    @Override
    public CompoundTag write(BenderData data, HolderLookup.Provider provider) {
        return data.serializeNBT(provider);
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("DataVersion", DATA_VERSION);
        skillCategoryData.forEach(catData -> tag.put(catData.name(), catData.serializeNBT(provider)));
        skillDataMap.values().forEach(skillData -> tag.put(skillData.name(), skillData.serializeNBT(provider)));
        return tag;
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        // Default to version 0 if not present
        int version = tag.contains("DataVersion") ? tag.getInt("DataVersion") : 0;
        System.out.println("LOAD EM UP | deserializeNBT BenderData version: " + version);
        switch (version) {
            case 1 -> {
                LOGGER.info("Loading Bender data version: {}", version);
                for (SkillCategoryData catData : skillCategoryData) {
                    if (tag.contains(catData.name(), Tag.TAG_COMPOUND)) {
                        catData.deserializeNBT(provider, tag.getCompound(catData.name()));
                    } else {
                        LOGGER.warn("Missing skill category data for: {}", catData.name());
                    }
                }

                for (SkillData skillData : skillDataMap.values()) {
                    if (tag.contains(skillData.name(), Tag.TAG_COMPOUND)) {
                        skillData.deserializeNBT(provider, tag.getCompound(skillData.name()));
                    } else {
                        LOGGER.warn("Missing skill data for: {}", skillData.name());
                    }
                }
            } default -> { // Handle unknown versions for migrating data
                // Set defaults here for new player / world
                LOGGER.info("Unknown Bender data version: {}", version);
            }
        }
    }
}
