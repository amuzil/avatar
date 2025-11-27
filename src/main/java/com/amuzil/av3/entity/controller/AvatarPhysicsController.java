package com.amuzil.av3.entity.controller;

import com.amuzil.av3.entity.api.EntityGrid;
import com.amuzil.av3.entity.api.modules.IAvatarController;
import com.amuzil.av3.entity.construct.AvatarConstruct;
import com.amuzil.av3.entity.construct.AvatarElementCollider;
import com.amuzil.magus.physics.PhysicsBuilder;
import com.amuzil.magus.physics.core.ForceCloud;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.HashMap;

import static com.amuzil.av3.entity.AvatarEntities.AVATAR_PHYSICS_CONTROLLER_ENTITY_TYPE;

// Should hold element rigidbodies and a force cloud...
public class AvatarPhysicsController extends AvatarConstruct implements IAvatarController {
    private static final EntityDataAccessor<Boolean> DYING = SynchedEntityData.defineId(AvatarPhysicsController.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DEATH_TIMER = SynchedEntityData.defineId(AvatarPhysicsController.class, EntityDataSerializers.INT);
    private float cellSize = PhysicsBuilder.CELL_SIZE;
    // Physics is automatically handled.
    private ForceCloud forceCloud;
    private HashMap<Integer, AvatarElementCollider> elements;
    private EntityGrid<AvatarElementCollider> elementGrid;

    public AvatarPhysicsController(EntityType<? extends AvatarConstruct> entityType, Level level) {
        super(entityType, level);
        setCollidable(false);
        setDamageable(false);
        setInvulnerable(true);
    }

    public AvatarPhysicsController(Level level) {
        this(AVATAR_PHYSICS_CONTROLLER_ENTITY_TYPE.get(), level);
        this.elements = new HashMap<>();
        this.elementGrid = new EntityGrid<>(PhysicsBuilder.CELL_SIZE, (int) PhysicsBuilder.GRID_SIZE, (int) PhysicsBuilder.GRID_SIZE, (int) PhysicsBuilder.GRID_SIZE,
                30, (long) position().x, (long) position().y, (long) position().z, null);
    }


    @Override
    public void tick() {
        System.out.println("Tick Count: " + this.tickCount);
        super.tick();

    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     *
     * @param compound
     */
    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {

    }

    @Override
    public ForceCloud forceCloud() {
        return forceCloud;
    }

    @Override
    public void setForceCloud(ForceCloud cloud) {
        this.forceCloud = cloud;
    }


    @Override
    public void kill() {
        if (forceCloud != null) {
            // Should automatically remove itself from the physics space.
            forceCloud.kill();
        }
        elementGrid.clear();
        elements.clear();
        super.kill();
        discard();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DYING, false);
        builder.define(DEATH_TIMER, 15);
    }

    public boolean dying() {
        return this.entityData.get(DYING);
    }

    public void dying(boolean dying) {
        this.entityData.set(DYING, dying);
    }

    public  int deathTimer() {
        return this.entityData.get(DEATH_TIMER);
    }

    public  void deathTimer(int deathTimer) {
        this.entityData.set(DEATH_TIMER, deathTimer);
    }

    public EntityGrid<AvatarElementCollider> entityGrid() {
        return this.elementGrid;
    }
}
