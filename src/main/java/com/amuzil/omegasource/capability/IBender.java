package com.amuzil.omegasource.capability;

import com.amuzil.omegasource.bending.BendingSelection;
import com.amuzil.omegasource.bending.element.Element;
import com.amuzil.omegasource.network.packets.client.SyncBenderPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.common.util.INBTSerializable;


/**
 * This interface is used to define the capability of bending elements.
 * It allows for serialization and deserialization of the element & skill data.
 * Remember to call markDirty() or send {@link SyncBenderPacket} after every data update to ensure proper syncing
 */
@AutoRegisterCapability
public interface IBender extends INBTSerializable<CompoundTag> {

    void setAvatar();

    Element getElement();

    void setElement(Element activeElement);

    void setCanUseElement(boolean canUse, Element element);

    void setCanUseSkill(boolean canUse, ResourceLocation skillId);

    void setCanUseAllElements();

    void setCanUseAllSkills();

    void setCanUseAllSkills(Element element);

    void setSelectionTarget(BendingSelection.Target selection);

    // Reset all non-persistent data
    void reset();

    // Remember to call this in *every* data update!
    void markDirty();

    // Mark data as clean
    void markClean();

    // Check if data needs to be synced across client / server
    boolean isDirty();

    // Save data
    CompoundTag serializeNBT();

    // Load data
    void deserializeNBT(CompoundTag tag);
}
