package com.amuzil.av3.utils.bending;

import com.amuzil.av3.entity.construct.AvatarRigidBlock;
import com.amuzil.caliber.physics.bullet.collision.body.EntityRigidBody;
import com.amuzil.caliber.physics.bullet.collision.space.MinecraftSpace;
import com.amuzil.caliber.physics.bullet.math.Convert;
import com.jme3.bullet.RotationOrder;
import com.jme3.bullet.joints.New6Dof;
import com.jme3.bullet.joints.motors.MotorParam;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import static com.amuzil.av3.utils.bending.SkillHelper.getPivot;
import static com.amuzil.av3.utils.bending.SkillHelper.getRightPivot;

public class RigidBlockFactory {

    public static @NotNull AvatarRigidBlock createBlock(Level level, BlockState blockState, LivingEntity owner, int lifetime, float size) {
        AvatarRigidBlock rigidBlock = new AvatarRigidBlock(level);
        rigidBlock.setBlockState(blockState);
        rigidBlock.setOwner(owner);
        rigidBlock.setYRot(owner.getYRot());
        rigidBlock.setYHeadRot(owner.getYRot());
        rigidBlock.yRotO = owner.getYRot(); // previous rotation too, prevents lerp snapping
        rigidBlock.setXRot(0f); // ignore pitch, walls should stand upright
        rigidBlock.getRigidBody().setPhysicsRotation(Convert.toBullet(0, owner.getYRot()));
        rigidBlock.setMaxLifetime(lifetime);
        rigidBlock.setWidth(size);
        rigidBlock.setHeight(size);
        rigidBlock.setDamageable(false);
        return rigidBlock;
    }

    public static @NotNull AvatarRigidBlock createKinematicBlock(Level level, BlockState blockState, LivingEntity owner, int blockCount, int lifetime, float size) {
        AvatarRigidBlock rigidBlock = new AvatarRigidBlock(level);
        rigidBlock.setBlockState(blockState);
        rigidBlock.setOwner(owner);
        rigidBlock.getRigidBody().setPhysicsRotation(Convert.toBullet(owner.getXRot(), owner.getYRot()));
        rigidBlock.setPos(getRightPivot(owner, 1.0f, blockCount * -0.8));
        rigidBlock.setMaxLifetime(lifetime);
        rigidBlock.setWidth(size);
        rigidBlock.setHeight(size);
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

    public static void createGlueJoint(MinecraftSpace space, AvatarRigidBlock blockA, AvatarRigidBlock blockB) {
        EntityRigidBody rigidBodyA = blockA.getRigidBody();
        EntityRigidBody rigidBodyB = blockB.getRigidBody();

        Vector3f posA = rigidBodyA.getPhysicsLocation(new Vector3f());
        Vector3f posB = rigidBodyB.getPhysicsLocation(new Vector3f());
        Vector3f midpoint = posA.add(posB).mult(0.5f);

        Vector3f worldOffsetA = midpoint.subtract(posA);
        Vector3f worldOffsetB = midpoint.subtract(posB);

        Quaternion rotA = rigidBodyA.getPhysicsRotation(new Quaternion());
        Quaternion rotB = rigidBodyB.getPhysicsRotation(new Quaternion());

        // Convert to rotation matrix and invert to get local space
        Matrix3f rotMatA = rotA.toRotationMatrix(new Matrix3f()).invert(new Matrix3f());
        Matrix3f rotMatB = rotB.toRotationMatrix(new Matrix3f()).invert(new Matrix3f());

        Vector3f pivotInA = rotMatA.mult(worldOffsetA, new Vector3f());
        Vector3f pivotInB = rotMatB.mult(worldOffsetB, new Vector3f());

        New6Dof glue = new New6Dof(
                rigidBodyA, rigidBodyB,
                pivotInA, pivotInB,
                Matrix3f.IDENTITY, Matrix3f.IDENTITY,
                RotationOrder.XYZ
        );

        for (int dof = 0; dof < 6; dof++) {
            glue.set(MotorParam.LowerLimit, dof, 0f);
            glue.set(MotorParam.UpperLimit, dof, 0f);
        }

        glue.setBreakingImpulseThreshold(50f);
        glue.setCollisionBetweenLinkedBodies(false);
        space.addJoint(glue);
        blockA.addGlueJoint(glue);
        blockB.addGlueJoint(glue);
    }
}
