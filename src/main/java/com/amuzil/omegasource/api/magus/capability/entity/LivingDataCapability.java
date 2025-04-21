package com.amuzil.omegasource.api.magus.capability.entity;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.api.magus.capability.CapabilityHandler;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.traits.DataTrait;
import com.amuzil.omegasource.api.magus.registry.Registries;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;


public class LivingDataCapability {
    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "living_data");

    public static class LivingDataCapabilityImp implements Data {

        //The amount of data traits the player has should not change after initialisation.
        private final List<DataTrait> traits = new ArrayList<>();
        private final List<SkillCategory> categories = new ArrayList<>();
        private final List<Skill> skills = new ArrayList<>();
        private boolean isDirty;
        // Instance of skill/magus supported entity, essentially.
        private Magi magi;

        public LivingDataCapabilityImp() {
            fillTraits();
            //TODO: Data generation methods for each skill
            fillCategories();
            fillSkills();
            markDirty();
        }

        public Magi getMagi(LivingEntity entity) {
            if (magi == null) {
                if (entity instanceof Player) {
                    Avatar.LOGGER.debug("Client Side: "  + entity.level().isClientSide);
                    Avatar.LOGGER.warn("Magi instance is null.");
//                    Thread.dumpStack();
                }
                fillTraits();
                magi = new Magi(entity);
            }
            // Right now we need a "load default".
            return this.magi;
        }

        @Override
        public CompoundTag serializeNBT() {
            CompoundTag tag;
//            Thread.dumpStack();
            if (magi != null && magi.isDirty()) {
                tag = magi.serialiseNBT();
            } else tag = new CompoundTag();
            traits.forEach(trait -> {
                if (trait.isDirty() || isDirty()) {
                    tag.put(trait.name(), trait.serializeNBT());
                }
            });

            return tag;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            markClean();
            traits.forEach(trait -> trait.deserializeNBT((CompoundTag) nbt.get(trait.name())));
            if (magi != null) {
                magi.deserialiseNBT(nbt);
                magi.setClean();
            }
        }

        public void fillTraits() {
            traits.addAll(Registries.DATA_TRAITS.get().getValues());
        }

        public List<DataTrait> getTraits() {
            return this.traits;
        }

        //When players move to versions with new techniques and such, we'll have to use these to accomodate.
        public void addTraits(List<DataTrait> dataTraits) {
            traits.addAll(dataTraits);
        }

        public void addTrait(DataTrait trait) {
            traits.add(trait);
        }

        @Nullable
        public DataTrait getTrait(String name) {
            for (DataTrait trait : getTraits())
                if (trait.name().equals(name))
                    return trait;

            return null;

        }

        public void fillCategories() {
            categories.addAll(Registries.SKILL_CATEGORIES.get().getValues());
        }

        public List<SkillCategory> getAllSkillCategories() {
            return this.categories;
        }

        public void fillSkills() {
            skills.addAll(Registries.SKILLS.get().getValues());
        }

        public List<Skill> getAllSkills() {
            return this.skills;
        }

        public SkillData getSkillData() {
            return null;
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
            //TODO: Add a check for all kinds of data, not just DataTraits
            return this.isDirty;
        }
    }

    public static class LivingDataProvider implements ICapabilityProvider, ICapabilitySerializable<CompoundTag> {

        private final LazyOptional<Data> instance = LazyOptional.of(LivingDataCapabilityImp::new);

        public static void init() {}

        @Override
        public CompoundTag serializeNBT() {
            return instance.orElseThrow(NullPointerException::new).serializeNBT();
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) {
            instance.orElseThrow(NullPointerException::new).deserializeNBT(nbt);
        }

        @Override @Nonnull
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
            return CapabilityHandler.LIVING_DATA.orEmpty(cap, instance.cast());
        }
    }
}
