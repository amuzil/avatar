package com.amuzil.av3.data.capability;

import com.amuzil.av3.bending.BendingSelection;
import com.amuzil.av3.bending.element.Element;
import com.amuzil.av3.bending.form.BendingForm;
import com.amuzil.av3.data.attachment.BenderData;
import com.amuzil.av3.network.AvatarNetwork;
import com.amuzil.av3.network.packets.sync.SyncMovementPacket;
import com.amuzil.av3.network.packets.sync.SyncSelectionPacket;
import com.amuzil.magus.form.ActiveForm;
import com.amuzil.magus.form.Form;
import com.amuzil.magus.form.FormActivatedEvent;
import com.amuzil.magus.registry.Registries;
import com.amuzil.magus.skill.Skill;
import com.amuzil.magus.skill.data.SkillCategoryData;
import com.amuzil.magus.skill.data.SkillData;
import com.amuzil.magus.tree.SkillTree;
import com.amuzil.magus.tree.TreeResult;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.common.NeoForge;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.amuzil.av3.data.attachment.AvatarAttachments.ACTIVE_ELEMENT;
import static com.amuzil.av3.data.attachment.AvatarAttachments.BENDER_DATA;


public class Bender implements IBender {

    private static final Logger LOGGER = LogManager.getLogger();

    LivingEntity entity;
    private BenderData benderData;
    private final Consumer<FormActivatedEvent> formListener;

    private boolean isDirty = true; // Indicates if data was changed
    private boolean active = false; // Indicates if a BendingForm is currently active
    private final int timeout = 5; // Adjust clear BendingForm cache timeout here
    private int tick = timeout;
    private Vec3 lastDeltaMovement = Vec3.ZERO; // in-sync
    private BendingForm.Type.Motion stepDirection; // in-sync
    private BendingSelection selection = new BendingSelection(); // in-sync
    public final List<Form> formPath = new ArrayList<>();
    public final HashMap<String, Skill> activeSkills = new HashMap<>();
    private Skill skillToActivate = null;
    private final int SKILL_ACTIVATION_THRESHOLD = 10;
    private int skillActivationTimer = SKILL_ACTIVATION_THRESHOLD;

    public Bender(LivingEntity entity) {
        this.entity = entity;
        this.benderData = entity.getData(BENDER_DATA);
        this.formListener = this::onFormActivatedEvent;

        // Allow use of all Elements & Skills for testing!
//        setAvatar(); // Grant all elements & skills

        markDirty();
    }

    @Override
    public String toString() {
        return String.format("Bender[ %s | activeElement=%s ]",
                entity.getName().getString() , getElement().name());
    }

    public void tick() {
        serverTick();
    }

    private boolean canUseSkill(Skill skill) {
        return getSkillCategoryData(skill.getCategory().getId()).canUse()
//                && getSkillData(skill).canUse()
                && getElement() == skill.getCategory();
    }

    private void onFormActivatedEvent(FormActivatedEvent event) {
        if (event.getEntity().getUUID() == entity.getUUID() && event.getActiveForm().form().notNull()) {
            active = !event.released();
            ActiveForm activeForm = event.getActiveForm();
            if (activeForm.direction() != null)
                stepDirection = activeForm.direction(); // TODO: Move to bending context
            else
                stepDirection = BendingForm.Type.Motion.NONE;

            formPath.add(event.getActiveForm().form());
//            printFormPath(); // Debugging purposes
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
        if (skillToActivate != null) {
            skillActivationTimer--;
            if (skillActivationTimer <= 0) {
                startSkill();
            }
        }
        if (!active) {
            if (tick == 0) {
                formPath.clear(); // Only clear when Forms are inactive for `timeout` # of ticks
                tick = timeout;
                active = true;
            }
            tick--;
        }
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

    // Used for when we have momentum when double jump is executed
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
        NeoForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, formListener);
    }

