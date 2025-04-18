package com.amuzil.omegasource.api.magus.skill.utils.data;

import com.amuzil.omegasource.api.magus.radix.RadixTree;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.api.magus.skill.utils.traits.DataTrait;
import com.amuzil.omegasource.api.magus.skill.utils.traits.SkillTrait;
import com.amuzil.omegasource.registry.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;


//TODO: Make this an implementation rather than a class.
//E.g SizeTrait vs ElementTrait or something are both SkillTraits but....
public class SkillData implements DataTrait {

    List<SkillTrait> skillTraits;
    // Types should not need serialisation as they do not change
    //The reason we're using a resource location and not the actual Skill object is because
    //it's much easier to serialise a String and then get a skill from it.
    ResourceLocation skillId;
    private boolean canUse;
    private boolean isDirty = false;
    private Skill.SkillState state;

    public SkillData(ResourceLocation skillId) {
        this.skillId = skillId;
        this.skillTraits = new LinkedList<>();
        this.canUse = false;

        this.state = Skill.SkillState.START;
        if (getSkill() != null)
            skillTraits = getSkill().getTraits();
    }

    public SkillData(Skill skill) {
        this(skill.getId());
    }

    public void setCanUse(boolean canUse) {
        this.canUse = canUse;
        markDirty();
    }

    public Skill.SkillState getState() {
        return this.state;
    }

    public void setState(Skill.SkillState state) {
        this.state = state;
        markDirty();
    }

    public boolean canUse() {
        return this.canUse;
    }

    public List<RadixTree.ActivationType> getActivationTypes() {
        return this.getSkill().getActivationTypes();
    }


    public List<Skill.SkillType> getSkillTypes() {
        return this.getSkill().getTypes();
    }

    @Override
    public String getName() {
        return getSkillId() + "_skillData";
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
        for (SkillTrait trait : skillTraits)
            if (trait.isDirty()) {
                markDirty();
                return true;
            }

        return this.isDirty;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("Skill ID", skillId.toString());
        tag.putString("Skill State", state.name());
        skillTraits.forEach(skillTrait -> {
            if (skillTrait.isDirty())
                tag.put(skillTrait.getName() + "Trait", skillTrait.serializeNBT());
        });
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        try {
            skillId = ResourceLocation.tryParse(nbt.getString("Skill ID"));
            state = Skill.SkillState.valueOf(nbt.getString("Skill State"));
            if (!skillTraits.isEmpty())
                skillTraits.forEach(skillTrait -> {
                    if (skillTrait.isDirty())
                        skillTrait.deserializeNBT((CompoundTag) Objects.requireNonNull(nbt.get(skillTrait.getName() + "Trait")));
                    skillTrait.markClean();
                });
        } catch (NullPointerException e) {
            RadixTree.getLogger().error("Something has gone seriously wrong:" + "A skill trait hasn't been carried over from the registry.");
            e.printStackTrace();
        }
        markClean();
    }


    public List<SkillTrait> getSkillTraits() {
        if (skillTraits == null || skillTraits.isEmpty())
            skillTraits = getSkill().getTraits();
        return skillTraits;
    }

    public ResourceLocation getSkillId() {
        return skillId;
    }

    public Skill getSkill() {
        return Registries.SKILLS.get().getValue(getSkillId());
    }

    public List<SkillTrait> getFilteredTraits(Predicate<? super SkillTrait> filter) {
        return getSkillTraits().stream().filter(filter).collect(Collectors.toList());
    }

    @Nullable
    public SkillTrait getTrait(String name) {
        for (SkillTrait trait : getSkillTraits())
            if (trait.getName().equals(name)) return trait;

        return null;
    }

    @Nullable
    public <T extends SkillTrait> T getTrait(String name, Class<T> type) {
        for (SkillTrait trait : getSkillTraits()) {
            if (trait.getName().equals(name) && type.isInstance(trait)) {
                return type.cast(trait);
            }
        }
        return null;
    }


    public void reset() {
        for (SkillTrait trait : getSkillTraits())
            trait.reset();
    }
}
