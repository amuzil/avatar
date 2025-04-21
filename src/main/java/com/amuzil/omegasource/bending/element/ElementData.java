//package com.amuzil.omegasource.bending.element;
//
//import com.amuzil.omegasource.api.magus.skill.data.SkillCategoryData;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.resources.ResourceLocation;
//
//public class ElementData extends SkillCategoryData {
//    private final ResourceLocation elementId;
//
//    public ElementData(ResourceLocation elementId) {
//        super();
//        this.elementId = elementId;
//    }
//
//    public ElementData(Element element) {
//        this.element = element.name();
//    }
//
//    @Override
//    public String name() {
//        return element.toString();
//    }
//
//    @Override
//    public void markDirty() {
//        // Implement dirty marking logic if needed
//    }
//
//    @Override
//    public void markClean() {
//        // Implement clean marking logic if needed
//    }
//
//    @Override
//    public boolean isDirty() {
//        return false; // Implement dirty check logic if needed
//    }
//
//    @Override
//    public CompoundTag serializeNBT() {
//        return new CompoundTag(); // Implement serialization logic if needed
//    }
//
//    @Override
//    public void deserializeNBT(CompoundTag tag) {
//        // Implement deserialization logic if needed
//    }
//}
