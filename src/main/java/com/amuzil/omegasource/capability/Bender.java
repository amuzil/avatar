package com.amuzil.omegasource.capability;

import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.registry.Registries;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.api.magus.skill.data.SkillCategoryData;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.traits.DataTrait;
import com.amuzil.omegasource.bending.BendingSelection;
import com.amuzil.omegasource.bending.element.Element;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.events.FormActivatedEvent;
import com.amuzil.omegasource.network.AvatarNetwork;
import com.amuzil.omegasource.network.packets.skill.SkillDataPacket;
import com.amuzil.omegasource.network.packets.sync.SyncBenderPacket;
import com.amuzil.omegasource.network.packets.sync.SyncFormPathPacket;
import com.amuzil.omegasource.network.packets.sync.SyncMovementPacket;
import com.amuzil.omegasource.network.packets.sync.SyncSelectionPacket;
import com.amuzil.omegasource.utils.ship.OriginalBlocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;


public class Bender implements IBender {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final int DATA_VERSION = 1; // Update this as your data structure changes

    // Non-Persistent data
    private final LivingEntity entity;
    private final Consumer<FormActivatedEvent> formListener;
    private boolean isDirty = true; // Flag to indicate if data was changed
    private boolean active;
    private final int timeout = 5; // Adjust clear BendingForm cache timeout here
    private int tick = timeout;
    public final FormPath formPath = new FormPath(); // in-sync
    private Vec3 lastDeltaMovement = Vec3.ZERO; // in-sync
    private BendingSelection selection = new BendingSelection(); // in-sync
    private final HashMap<ResourceLocation, Boolean> shouldRuns = new HashMap<>(); // in-sync

    // Persistent data
    private Element activeElement = Elements.FIRE; // Currently active element // TODO - Randomize on first load
    private final List<SkillCategoryData> skillCategoryData = new ArrayList<>();
    private final List<SkillData> skillData = new ArrayList<>();
    private final List<DataTrait> dataTraits = new ArrayList<>();

    public final HashMap<String, Skill> activeSkills = new HashMap<>();
    private final List<Skill> availableSkills = new ArrayList<>();

    public Bender(LivingEntity entity) {
        this.entity = entity;
        this.formListener = this::onFormActivatedEvent;

        for (SkillCategory category : Registries.getSkillCategories())
            skillCategoryData.add(new SkillCategoryData(category));
        this.availableSkills.addAll(Registries.getSkills());
        for (Skill skill : availableSkills) {
            skillData.add(new SkillData(skill));
            if (skill.runPaths() != null)
                shouldRuns.put(skill.getId(), false); // Initialize shouldRuns map
        }
        dataTraits.addAll(Registries.getTraits());

        // Allow use of all Elements & Skills for testing!
        setAvatar(); // Uncomment this to grant all elements & skills

        markDirty();
    }

    @Override
    public String toString() {
        String elementName = "";
        if (activeElement != null)
            elementName = activeElement.name();
        return String.format("Bender[ %s | activeElement=%s ]", entity.getName() , elementName);
    }

    public void tick() {
        if (entity instanceof Player) {
            if (!entity.level().isClientSide())
                serverTick();
        }
    }

    private boolean canUseSkill(Skill skill) {
        return getSkillCategoryData(skill.getCategory().getId()).canUse()
//                && getSkillData(skill).canUse()
                && getElement() == skill.getCategory(); // Make SkillCategory part of Skill RadixTree
    }

    private void onFormActivatedEvent(FormActivatedEvent event) {
        if (event.getEntity().getId() == entity.getId()) {
            active = !event.released();
            formPath.update(event.getActiveForm());
//            this.syncFormPathToClient();
            for (Skill skill: activeSkills.values()) {
                if (skill.shouldStop(this, formPath)) {
                    skill.stop(this);
                } else if (skill.shouldRun(this, formPath)) {
                    skill.run(this);
                }
            }

            for (Skill skill: availableSkills) {
                if (canUseSkill(skill) && skill.shouldStart(this, formPath)) {
                    Skill newSkill = Registries.getSkillByName(skill.getId());
                    skillData.add(new SkillData(newSkill));
//                    AvatarNetwork.sendToClient(new SkillDataPacket(newSkill.getId(), newSkill.getSkillUuid(), 0), (ServerPlayer) entity);
                    newSkill.start(this);
                    formPath.clear();
                }
            }
            tick = timeout;
//            LOGGER.info("Simple Forms: {}", formPath.simple());
//            LOGGER.info("Complex Forms: {}", formPath.complex());
        }
    }

