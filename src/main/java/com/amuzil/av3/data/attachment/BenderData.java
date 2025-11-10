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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BenderData implements IAttachmentSerializer<CompoundTag, BenderData> {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final int DATA_VERSION = 1; // Update this as your data structure changes
    public final List<SkillCategoryData> skillCategoryData = new ArrayList<>();
    public final Map<String, SkillData> skillDataMap = new HashMap<>();
    private boolean initialized = false;

    public BenderData() {
        for (SkillCategory category: Registries.getSkillCategories())
            skillCategoryData.add(new SkillCategoryData(category));
        for (Skill skill: Registries.getSkills())
            skillDataMap.put(skill.name(), new SkillData(skill));
        initialized = true;
    }

    public BenderData(CompoundTag tag, HolderLookup.Provider provider) {
        this.deserializeNBT(provider, tag);
        initialized = true;
    }

    @Override
    public BenderData read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
        if (initialized)
            return this;
        else
            return new BenderData(tag, provider);
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

    /** NOTE: If you change the data structure in ANY way and want to overwrite the old data with defaults,
     comment out the deserializing of whatever data changed. Then load the game and save and quit to overwrite.
     ---
     Now, if you want to migrate the data, bump the DATA_VERSION and add a new case in the switch statement.
     After that, modify the previous case to handle the migration by loading up the old data to fit into the new.
     */
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        // Default to version 0 if not present
        int version = tag.contains("DataVersion") ? tag.getInt("DataVersion") : 0;
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
