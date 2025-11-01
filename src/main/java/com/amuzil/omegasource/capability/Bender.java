package com.amuzil.omegasource.capability;

import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.form.Form;
import com.amuzil.omegasource.api.magus.registry.Registries;
import com.amuzil.omegasource.api.magus.skill.Skill;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.api.magus.skill.data.SkillCategoryData;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.traits.DataTrait;
import com.amuzil.omegasource.api.magus.tree.SkillTree;
import com.amuzil.omegasource.api.magus.tree.TreeResult;
import com.amuzil.omegasource.bending.BendingSelection;
import com.amuzil.omegasource.bending.element.Element;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.bending.form.BendingForm;
import com.amuzil.omegasource.events.FormActivatedEvent;
import com.amuzil.omegasource.network.AvatarNetwork;
import com.amuzil.omegasource.network.packets.sync.SyncBenderPacket;
import com.amuzil.omegasource.network.packets.sync.SyncMovementPacket;
import com.amuzil.omegasource.network.packets.sync.SyncSelectionPacket;
import com.amuzil.omegasource.utils.bending.OriginalBlocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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
    public final List<Form> formPath = new ArrayList<>(); // in-sync
    private Vec3 lastDeltaMovement = Vec3.ZERO; // in-sync
    private BendingSelection selection = new BendingSelection(); // in-sync

    public final HashMap<String, Skill> activeSkills = new HashMap<>();
    private final List<Skill> availableSkills = new ArrayList<>();
    private Skill skillToActivate = null;
    private final int SKILL_ACTIVATION_THRESHOLD = 10;
    private int skillActivationTimer = SKILL_ACTIVATION_THRESHOLD;

    // Persistent data
    private Element activeElement = Elements.FIRE; // Currently active element // TODO - Randomize on first load
    private BendingForm.Type.Motion stepDirection;
    private final List<SkillCategoryData> skillCategoryData = new ArrayList<>();
    private final HashMap<String, SkillData> skillDataMap = new HashMap<>();
    private final List<DataTrait> dataTraits = new ArrayList<>();

    public Bender(LivingEntity entity) {
        this.entity = entity;
        this.formListener = this::onFormActivatedEvent;

        for (SkillCategory category: Registries.getSkillCategories())
            skillCategoryData.add(new SkillCategoryData(category));
        this.availableSkills.addAll(Registries.getSkills());
        for (Skill skill: availableSkills)
            skillDataMap.put(skill.name(), new SkillData(skill));
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
                && getElement() == skill.getCategory();
    }

    private void onFormActivatedEvent(FormActivatedEvent event) {
        if (event.getEntity().getId() == entity.getId() && event.getActiveForm().form().notNull()) {
            active = !event.released();
            ActiveForm activeForm = event.getActiveForm();

            if (activeForm.direction() != null)
                stepDirection = activeForm.direction(); // TODO: Move to bending context
            else
                stepDirection = BendingForm.Type.Motion.NONE;

            formPath.add(event.getActiveForm().form());
            if (active) {
                switch (getSelection().target()) {
                    case BLOCK, NONE, SELF, ENTITY -> checkSkillTree();
                    case SKILL ->
                            applyFormsToSkill(getSelection().skillIds(), formPath); // send the form to the skill.
                }
            } else {
                if (getSelection().target().equals(BendingSelection.Target.SKILL)) {
                    releaseFormsOnSkill(getSelection().skillIds(), formPath);
                }
            }

            tick = timeout;
//            printFormPath(); // Debugging purposes
        }
    }

    private void releaseFormsOnSkill(List<String> strings, List<Form> formPath) {
        // method stub for now
    }

    private void applyFormsToSkill(List<String> skillIds, List<Form> formPath) {
        this.activeSkills.values().stream().filter(c -> skillIds.contains(c.getUUID().toString())).forEach((skill) -> {
            // skill.ApplyForms(formPath);
        });
    }

    private void checkSkillTree() {
        TreeResult result = SkillTree.ExecutePath(this, formPath);
        switch(result.resultType) {
            case SKILL_FOUND_TERMINAL -> {
//                LOGGER.info("Skill found: " + result.skill.name());
                skillToActivate = result.skill;
                startSkill();
            }
            case SKILL_FOUND -> {
//                LOGGER.info("Skill found: " + result.skill.name());
                skillToActivate = result.skill;
                skillActivationTimer = SKILL_ACTIVATION_THRESHOLD;
            }
            case TERMINAL_NODE -> {
//                LOGGER.info("Terminal node");
                formPath.clear();
            } // tree reached the end and found nothing. clear the formPath
            case SKILL_NOT_FOUND -> {
//                LOGGER.info("Skill not found");
            } // do nothing. there may still be a combo on the next form.
        }
    }

    private void serverTick() {
        if(skillToActivate != null) {
            skillActivationTimer--;
            if(skillActivationTimer <= 0) {
                startSkill();
            }
        }
        if (!active) {
            if (tick == 0) {
                formPath.clear(); // Only clear when no Forms are active
                tick = timeout;
                active = true;
            }
            tick--;
        }
        restoreOriginalBlocks();
    }

    private void startSkill() {
        Skill newSkill = Objects.requireNonNull(Registries.getSkill(skillToActivate.getId())).create(this);
        if (canUseSkill(newSkill)) {
            newSkill.start(this);
//            formPath.clear(); // Please don't clear formPath until `applyFormsToSkill()` is implemented
            skillToActivate = null;
            skillActivationTimer = SKILL_ACTIVATION_THRESHOLD;
        }
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

    public BendingForm.Type.Motion getStepDirection() {
        return stepDirection;
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
    public List<Form> getFormPath() {
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
        return skillDataMap.get(skill.name());
    }

    @Override
    public SkillData getSkillData(String name) {
        return skillDataMap.get(name);
    }

    @Override
    public void resetSkillData() {
        skillDataMap.clear();
        for (Skill skill: availableSkills)
            skillDataMap.put(skill.name(), new SkillData(skill));
        markDirty();
    }

    @Override
    public void resetSkillData(Skill skill) {
        skillDataMap.put(skill.name(), new SkillData(skill));
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
    public void setCanUseSkill(boolean canUse, String name) {
        SkillData skillData = getSkillData(name);
        if (skillData != null) {
            skillData.setCanUse(canUse);
            markDirty();
        } else {
            LOGGER.warn("Skill data not found for ID: {}", name);
        }
    }

    @Override
    public void setCanUseAllElements() {
        skillCategoryData.forEach(element -> element.setCanUse(true));
        markDirty();
    }

    @Override
    public void setCanUseAllSkills() {
        skillDataMap.values().forEach(skill -> skill.setCanUse(true));
        markDirty();
    }

    @Override
    public void setCanUseAllSkills(Element element) {
        setCanUseElement(true, element);
        skillDataMap.values().forEach(skillData -> {
            if (skillData.getSkill().getCategory().getId().equals(element.getId()))
                skillData.setCanUse(true);
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

    private void printFormPath() {
        LOGGER.info("Forms: {}", formPath);
    }

    public void printNBT() {
        CompoundTag tag = this.serializeNBT();
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
                """
                \nData Version: %d
                Active Element: %s
                """, tag.getInt("DataVersion"), tag.getString("Active Element")));
        skillCategoryData.forEach(catData -> sb.append(tag.get(catData.name())).append("\n"));
        skillDataMap.values().forEach(skillData -> sb.append(tag.get(skillData.name())).append("\n"));
        LOGGER.info(sb.toString());
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("DataVersion", DATA_VERSION);
        tag.putString("Active Element", Objects.requireNonNullElse(activeElement, Elements.FIRE).getId().toString());
        skillCategoryData.forEach(catData -> tag.put(catData.name(), catData.serializeNBT()));
        skillDataMap.values().forEach(skillData -> tag.put(skillData.name(), skillData.serializeNBT()));
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

                for (SkillData skillData : skillDataMap.values()) {
                    if (tag.contains(skillData.name(), Tag.TAG_COMPOUND)) {
                        skillData.deserializeNBT(tag.getCompound(skillData.name()));
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

    public static @Nullable IBender getBender(LivingEntity entity) {
        if (entity == null)
            return null;
        return entity.getCapability(AvatarCapabilities.BENDER)
                .orElse(null);
    }

}