    private void serverTick() {
        if (!active) {
            if (tick == 0) {
                if (!formPath.isActive()) {
                    formPath.clear(); // Only clear when no Forms are active
//                    LOGGER.info("Complex Forms Timed Out");
                }
                tick = timeout;
                active = true;
            }
            tick--;
        }

        for (Skill skill: activeSkills.values()) {
            if (canUseSkill(skill)) {
                skill.execute(this, formPath);
            }
        }

        restoreOriginalBlocks();
    }

    public void startTickingOriginalBlocks(Long shipId) {
        OriginalBlocks originalBlocks = selection.originalBlocksMap().get(shipId);
        if (originalBlocks != null)
            originalBlocks.startTicking(true);
    }

    private void restoreOriginalBlocks() {
        Iterator<Map.Entry<Long, OriginalBlocks>> iterator = selection.originalBlocksMap().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Long, OriginalBlocks> entry = iterator.next();
            OriginalBlocks originalBlocks = entry.getValue();
            if (originalBlocks.startedTicking() && entity.level() instanceof ServerLevel level) {
                if (originalBlocks.incrementAndGetTickCount() > 400) {
                    originalBlocks.restore(level);
                    iterator.remove();
                }
            }
        }
    }

    @Override
    public Vec3 getDeltaMovement() {
        return lastDeltaMovement;
    }

    @Override
    public void setDeltaMovement(Vec3 lastDeltaMovement) {
        this.lastDeltaMovement = lastDeltaMovement;
    }

    @Override
    public void register() {
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, formListener);
    }

    @Override
    public void unregister() {
        MinecraftForge.EVENT_BUS.unregister(formListener);
    }

    @Override
    public LivingEntity getEntity() {
        return entity;
    }

    @Override
    public FormPath getFormPath() {
        return formPath;
    }

    @Override
    public SkillCategoryData getSkillCategoryData(Element element) {
        return getSkillCategoryData(element.getId());
    }

    @Override
    public SkillCategoryData getSkillCategoryData(ResourceLocation id) {
        return skillCategoryData.stream()
                .filter(data -> data.getSkillCategory().getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public SkillData getSkillData(Skill skill) {
        return getSkillData(skill.getSkillUuid());
    }

    @Override
    public SkillData getSkillData(String skillUuid) {
        return skillData.stream()
                .filter(data -> data.getSkillUuid().equals(skillUuid))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void resetSkillData() {
        skillData.clear();
        for (Skill skill : availableSkills)
            skillData.add(new SkillData(skill));
        markDirty();
    }

    @Override
    public void resetSkillData(Skill skill) {
        List<SkillData> newSkillData = new ArrayList<>();
        for (SkillData data : skillData) {
            if (data.getSkillUuid().equals(skill.getSkillUuid()))
                newSkillData.add(new SkillData(skill));
            else
                newSkillData.add(data);
        }
        skillData.clear();
        skillData.addAll(newSkillData);
        markDirty();
    }

    @Override
    public void setAvatar() {
        setCanUseAllElements();
        setCanUseAllSkills();
    }

    @Override
    public Element getElement() {
        return activeElement;
    }

    @Override
    public void setElement(Element activeElement) {
        this.activeElement = activeElement;
        markDirty();
    }

    @Override
    public void setCanUseElement(boolean canUse, Element element) {
        SkillCategoryData catData = getSkillCategoryData(element);
        if (catData != null) {
            catData.setCanUse(canUse);
            markDirty();
        } else {
            LOGGER.warn("Skill category data not found for element: {}", element.name());
        }
    }

    @Override
    public void setCanUseSkill(boolean canUse, String skillId) {
        SkillData skillData = getSkillData(skillId);
        if (skillData != null) {
            skillData.setCanUse(canUse);
            markDirty();
        } else {
            LOGGER.warn("Skill data not found for ID: {}", skillId);
        }
    }

    @Override
    public void setCanUseAllElements() {
        skillCategoryData.forEach(element -> element.setCanUse(true));
        markDirty();
    }

    @Override
    public void setCanUseAllSkills() {
        skillData.forEach(skill -> skill.setCanUse(true));
        markDirty();
    }

    @Override
    public void setCanUseAllSkills(Element element) {
        setCanUseElement(true, element);
        skillData.forEach(skill -> {
            if (skill.getSkill().getCategory().getId().equals(element.getId()))
                skill.setCanUse(true);
        });
        markDirty();
    }

    @Override
    public void setSelection(BendingSelection selection) {
        this.selection = selection;
        markDirty();
    }

    @Override
    public BendingSelection getSelection() {
        return this.selection;
    }

    @Override
    public void reset() {
        this.formPath.clear();
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
        return this.isDirty;
    }

    @Override
    public void syncDeltaMovementToServer() {
        if (entity.level().isClientSide())
            AvatarNetwork.sendToServer(new SyncMovementPacket(lastDeltaMovement, entity.getUUID()));
    }

    @Override
    public void syncSelectionToServer() {
        if (entity.level().isClientSide())
            AvatarNetwork.sendToServer(new SyncSelectionPacket(selection.serializeNBT(), entity.getUUID()));
    }

    @Override
    public void syncFormPathToClient() {
        if (!entity.level().isClientSide())
            if (entity instanceof ServerPlayer player)
                AvatarNetwork.sendToClient(new SyncFormPathPacket(formPath.serializeNBT()), player);
    }

    @Override
    public void syncToClient() {
        if (!entity.level().isClientSide())
            if (entity instanceof ServerPlayer player)
                AvatarNetwork.sendToClient(new SyncBenderPacket(this.serializeNBT(), player.getUUID()), player);
    }

    // TODO - Create generic method to check & return if data in NBT tag exists & warn if it doesn't
//    private <T> T validate(CompoundTag tag, String key, byte type) {
//        if (tag.contains(key, type)) {
//            return (T) tag.get(key);
//        } else {
//            LOGGER.warn("Missing data for: {}", key);
//            return null;
//        }
//        return list != null && !list.isEmpty();
//    }

    public void printNBT() {
        CompoundTag tag = this.serializeNBT();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                """
                \nData Version: %d
                Active Element: %s
                """, tag.getInt("DataVersion"), tag.getString("Active Element")));
        skillCategoryData.forEach(catData -> sb.append(tag.get(catData.name())).append("\n"));
        skillData.forEach(sData -> sb.append(tag.get(sData.name())).append("\n"));
        LOGGER.info(sb.toString());
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("DataVersion", DATA_VERSION);
        tag.putString("Active Element", Objects.requireNonNullElse(activeElement, Elements.FIRE).getId().toString());
        skillCategoryData.forEach(catData -> tag.put(catData.name(), catData.serializeNBT()));
        skillData.forEach(sData -> tag.put(sData.name(), sData.serializeNBT()));
        return tag;
    }

    /** NOTE: If you change the data structure in ANY way and want to overwrite the old data with defaults,
     comment out the deserializing of whatever data changed. Then load the game and save and quit to overwrite.
     ---
     Now, if you want to migrate the data, bump the DATA_VERSION and add a new case in the switch statement.
     After that, modify the previous case to handle the migration by loading up the old data to fit into the new.
     */
    @Override
    public void deserializeNBT(CompoundTag tag) {
//        System.out.println("[Bender] Deserializing NBT: " + tag);
        int version = tag.contains("DataVersion") ? tag.getInt("DataVersion") : 0; // Default to version 0 if not present
        switch (version) {
            case 1 -> {
                LOGGER.info("Loading Bender data version: {}", version);
                this.activeElement = Elements.get(ResourceLocation.parse(tag.getString("Active Element")));
                for (SkillCategoryData catData : skillCategoryData) {
                    if (tag.contains(catData.name(), Tag.TAG_COMPOUND)) {
                        catData.deserializeNBT(tag.getCompound(catData.name()));
                    } else {
                        LOGGER.warn("Missing skill category data for: {}", catData.name());
                    }
                }

                for (SkillData sData : skillData) {
                    if (tag.contains(sData.name(), Tag.TAG_COMPOUND)) {
                        sData.deserializeNBT(tag.getCompound(sData.name()));
                    } else {
                        LOGGER.warn("Missing skill data for: {}", sData.name());
                    }
                }
            } default -> { // Handle unknown versions for migrating data
                // Set defaults here for new player / world
                LOGGER.info("Unknown Bender data version: {}", version);
            }
        }
    }

    public static @Nullable IBender getBender(LivingEntity entity) {
        if (entity == null)
            return null;
        return entity.getCapability(AvatarCapabilities.BENDER)
                .orElse(null);
    }
}
