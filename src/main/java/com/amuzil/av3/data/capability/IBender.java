package com.amuzil.av3.data.capability;

import com.amuzil.av3.bending.BendingSelection;
import com.amuzil.av3.bending.element.Element;
import com.amuzil.av3.data.attachment.BenderData;
import com.amuzil.magus.form.Form;
import com.amuzil.magus.skill.Skill;
import com.amuzil.magus.skill.data.SkillCategoryData;
import com.amuzil.magus.skill.data.SkillData;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.List;


/**
 * This interface is used to define the capability of bending elements.
 * It allows for serialization and deserialization of the element & bending skill data.
 * Remember to call markDirty() to ensure proper syncing
 */
public interface IBender extends INBTSerializable<CompoundTag> {

    Vec3 getDeltaMovement();

    void setDeltaMovement(Vec3 lastDeltaMovement);

    void register();

    void unregister();

    List<Form> getFormPath();

    SkillCategoryData getSkillCategoryData(Element element);

    SkillCategoryData getSkillCategoryData(ResourceLocation id);

    SkillData getSkillData(Skill skill);

    SkillData getSkillData(String name);

    void resetSkillData();

    void resetSkillData(Skill skill);

    void setAvatar();

    Element getElement();

    void setElement(Element activeElement);

    void setCanUseElement(boolean canUse, Element element);

    void setCanUseSkill(boolean canUse, String skillId);

    void setCanUseAllElements();

    void setCanUseAllSkills();

    void setCanUseAllSkills(Element element);

    BenderData getBenderData();

    void setBenderData(BenderData data);

    void setSelection(BendingSelection selection);
    BendingSelection getSelection();

    // Remember to call this in *every* data update!
    void markDirty();

    // Mark data as clean
    void markClean();

    // Check if data needs to be synced across client / server
    boolean isDirty();

    void syncDeltaMovementToServer();

    void syncSelectionToServer();

    void syncToClient();
    // Save data

    @Override
    CompoundTag serializeNBT(HolderLookup.Provider provider);
    // Load data

    @Override
    void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag);
}
