package com.amuzil.av3.entity.controller;

import com.amuzil.av3.entity.AvatarEntity;
import com.amuzil.av3.entity.api.EntityGrid;
import com.amuzil.av3.entity.api.modules.IAvatarController;
import com.amuzil.av3.entity.api.modules.ModuleRegistry;
import com.amuzil.av3.entity.api.modules.force.MoveModule;
import com.amuzil.av3.entity.construct.AvatarElementCollider;
import com.amuzil.magus.physics.PhysicsBuilder;
import com.amuzil.magus.physics.core.ForceCloud;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;

import static com.amuzil.av3.entity.AvatarEntities.AVATAR_PHYSICS_CONTROLLER_ENTITY_TYPE;

// Should hold element rigidbodies and a force cloud...
public class AvatarPhysicsController extends AvatarEntity implements IAvatarController {
    protected static final EntityDataAccessor<Float> WIDTH = SynchedEntityData.defineId(AvatarPhysicsController.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(AvatarPhysicsController.class, EntityDataSerializers.FLOAT);
    protected static final EntityDataAccessor<Float> DEPTH = SynchedEntityData.defineId(AvatarPhysicsController.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Boolean> CONTROLLED = SynchedEntityData.defineId(AvatarPhysicsController.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DYING = SynchedEntityData.defineId(AvatarPhysicsController.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DEATH_TIMER = SynchedEntityData.defineId(AvatarPhysicsController.class, EntityDataSerializers.INT);
    private final float cellSize = PhysicsBuilder.CELL_SIZE;
    // Physics is automatically handled.
    private ForceCloud forceCloud;
    private HashMap<Integer, AvatarElementCollider> elements;
    private EntityGrid<AvatarElementCollider> elementGrid;

    public AvatarPhysicsController(EntityType<? extends AvatarPhysicsController> entityType, Level level) {
        super(entityType, level);
        setCollidable(false);
        setDamageable(false);
        setInvulnerable(true);
        addModule(ModuleRegistry.create(MoveModule.id));
    }

    public AvatarPhysicsController(Level level) {
        this(AVATAR_PHYSICS_CONTROLLER_ENTITY_TYPE.get(), level);
        this.elements = new HashMap<>();

    }

    public void initGrid() {
        this.elementGrid = new EntityGrid<>(PhysicsBuilder.CELL_SIZE, (int) PhysicsBuilder.GRID_SIZE, (int) PhysicsBuilder.GRID_SIZE, (int) PhysicsBuilder.GRID_SIZE,
                30, (long) position().x, (long) position().y, (long) position().z, null);
    }

    @Override
    public void tick() {
//        System.out.println("Tick Count: " + this.tickCount);

        if (entityGrid() != null) {
            entityGrid().rebuild();
        }
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
    public boolean controlled() {
        return this.entityData.get(CONTROLLED);
    }

    public void control(float scale) {
        Entity owner = this.getOwner();
        if (owner == null) return;
        Vec3[] pose = new Vec3[]{owner.position(), owner.getLookAngle()};
        pose[1] = pose[1].scale((scale)).add((0), (owner.getEyeHeight()), (0));
        Vec3 newPos = pose[1].add(pose[0]);
        control(newPos, 0.5f);
    }

    @Override
    public void control(Vec3 pos, float motion) {
        Vec3 dir = pos.subtract(position()).scale(motion);
        this.setDeltaMovement(dir);
    }

    @Override
    public void controlled(boolean controlled) {
        this.entityData.set(CONTROLLED, controlled);
    }

    public void setWidth(float size) {
        this.entityData.set(WIDTH, size);
    }

    public void setHeight(float size) {
        this.entityData.set(HEIGHT, size);
    }

    public void setDepth(float size) {
        this.entityData.set(DEPTH, size);
    }

    public void setSize(float size) {
        setWidth(size);
        setHeight(size);
        setDepth(size);
    }

    public float getWidth() {
        return this.entityData.get(WIDTH);
    }

    public float getHeight() {
        return this.entityData.get(HEIGHT);
    }

    public float getDepth() {
        return this.entityData.get(DEPTH);
    }



    @Override
    public void kill() {
        if (forceCloud != null) {
            // Should automatically remove itself from the physics space.
            forceCloud.kill();
        }
        if (elementGrid != null)
            elementGrid.clear();
        if (elements != null)
            elements.clear();
        super.kill();
        discard();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DYING, false);
        builder.define(DEATH_TIMER, 15);
        builder.define(CONTROLLED, false);
        builder.define(HEIGHT, 0.5f);
        builder.define(WIDTH, 0.5f);
        builder.define(DEPTH, 0.5f);
    }

    public boolean dying() {
        return this.entityData.get(DYING);
    }

    public void dying(boolean dying) {
        this.entityData.set(DYING, dying);
    }

    public int deathTimer() {
        return this.entityData.get(DEATH_TIMER);
    }

    public void deathTimer(int deathTimer) {
        this.entityData.set(DEATH_TIMER, deathTimer);
    }

    public EntityGrid<AvatarElementCollider> entityGrid() {
        return this.elementGrid;
    }
}
