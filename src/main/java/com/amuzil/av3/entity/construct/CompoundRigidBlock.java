package com.amuzil.av3.entity.construct;

import com.amuzil.av3.entity.AvatarEntities;
import com.amuzil.caliber.physics.bullet.collision.body.shape.MinecraftShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;


public class CompoundRigidBlock extends AvatarRigidBlock {
    private final List<AvatarRigidBlock> childBlocks;

    public CompoundRigidBlock(EntityType<? extends AvatarRigidBlock> type, Level level) {
        super(type, level);
        childBlocks = new ArrayList<>();
    }

    public CompoundRigidBlock(Level level) {
        this(AvatarEntities.COMPOUND_RIGID_BLOCK_ENTITY_TYPE.get(), level);
    }

    @Override
    public void tick() {
        if (isRigidBodyDirty()) {
            List<CollisionShape> shapes = childBlocks.stream().map(block -> block.getRigidBody().getCollisionShape()).toList();
            rigidBody.setCollisionShape(MinecraftShape.compound(shapes));
            float actualMass = 10 * (this.width() * this.height() * this.depth());
            rigidBody.setMass(actualMass);
            defaultMass = rigidBody.getMass();
            setRigidBodyDirty(false);
        }

        // Save previous tick position
        this.xOld = this.getX();
        this.yOld = this.getY();
        this.zOld = this.getZ();
        super.tick();
    }

    public List<AvatarRigidBlock> getChildBlocks() {
        return childBlocks;
    }

    public void addChildBlock(AvatarRigidBlock block) {
        childBlocks.add(block);
        setRigidBodyDirty(true);
    }

}
