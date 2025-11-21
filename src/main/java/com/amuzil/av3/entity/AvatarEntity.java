package com.amuzil.av3.entity;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.element.Element;
import com.amuzil.av3.bending.element.Elements;
import com.amuzil.av3.entity.api.ICollisionModule;
import com.amuzil.av3.entity.api.IEntityModule;
import com.amuzil.av3.entity.api.IForceModule;
import com.amuzil.av3.entity.api.IRenderModule;
import com.amuzil.magus.skill.traits.DataTrait;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.amuzil.av3.Avatar.isClientOrServer;


public abstract class AvatarEntity extends Entity {

    private static final EntityDataAccessor<Optional<UUID>> OWNER_ID = SynchedEntityData.defineId(AvatarEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<String> ELEMENT = SynchedEntityData.defineId(AvatarEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> SKILL_ID = SynchedEntityData.defineId(AvatarEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<String> FX = SynchedEntityData.defineId(AvatarEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Boolean> ONE_SHOT_FX = SynchedEntityData.defineId(AvatarEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> COLLIDABLE = SynchedEntityData.defineId(AvatarEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DAMAGEABLE = SynchedEntityData.defineId(AvatarEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> PHYSICS = SynchedEntityData.defineId(AvatarEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> TIER = SynchedEntityData.defineId(AvatarEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> MAX_LIFETIME = SynchedEntityData.defineId(AvatarEntity.class, EntityDataSerializers.INT);

    private final List<IEntityModule> modules = new ArrayList<>();
    private final List<IForceModule> forceModules = new ArrayList<>();
    private final List<ICollisionModule> collisionModules = new ArrayList<>();
    private final List<IRenderModule> renderModules = new ArrayList<>();
    private final List<DataTrait> traits = new LinkedList<>();
    private Entity owner;

    public AvatarEntity(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    /** Call this after adding it to a world.
     */
    public void init() {
        modules.forEach(mod -> mod.init(this));
        forceModules.forEach(mod -> mod.init(this));
        collisionModules.forEach(mod -> mod.init(this));
        renderModules.forEach(mod -> mod.init(this));
    }

    @Override
    public void tick() {
        super.tick();

        // Tick appropriate modules in each order
        if (this.level().isClientSide()) {
            renderModules.forEach(mod -> mod.tick(this));
        } else {
            modules.forEach(mod -> mod.tick(this));
            forceModules.forEach(mod -> mod.tick(this));
            collisionModules.forEach(mod -> mod.tick(this));
        }
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


    public String skillId() {
        return entityData.get(SKILL_ID);
    }

    public void setSkillId(String skillId) {
        entityData.set(SKILL_ID, skillId);
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

    public void setOwner(@NotNull Entity owner) {
        this.owner = owner;
        this.entityData.set(OWNER_ID, Optional.of(owner.getUUID()));
    }

    public Entity getOwner() {
        return owner();
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
    }

    public Element element() {
        return Elements.get(ResourceLocation.parse(this.entityData.get(ELEMENT)));
    }

    public void setFX(String fxName) {
        this.entityData.set(FX, fxName);
    }

    public String fxName() {
        return this.entityData.get(FX);
    }

    public ResourceLocation fxLocation() {
        String fxName = this.entityData.get(FX);
        if (fxName.isEmpty())
            return null;
        return Avatar.id(fxName);
    }

    public void setFXOneShot(boolean fxOneShot) {
        this.entityData.set(ONE_SHOT_FX, fxOneShot);
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
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(OWNER_ID, Optional.empty());
        builder.define(ELEMENT, Elements.FIRE.getId().toString());
        builder.define(SKILL_ID, "");
        builder.define(FX, "");
        builder.define(ONE_SHOT_FX, true);
        builder.define(COLLIDABLE, false);
        builder.define(DAMAGEABLE, false);
        builder.define(PHYSICS, false);
        builder.define(TIER, 0);
        builder.define(MAX_LIFETIME, 10000);

    }

    public void checkBlocks() {
        checkInsideBlocks();
    }

//    @Override
//    public boolean canCollideWith(Entity other) {
//        return getY() + 0.01 >= other.getY() + other.getBoundingBox().getYsize();
//    }

    /**
     * Returns {@code true} if this entity can be collided with
     */
    @Override
    public boolean canBeCollidedWith() {
        return entityData.get(COLLIDABLE);
    }

    /**
     * Returns {@code true} if this entity should push and be pushed by other entities when colliding.
     */
    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    public boolean canBeHitByProjectile() {
        return entityData.get(COLLIDABLE);
    }

    @Override
    public boolean isInvulnerable() {
        return entityData.get(DAMAGEABLE);
    }

    /** These were copied from the projectile class. Need to update these to account for the other data
     * serializers and important values that this class keeps track of.
     */
    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity serverEntity) {
        Entity entity = this.getOwner();
        return new ClientboundAddEntityPacket(this, serverEntity, entity == null ? 0 : entity.getId());
    }

    @Override
    public void recreateFromPacket(ClientboundAddEntityPacket packet) {
        super.recreateFromPacket(packet);
        Entity entity = this.level().getEntity(packet.getData());
        if (entity != null) {
            this.setOwner(entity);
        }
    }

    @Override
    public boolean mayInteract(Level level, BlockPos pos) {
        Entity entity = this.getOwner();
        return entity instanceof Player ? entity.mayInteract(level, pos) : entity == null || EventHooks.canEntityGrief(level, entity);
    }

    public void setDamageable(boolean damageable) {
        entityData.set(DAMAGEABLE, damageable);
    }

    public void setCollidable(boolean collidable) {
        entityData.set(COLLIDABLE, collidable);
    }
}