    @Override
    public void unregister() {
        NeoForge.EVENT_BUS.unregister(formListener);
    }

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
        return benderData.skillCategoryData.stream()
                .filter(data -> data.getSkillCategory().getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public SkillData getSkillData(Skill skill) {
        return benderData.skillDataMap.get(skill.name());
    }

    @Override
    public SkillData getSkillData(String name) {
        return benderData.skillDataMap.get(name);
    }

    @Override
    public void resetSkillData() {
        benderData.skillDataMap.clear();
        for (Skill skill: Registries.getSkills())
            benderData.skillDataMap.put(skill.name(), new SkillData(skill));
        entity.setData(BENDER_DATA, benderData);
        markDirty();
    }

    @Override
    public void resetSkillData(Skill skill) {
        benderData.skillDataMap.put(skill.name(), new SkillData(skill));
        entity.setData(BENDER_DATA, benderData);
        markDirty();
    }

    @Override
    public void setAvatar() {
        setCanUseAllElements();
        setCanUseAllSkills();
    }

    @Override
    public Element getElement() {
        return entity.getData(ACTIVE_ELEMENT);
    }

    @Override
    public void setElement(Element activeElement) {
        entity.setData(ACTIVE_ELEMENT, activeElement);
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
        benderData.skillCategoryData.forEach(element -> element.setCanUse(true));
        entity.setData(BENDER_DATA, benderData);
        markDirty();
    }

    @Override
    public void setCanUseAllSkills() {
        benderData.skillDataMap.values().forEach(skill -> skill.setCanUse(true));
        entity.setData(BENDER_DATA, benderData);
        markDirty();
    }

    @Override
    public void setCanUseAllSkills(Element element) {
        setCanUseElement(true, element);
        benderData.skillDataMap.values().forEach(skillData -> {
            if (skillData.getSkill().getCategory().getId().equals(element.getId()))
                skillData.setCanUse(true);
        });
        entity.setData(BENDER_DATA, benderData);
        markDirty();
    }

    @Override
    public BenderData getBenderData() {
        return benderData;
    }

    @Override
    public void setBenderData(BenderData data) {
        this.benderData = data;
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
            AvatarNetwork.sendToServer(new SyncSelectionPacket(selection.serializeNBT(entity.registryAccess()), entity.getUUID()));
    }

    @Override
    public void syncToClient() {
        if (!entity.level().isClientSide()) {
            entity.syncData(BENDER_DATA);
            entity.syncData(ACTIVE_ELEMENT);
            markClean();
        }
    }

    private void printFormPath() {
        LOGGER.info("Forms: {}", formPath);
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
//        tag.putInt("DataVersion", DATA_VERSION);
//        tag.putString("Active Element", Objects.requireNonNullElse(activeElement, Elements.FIRE).getId().toString());
//        skillCategoryData.forEach(catData -> tag.put(catData.name(), catData.serializeNBT(provider)));
//        skillDataMap.values().forEach(skillData -> tag.put(skillData.name(), skillData.serializeNBT(provider)));
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
//        System.out.println("[Bender] Deserializing NBT: " + tag);
//        int version = tag.contains("DataVersion") ? tag.getInt("DataVersion") : 0; // Default to version 0 if not present
//        switch (version) {
//            case 1 -> {
//                LOGGER.info("Loading Bender data version: {}", version);
//                this.activeElement = Elements.get(ResourceLocation.parse(tag.getString("Active Element")));
//                for (SkillCategoryData catData : skillCategoryData) {
//                    if (tag.contains(catData.name(), Tag.TAG_COMPOUND)) {
//                        catData.deserializeNBT(provider, tag.getCompound(catData.name()));
//                    } else {
//                        LOGGER.warn("Missing skill category data for: {}", catData.name());
//                    }
//                }
//
//                for (SkillData skillData : skillDataMap.values()) {
//                    if (tag.contains(skillData.name(), Tag.TAG_COMPOUND)) {
//                        skillData.deserializeNBT(provider, tag.getCompound(skillData.name()));
//                    } else {
//                        LOGGER.warn("Missing skill data for: {}", skillData.name());
//                    }
//                }
//            } default -> { // Handle unknown versions for migrating data
//                // Set defaults here for new player / world
//                LOGGER.info("Unknown Bender data version: {}", version);
//            }
//        }
    }

    public static @Nullable Bender getBender(final LivingEntity entity) {
        if (entity == null)
            return null;
        return entity.getCapability(AvatarCapabilities.BENDER);
    }

}
