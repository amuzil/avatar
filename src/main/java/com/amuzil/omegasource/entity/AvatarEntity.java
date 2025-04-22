package com.amuzil.omegasource.entity;

import com.amuzil.omegasource.api.magus.skill.traits.DataTrait;
import com.amuzil.omegasource.bending.element.Element;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.entity.modules.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.*;

public abstract class AvatarEntity extends Entity {

    private static final EntityDataAccessor<Optional<UUID>> OWNER_ID =
            SynchedEntityData.defineId(AvatarEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<String> ELEMENT =
            SynchedEntityData.defineId(AvatarEntity.class, EntityDataSerializers.STRING);

    private Entity owner;
    private Element element;

    private List<DataTrait> traits;

    private List<IEntityModule> modules = new ArrayList<>();
    private List<IControlModule> controlModules = new ArrayList<>();
    private List<IForceModule> forceModules = new ArrayList<>();
    private List<ICollisionModule> collisionModules = new ArrayList<>();
    private List<IRenderModule> renderModules = new ArrayList<>();

    // Data Sync for Owner
    // Data Sync for Element
    // Data Sync for Behaviour
    // Data Sync for each trait

    public AvatarEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    // --- Module Management ---
    public void addModule(IEntityModule mod) {
        modules.add(mod);
    }

    public void addControlModule(IControlModule mod) {
        controlModules.add(mod);
    }

    public void addForceModule(IForceModule mod) {
        forceModules.add(mod);
    }

    public void addCollisionModule(ICollisionModule mod) {
        collisionModules.add(mod);
    }

    public void addRenderModule(IRenderModule mod) {
        renderModules.add(mod);
    }

    public boolean removeModule(IEntityModule mod) {
        return modules.remove(mod);
    }

    public boolean removeControlModule(IControlModule mod) {
        return controlModules.remove(mod);
    }

    public boolean removeForceModule(IForceModule mod) {
        return forceModules.remove(mod);
    }

    public boolean removeCollisionModule(ICollisionModule mod) {
        return collisionModules.remove(mod);
    }

    public boolean removeRenderModule(IRenderModule mod) {
        return renderModules.remove(mod);
    }

    public List<IEntityModule> genericModules() {
        return Collections.unmodifiableList(modules);
    }

    public List<IControlModule> controlModules() {
        return Collections.unmodifiableList(controlModules);
    }

    public List<IForceModule> forceModules() {
        return Collections.unmodifiableList(forceModules);
    }

    public List<ICollisionModule> collisionModules() {
        return Collections.unmodifiableList(collisionModules);
    }

    public List<IRenderModule> renderModules() {
        return Collections.unmodifiableList(renderModules);
    }

    /*
        Call this after adding it to a world.
     */
    public void init() {
        modules.forEach(mod -> mod.init(this));
        controlModules.forEach(mod -> mod.init(this));
        forceModules.forEach(mod -> mod.init(this));
        collisionModules.forEach(mod -> mod.init(this));
        renderModules.forEach(mod -> mod.init(this));
    }


    public void setOwner(Entity owner) {
        this.owner = owner;
        this.entityData.set(OWNER_ID, Optional.of(owner.getUUID()));
    }


    public Entity owner() {
        if (this.owner == null) {
            Optional<UUID> uid = this.entityData.get(OWNER_ID);
            uid.ifPresent(value -> this.owner = ((ServerLevel) this.level()).getEntity(value));
        }
        return this.owner;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(OWNER_ID, Optional.empty());
        this.entityData.define(ELEMENT, Elements.FIRE.id().toString());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     *
     * @param pCompound
     */
    @Override
    protected void readAdditionalSaveData(CompoundTag pCompound) {
        if (pCompound.hasUUID("OwnerUUID")) {
            this.entityData.set(OWNER_ID, Optional.of(pCompound.getUUID("OwnerUUID")));
        }

        // Element
        if (pCompound.contains("Element")) {
            this.element = Elements.get(ResourceLocation.parse(pCompound.getString("Element")));
            this.entityData.set(ELEMENT, element.id().toString());
        }

        readModuleList(pCompound, "GenericModules", modules);
        readModuleList(pCompound, "ControlModules", controlModules);
        readModuleList(pCompound, "ForceModules", forceModules);
        readModuleList(pCompound, "CollisionModules", collisionModules);
        readModuleList(pCompound, "RenderModules", renderModules);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag pCompound) {
        this.entityData.get(OWNER_ID).ifPresent(uuid -> pCompound.putUUID("OwnerUUID", uuid));

        // Element
        if (element != null) {
            pCompound.putString("Element", element.name());
        }

        writeModuleList(pCompound, "GenericModules", modules);
        writeModuleList(pCompound, "ControlModules", controlModules);
        writeModuleList(pCompound, "ForceModules", forceModules);
        writeModuleList(pCompound, "CollisionModules", collisionModules);
        writeModuleList(pCompound, "RenderModules", renderModules);

    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void tick() {
        super.tick();

        // Tick appropriate modules in each other
        modules.forEach(mod -> mod.tick(this));
        controlModules.forEach(mod -> mod.tick(this));
        forceModules.forEach(mod -> mod.tick(this));
        collisionModules.forEach(mod -> mod.tick(this));
        renderModules.forEach(mod -> mod.tick(this));
    }

    private <T> void readModuleList(CompoundTag parent, String key, List<T> list) {
        ListTag mods = parent.getList(key, Tag.TAG_COMPOUND);
        for (Tag t : mods) {
            CompoundTag mTag = (CompoundTag) t;
            String id = mTag.getString("Module ID");
            IEntityModule mod = ModuleRegistry.create(id);
            if (mod != null) {
                mod.load(mTag);
                @SuppressWarnings("unchecked")
                T casted = (T) mod;
                list.add(casted);
            }
        }
    }

    private void writeModuleList(CompoundTag parent, String key, List<? extends IEntityModule> list) {
        ListTag mods = new ListTag();
        for (IEntityModule mod : list) {
            CompoundTag mTag = new CompoundTag();
            mTag.putString("Module ID", mod.id());
            mod.save(mTag);
            mods.add(mTag);
        }
        parent.put(key, mods);
    }
}
