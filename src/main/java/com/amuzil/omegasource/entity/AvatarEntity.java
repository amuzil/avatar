package com.amuzil.omegasource.entity;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.skill.traits.DataTrait;
import com.amuzil.omegasource.bending.element.Element;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.entity.modules.*;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.amuzil.omegasource.Avatar.isClientOrServer;

public abstract class AvatarEntity extends Entity {

    private static final EntityDataAccessor<Optional<UUID>> OWNER_ID = SynchedEntityData.defineId(AvatarEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<String> ELEMENT = SynchedEntityData.defineId(AvatarEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> FX = SynchedEntityData.defineId(AvatarEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> ONE_SHOT_FX = SynchedEntityData.defineId(AvatarEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> COLLIDABLE = SynchedEntityData.defineId(AvatarEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DAMAGEABLE = SynchedEntityData.defineId(AvatarEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PHYSICS = SynchedEntityData.defineId(AvatarEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TIER = SynchedEntityData.defineId(AvatarEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MAX_LIFETIME = SynchedEntityData.defineId(AvatarEntity.class, EntityDataSerializers.INT);

    private final List<IEntityModule> modules = new ArrayList<>();
    private final List<IControlModule> controlModules = new ArrayList<>();
    private final List<IForceModule> forceModules = new ArrayList<>();
    private final List<ICollisionModule> collisionModules = new ArrayList<>();
    private final List<IRenderModule> renderModules = new ArrayList<>();
    private final List<DataTrait> traits = new LinkedList<>();
    private Entity owner;
    private Element element;
    private String fxName;
    private boolean fxOneShot = true;
    private boolean hittable = false;
    private boolean damageable = false;

    // Data Sync for Owner
    // Data Sync for Element
    // Data Sync for Behaviour
    // Data Sync for each trait

    public AvatarEntity(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void tick() {
        super.tick();

        // Tick appropriate modules in each order
        modules.forEach(mod -> mod.tick(this));
        controlModules.forEach(mod -> mod.tick(this));
        forceModules.forEach(mod -> mod.tick(this));
        collisionModules.forEach(mod -> mod.tick(this));
        renderModules.forEach(mod -> mod.tick(this));
    }

    public void tickDespawn() {
        if (tickCount >= maxLifetime()) {
            this.discard();
        }
    }

    // --- Module Management ---
    public void printModules() {
        System.out.println(isClientOrServer(this.level().isClientSide()) + " " + this.getId() + " has the following modules:");
        modules.forEach(m -> System.out.print(m.id() + ", "));
        System.out.println();
    }

    public void addModule(IEntityModule mod) {
        modules.add(mod);
    }

    public boolean removeModule(IEntityModule mod) {
        return removeModule(mod.id());
    }

    public boolean removeModule(String id) {
        return modules.removeIf(m -> m.id().equals(id));
    }

    public List<IEntityModule> genericModules() {
        return Collections.unmodifiableList(modules);
    }

    // Control modules
    public void addControlModule(IControlModule mod) {
        controlModules.add(mod);
    }

    public boolean removeControlModule(IControlModule mod) {
        return removeControlModule(mod.id());
    }

    public boolean removeControlModule(String id) {
        return controlModules.removeIf(m -> m.id().equals(id));
    }

    public List<IControlModule> controlModules() {
        return Collections.unmodifiableList(controlModules);
    }

    // Force modules
    public void addForceModule(IForceModule mod) {
        forceModules.add(mod);
    }

    public boolean removeForceModule(IForceModule mod) {
        return removeForceModule(mod.id());
    }

    public boolean removeForceModule(String id) {
        return forceModules.removeIf(m -> m.id().equals(id));
    }

    public List<IForceModule> forceModules() {
        return Collections.unmodifiableList(forceModules);
    }

    // Collision modules
    public void addCollisionModule(ICollisionModule mod) {
        collisionModules.add(mod);
    }

    public boolean removeCollisionModule(ICollisionModule mod) {
        return removeCollisionModule(mod.id());
    }

    public boolean removeCollisionModule(String id) {
        return collisionModules.removeIf(m -> m.id().equals(id));
    }

    public List<ICollisionModule> collisionModules() {
        return Collections.unmodifiableList(collisionModules);
    }

    // Render modules
    public void addRenderModule(IRenderModule mod) {
        renderModules.add(mod);
    }

    public boolean removeRenderModule(IRenderModule mod) {
        return removeRenderModule(mod.id());
    }

    public boolean removeRenderModule(String id) {
        return renderModules.removeIf(m -> m.id().equals(id));
    }

    public List<IRenderModule> renderModules() {
        return Collections.unmodifiableList(renderModules);
    }

    public void setMaxLifetime(int max) {
        entityData.set(MAX_LIFETIME, max);
    }

    public int maxLifetime() {
        return entityData.get(MAX_LIFETIME);
    }

    public void addTraits(DataTrait... traits) {
        this.traits.addAll(List.of(traits));
    }

    public List<DataTrait> getFilteredTraits(Predicate<? super DataTrait> filter) {
        return traits.stream().filter(filter).collect(Collectors.toList());
    }

    @Nullable
    public DataTrait getTrait(String name) {
        for (DataTrait trait : traits)
            if (trait.name().equals(name)) return trait;

        return null;
    }

    @Nullable
    public <T extends DataTrait> T getTrait(String name, Class<T> type) {
        for (DataTrait trait : traits) {
            if (trait.name().equals(name) && type.isInstance(trait)) {
                return type.cast(trait);
            }
        }
        return null;
    }

    /** Call this after adding it to a world.
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
            uid.ifPresent(value -> {
                if (!level().isClientSide())
                    this.owner = ((ServerLevel) this.level()).getEntity(value);
            });
        }
        return this.owner;
    }

    public void setElement(Element element) {
        this.entityData.set(ELEMENT, element.getId().toString());
        this.element = Elements.get(ResourceLocation.parse(this.entityData.get(ELEMENT))); // Doesn't live to see the next tick
    }

    public Element element() {
        return Elements.get(ResourceLocation.parse(this.entityData.get(ELEMENT)));
    }

    public void setFX(String fxName) {
        this.entityData.set(FX, fxName);
        this.fxName = this.entityData.get(FX); // Doesn't live to see the next tick
    }

    public ResourceLocation fxLocation() {
        return ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, this.entityData.get(FX));
    }

    public void setFXOneShot(boolean fxOneShot) {
        this.entityData.set(ONE_SHOT_FX, fxOneShot);
        this.fxOneShot = this.entityData.get(ONE_SHOT_FX); // Doesn't live to see the next tick
    }

    public boolean oneShotFX() {
        return this.entityData.get(ONE_SHOT_FX);
    }


    public void setPhysics(boolean physics) {
        this.entityData.set(PHYSICS, physics);
    }

    public boolean physics() {
        return this.entityData.get(PHYSICS);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(OWNER_ID, Optional.empty());
        this.entityData.define(ELEMENT, Elements.FIRE.getId().toString());
        this.entityData.define(FX, "");
        this.entityData.define(ONE_SHOT_FX, true);
        this.entityData.define(COLLIDABLE, false);
        this.entityData.define(DAMAGEABLE, false);
        this.entityData.define(PHYSICS, false);
        this.entityData.define(TIER, 0);
        this.entityData.define(MAX_LIFETIME, 100);
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
            this.entityData.set(ELEMENT, pCompound.getString("Element"));
        }

        this.hittable = pCompound.getBoolean("Collidable");
        this.damageable = pCompound.getBoolean("Damageable");

        readTraits(pCompound);
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

        pCompound.putBoolean("Collidable", hittable);
        pCompound.putBoolean("Damageable", damageable);

        writeTraits(pCompound);
        writeModuleList(pCompound, "GenericModules", modules);
        writeModuleList(pCompound, "ControlModules", controlModules);
        writeModuleList(pCompound, "ForceModules", forceModules);
        writeModuleList(pCompound, "CollisionModules", collisionModules);
        writeModuleList(pCompound, "RenderModules", renderModules);

    }

    public void checkBlocks() {
        checkInsideBlocks();
    }

    private <T> void readModuleList(CompoundTag parent, String key, List<T> list) {
        // Clears the list before re-adding whatever modules it needs toAdd commentMore actions
        list.clear();
        ListTag mods = parent.getList(key, Tag.TAG_COMPOUND);
        for (Tag t : mods) {
            CompoundTag mTag = (CompoundTag) t;
            String id = mTag.getString("Module ID");
            IEntityModule mod = ModuleRegistry.create(id);
            if (mod != null) {
                mod.load(mTag);
                @SuppressWarnings("unchecked") T casted = (T) mod;
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

    private void writeTraits(CompoundTag parent) {
        ListTag list = new ListTag();
        for (DataTrait trait : traits) {
            CompoundTag tTag = new CompoundTag();
            // TODO: check whether ensuring ID is included is necessary
            tTag.put("Trait Data", trait.serializeNBT());
            list.add(tTag);
        }
        parent.put("DataTraits", list);
    }

    private void readTraits(CompoundTag parent) {
        ListTag list = parent.getList("DataTraits", Tag.TAG_COMPOUND);

        int limit = Math.min(list.size(), traits.size());
        for (int i = 0; i < limit; i++) {
            CompoundTag tTag = list.getCompound(i);
            traits.get(i).deserializeNBT(tTag.getCompound("Trait Data"));
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return hittable;
    }

    /**
     * Returns {@code true} if this entity should push and be pushed by other entities when colliding.
     */
    @Override
    public boolean isPushable() {
        return hittable;
    }

    @Override
    public boolean canBeHitByProjectile() {
        return hittable;
    }

    @Override
    public boolean isInvulnerable() {
        return damageable;
    }

    //  TODO - These were copied from the projectile class. Need to update these to account for the other data
    //   serializers and important values that this class keeps track of.
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        Entity entity = this.owner();
        return new ClientboundAddEntityPacket(this, entity == null ? 0 : entity.getId());
    }

    public void recreateFromPacket(ClientboundAddEntityPacket pPacket) {
        super.recreateFromPacket(pPacket);
        Entity entity = this.level().getEntity(pPacket.getData());
        if (entity != null) {
            this.setOwner(entity);
        }

    }

    public boolean mayInteract(Level pLevel, BlockPos pPos) {
        Entity entity = this.owner();
        if (entity instanceof Player) {
            return entity.mayInteract(pLevel, pPos);
        } else {
            return entity == null || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(pLevel, entity);
        }
    }

    public void setDamageable(boolean damageable) {
        entityData.set(DAMAGEABLE, damageable);
    }

    public void setHittable(boolean hittable) {
        entityData.set(COLLIDABLE, hittable);
    }
}
