package com.amuzil.omegasource.api.magus.skill;

import com.amuzil.omegasource.api.magus.form.Form;
import com.amuzil.omegasource.api.magus.radix.RadixTree;
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
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Predicate;


/**
 * Basic skill class. All other skills extend this.
 */
public abstract class Skill {
    private final String name;
    private final ResourceLocation id;
    private final List<RadixTree.ActivationType> activationTypes;
    private final List<SkillType> skillTypes;
    private final List<SkillTrait> skillTraits;
    protected Consumer<SkillTickEvent> run;
    protected Bender bender;
    protected SkillData skillData;
    // How the skill was activated. Useful if you want different methods to influence the skill in different ways.
    protected RadixTree.ActivationType activatedType;
    protected List<Form> startPaths, runPaths, stopPaths;
    private String uuid;

    public Skill(String modId, String name) {
        this(ResourceLocation.fromNamespaceAndPath(modId, name), name);
    }

    public Skill(ResourceLocation id, String name) {
        this.id = id;
        this.uuid = UUID.randomUUID().toString();
        this.name = name;
        // Menu is default
        this.activatedType = RadixTree.ActivationType.MENU;
        this.skillTypes = new LinkedList<>();
        this.skillTraits = new LinkedList<>();
        this.activationTypes = new LinkedList<>();

        this.run = event -> {
            if (bender != null) {
                this.tick(bender);
            }
        };

        addTrait(new UseTrait("use_skill", false));
//        Registries.registerSkill(this);
    }

    public Skill create(Bender bender) {
        this.bender = bender;
        this.skillData = new SkillData(this);
        return this;
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

//    public abstract BendingSelection.Target targetType();  // todo: part of selection type refactor

    public void addType(SkillType type) {
        this.skillTypes.add(type);
    }

    public List<RadixTree.ActivationType> getActivationTypes() {
        return this.activationTypes;
    }

    public void addActivationType(RadixTree.ActivationType type) {
        this.activationTypes.add(type);
    }

    public abstract SkillCategory getCategory();

    public String name() {
        return name;
    }

    public ResourceLocation getId() {
        return id;
    }

    public String getUUID() {
        return uuid;
    }

    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    public List<SkillTrait> getTraits() {
        return this.skillTraits;
    }

    public List<SkillType> getTypes() {
        return this.skillTypes;
    }

    // Execute Skill on server
//    public void execute(Bender bender) {
//        FormPath formPath = bender.formPath;
//        // Remember, for some reason post only returns true upon the event being cancelled. Blame Forge.
//        SkillData skillData = bender.getSkillData(this);
//
//        switch (skillData.getSkillState()) {
//            case START -> {
//                if (shouldStart(bender, formPath)) {
//                    if (MinecraftForge.EVENT_BUS.post(new SkillExecutionEvent.Start(bender.getEntity(), formPath, this)))
//                        return;
//                    executeOnClient(bender.getEntity(), skillData, SkillState.START);
//                    start(bender);
//                }
//            }
//
//            case RUN -> {
//                if (shouldRun(bender, formPath)) {
//                    if (MinecraftForge.EVENT_BUS.post(new SkillExecutionEvent.Run(bender.getEntity(), formPath, this)))
//                        return;
//                    executeOnClient(bender.getEntity(), skillData, SkillState.RUN);
//                    run(bender);
//                }
//            }
//
//            case STOP -> {
//                if (shouldStop(bender, formPath)) {
//                    if (MinecraftForge.EVENT_BUS.post(new SkillExecutionEvent.Stop(bender.getEntity(), formPath, this)))
//                        return;
//                    executeOnClient(bender.getEntity(), skillData, SkillState.STOP);
//                    stop(bender);
//                }
//            }
//        }
//    }

    protected <T extends Event> void listen(Class<T> eventType, Consumer<T> runnable) {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, eventType, runnable);
    }

    protected void hush(Consumer<?> runnable) {
        MinecraftForge.EVENT_BUS.unregister(runnable);
    }

    public void tick(Bender bender) {
        if (shouldStop(bender, bender.formPath)) {
            stop(bender);
            hush(run);
        } else {
            run(bender);
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

    public abstract List<Form> startPaths();

    public abstract List<Form> runPaths();

    public abstract List<Form> stopPaths();

    public abstract boolean shouldRun(Bender bender, List<Form> formPath);

    public abstract boolean shouldStop(Bender bender, List<Form> formPath);

    public abstract void start(Bender bender);

    public abstract void run(Bender bender);

    public abstract void stop(Bender bender);

    // Resets the skill and any necessary skill data; should be called upon stopping execution.
    public abstract void reset(LivingEntity entity);

    public enum SkillState {
        IDLE,
        START,
        RUN,
        STOP,
    }

    /**
     * Different skill types. A skill can be multiple of one type.
     */
    public enum SkillType {
        OFFENSIVE, DEFENSIVE, MOBILITY, BUFF, UTILITY, RANGED, MELEE, CONSTRUCT
    }
}
