package com.amuzil.omegasource.api.magus.skill;

import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.radix.RadixTree;
import com.amuzil.omegasource.api.magus.registry.Registries;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.event.SkillTickEvent;
import com.amuzil.omegasource.api.magus.skill.traits.SkillTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.UseTrait;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.network.AvatarNetwork;
import com.amuzil.omegasource.network.packets.skill.ActivatedSkillPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;


/**
 * Basic skill class. All other skills extend this.
 */
public abstract class Skill {
    private final String name;
    private final ResourceLocation id;
    private final SkillCategory category;
    private final List<RadixTree.ActivationType> activationTypes;
    private final List<SkillType> skillTypes;
    private final List<SkillTrait> skillTraits;
    // How the skill was activated. Useful if you want different methods to influence the skill in different ways.
    // For complex, game-design move combinations, see ModifierData for how to alter your skills.
    protected RadixTree.ActivationType activatedType;
    protected FormPath startPaths, runPaths, stopPaths;
    private boolean shouldStart, shouldRun, shouldStop;

    public Skill(String modId, String name, SkillCategory category) {
        this(ResourceLocation.fromNamespaceAndPath(modId, name), name, category);
    }

    public Skill(ResourceLocation id, String name, SkillCategory category) {
        this.id = id;
        this.name = name;
        this.category = category;
        // Menu is default
        this.activatedType = RadixTree.ActivationType.MENU;
        this.skillTypes = new LinkedList<>();
        this.skillTraits = new LinkedList<>();
        this.activationTypes = new LinkedList<>();

        // Maybe static instances of traits rather than new instances per Skill? Unsure
        addTrait(new UseTrait("use_skill", false));

        Registries.registerSkill(this);
    }

    public boolean addTrait(SkillTrait trait) {
        String name = trait.name();
        for (SkillTrait st : this.skillTraits) {
            if (st.name().equals(name))
                return false;
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

    public String name() {
        return name;
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

    public void tick(Bender bender, FormPath formPath) {
        if (bender.getSkillData(this).getSkillState().equals(SkillState.RUN)) {
            if (MinecraftForge.EVENT_BUS.post(new SkillTickEvent.Run(bender.getEntity(), formPath, this))) return;
            run(bender);
        }
    }

    // Execute Skill on server
    public void execute(Bender bender, FormPath formPath) {

        // Remember, for some reason post only returns true upon the event being cancelled. Blame Forge.
        SkillData skillData = bender.getSkillData(this);

        switch (skillData.getSkillState()) {
            case START -> {
                if (shouldStart(bender, formPath)) {
                    if (MinecraftForge.EVENT_BUS.post(new SkillTickEvent.Start(bender.getEntity(), formPath, this)))
                        return;
                    executeOnClient(bender.getEntity(), skillData, SkillState.START);
                    start(bender);
                }
            }

            case RUN -> {
                if (shouldRun(bender, formPath)) {
                    if (MinecraftForge.EVENT_BUS.post(new SkillTickEvent.Run(bender.getEntity(), formPath, this)))
                        return;
                    executeOnClient(bender.getEntity(), skillData, SkillState.RUN);
                    run(bender);
                }
            }

            case STOP -> {
                if (shouldStop(bender, formPath)) {
                    if (MinecraftForge.EVENT_BUS.post(new SkillTickEvent.Stop(bender.getEntity(), formPath, this)))
                        return;
                    executeOnClient(bender.getEntity(), skillData, SkillState.STOP);
                    stop(bender);
                }
            }
        }
    }

    // Execute Skill on client(s) and sync SkillState
    public void executeOnClient(LivingEntity entity, SkillData skillData, SkillState skillState) {
        // Handle which player clients should receive the packet based on Skill
        if (!entity.level().isClientSide()) {
            skillData.setSkillState(skillState); // Set SkillState on server
            ServerLevel level = (ServerLevel) entity.level();
            ActivatedSkillPacket packet = new ActivatedSkillPacket(id, skillState.ordinal());
            Predicate<ServerPlayer> predicate = (serverPlayer) -> entity.distanceToSqr(serverPlayer) < 2500;
            for (ServerPlayer nearbyPlayer : level.getPlayers(predicate.and(LivingEntity::isAlive))) {
                AvatarNetwork.sendToClient(packet, nearbyPlayer);
            }
        }
    }

    public abstract FormPath getStartPaths();

    public abstract FormPath getRunPaths();

    public abstract FormPath getStopPaths();

    public abstract boolean shouldStart(Bender bender, FormPath formPath);

    public abstract boolean shouldRun(Bender bender, FormPath formPath);

    public abstract boolean shouldStop(Bender bender, FormPath formPath);

    public abstract void start(Bender bender);

    public abstract void run(Bender bender);

    public abstract void stop(Bender bender);

    // Resets the skill and any necessary skill data; should be called upon stopping execution.
    public abstract void reset(LivingEntity entity);

    public enum SkillState {
        IDLE,
        START,
        RUN,
        STOP
    }

    /**
     * Different skill types. A skill can be multiple of one type.
     */
    public enum SkillType {
        OFFENSIVE, DEFENSIVE, MOBILITY, BUFF, UTILITY, RANGED, MELEE, CONSTRUCT
    }
}
