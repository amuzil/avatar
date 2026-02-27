package com.amuzil.av3.utils.bending;

import com.amuzil.av3.entity.construct.AvatarRigidBlock;
import com.amuzil.caliber.physics.bullet.math.Convert;
import com.jme3.math.Vector3f;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import static com.amuzil.av3.utils.bending.SkillHelper.getPivot;
import static com.amuzil.av3.utils.bending.SkillHelper.getRightPivot;

public class RigidBlockFactory {

    public static @NotNull AvatarRigidBlock createKinematicBlock(Level level, BlockState blockState, LivingEntity owner, int blockCount, int lifetime, float size) {
        AvatarRigidBlock rigidBlock = new AvatarRigidBlock(level);
        rigidBlock.setBlockState(blockState);
        rigidBlock.setOwner(owner);
        rigidBlock.setPos(getRightPivot(owner, 1.0f, blockCount * -0.8));
        rigidBlock.setMaxLifetime(lifetime);
        rigidBlock.setWidth(size);
        rigidBlock.setHeight(size);
        rigidBlock.getRigidBody().setPhysicsRotation(Convert.toBullet(owner.getXRot(), owner.getYRot()));
        rigidBlock.getRigidBody().setMass(0f);
        rigidBlock.getRigidBody().setKinematic(true);
        rigidBlock.getRigidBody().prioritize((Player) owner);
        rigidBlock.setDamageable(false);
        rigidBlock.setControlled(true);
        return rigidBlock;
    }

    public static @NotNull AvatarRigidBlock createWall(Level level, BlockState blockState, LivingEntity owner, int lifetime, float size) {
        AvatarRigidBlock rigidBlock = new AvatarRigidBlock(level);
        rigidBlock.setBlockState(blockState);
        rigidBlock.setOwner(owner);
        rigidBlock.getRigidBody().setPhysicsRotation(Convert.toBullet(0, owner.getYRot()));
        rigidBlock.setPos(getPivot(owner, 3f)); // TODO make it spawn at set y position
//        rigidBlock.setMaxLifetime(lifetime);
        rigidBlock.setWidth(size);
        rigidBlock.setHeight(size);
        rigidBlock.getRigidBody().setFriction(0.1f);
        rigidBlock.getRigidBody().setAngularFactor(0f);
        rigidBlock.getRigidBody().setAngularVelocity(new Vector3f(0f, 0f, 0f));
        rigidBlock.setDamageable(false);
        rigidBlock.setRigidBodyDirty(true);
        return rigidBlock;
    }
}
