package com.amuzil.omegasource.api.magus.skill;

import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.radix.RadixTree;
import com.amuzil.omegasource.api.magus.skill.event.SkillTickEvent;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.traits.SkillTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.UseTrait;
import com.amuzil.omegasource.api.magus.registry.Registries;
import com.amuzil.omegasource.capability.Bender;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;

import java.util.LinkedList;
import java.util.List;


/**
 * Basic skill class. All other skills extend this.
 */
public abstract class Skill {
    private final ResourceLocation id;
    private final SkillCategory category;
    private final List<RadixTree.ActivationType> activationTypes;
    private final List<SkillType> skillTypes;
    private final List<SkillTrait> skillTraits;
    // How the skill was activated. Useful if you want different methods to influence the skill in different ways.
    // For complex, game-design move combinations, see ModifierData for how to alter your skills.
    protected RadixTree.ActivationType activatedType;
    private boolean shouldStart, shouldRun, shouldStop;
    protected FormPath startPaths, runPaths, stopPaths;

    public Skill(String modId, String name, SkillCategory category) {
        this(ResourceLocation.fromNamespaceAndPath(modId, name), category);
    }

    public Skill(ResourceLocation id, SkillCategory category) {
        this.id = id;
        this.category = category;
        // Menu is default
        this.activatedType = RadixTree.ActivationType.MENU;
        this.skillTypes = new LinkedList<>();
        this.skillTraits = new LinkedList<>();
        this.activationTypes = new LinkedList<>();

        // Maybe static instances of traits rather then new instances per Skill? Unsure
        addTrait(new UseTrait("use_skill", false));

        Registries.registerSkill(this);
    }

    public boolean addTrait(SkillTrait trait) {
        String name = trait.name();
        for (SkillTrait st : this.skillTraits) {
            if (st.name().equals(name)) {
                return false;
            }
        }
        this.skillTraits.add(trait);
        return true;
    }

    public void addType(SkillType type) {
        this.skillTypes.add(type);
    }

    public List<RadixTree.ActivationType> getActivationTypes() {
        return this.activationTypes;
    }

    public void addActivationType(RadixTree.ActivationType type) {
        this.activationTypes.add(type);
    }

    public SkillCategory getCategory() {
        return category;
    }

    public ResourceLocation getId() {
        return id;
    }

    public List<SkillTrait> getTraits() {
        return this.skillTraits;
    }

    public List<SkillType> getTypes() {
        return this.skillTypes;
    }

    // Fix this because this will not work lmao
    public void tick(LivingEntity entity, FormPath formPath) {
        //Run this asynchronously

        Bender bender = (Bender) Bender.getBender(entity);
        // Remember, for some reason post only returns true upon the event being cancelled. Blame Forge.
        if (shouldStart(entity, formPath)) {
            if (MinecraftForge.EVENT_BUS.post(new SkillTickEvent.Start(entity, formPath, this))) return;

            if (bender != null) {
                SkillData skillData = bender.getSkillData(this);
                skillData.setState(SkillState.START);
            }
            start(entity);
        } else return;

        if (shouldRun(entity, formPath)) {
            if (MinecraftForge.EVENT_BUS.post(new SkillTickEvent.Run(entity, formPath, this))) return;

            if (bender != null) {
                SkillData skillData = bender.getSkillData(this);
                if (!skillData.getState().equals(SkillState.RUN))
                    skillData.setState(SkillState.RUN);
            }
            run(entity);

        }

        if (shouldStop(entity, formPath)) {
            if (MinecraftForge.EVENT_BUS.post(new SkillTickEvent.Stop(entity, formPath, this))) return;

            if (bender != null) {
                SkillData skillData = bender.getSkillData(this);
                skillData.setState(SkillState.STOP);
            }
            stop(entity);
        }
    }


    public abstract FormPath getStartPaths();

    public abstract FormPath getRunPaths();

    public abstract FormPath getStopPaths();

    public abstract boolean shouldStart(LivingEntity entity, FormPath formPath);

    public abstract boolean shouldRun(LivingEntity entity, FormPath formPath);

    public abstract boolean shouldStop(LivingEntity entity, FormPath formPath);

    public abstract void start(LivingEntity entity);

    public abstract void run(LivingEntity entity);

    public void stop(LivingEntity entity) {
        Bender bender = (Bender) Bender.getBender(entity);
        if (bender != null) {
            SkillData data = bender.getSkillData(this);
            data.setState(SkillState.IDLE);
        }
    };

    // Resets the skill and any necessary skill data; should be called upon stopping execution.
    public abstract void reset(LivingEntity entity, FormPath formPath);

    public enum SkillState {
        IDLE,
        START,
        RUN,
        STOP;

        public @Override String toString() {
            return this.name();
        }
    }

    /**
     * Different skill types. A skill can be multiple of one type.
     */
    public enum SkillType {
        OFFENSIVE, DEFENSIVE, MOBILITY, BUFF, UTILITY, RANGED, MELEE, CONSTRUCT
    }
}
