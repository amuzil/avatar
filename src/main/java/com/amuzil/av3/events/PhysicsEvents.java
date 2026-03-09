package com.amuzil.av3.events;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.entity.construct.AvatarRigidBlock;
import com.amuzil.av3.utils.bending.RigidBlockFactory;
import com.amuzil.caliber.api.event.collision.CollisionEvent;
import com.amuzil.caliber.api.event.space.PhysicsSpaceEvent;
import com.amuzil.caliber.physics.bullet.collision.body.ElementRigidBody;
import com.amuzil.caliber.physics.bullet.collision.body.EntityRigidBody;
import com.amuzil.caliber.physics.bullet.collision.body.TerrainRigidBody;
import com.amuzil.caliber.physics.bullet.collision.body.shape.MinecraftShape;
import com.amuzil.caliber.physics.bullet.collision.space.MinecraftSpace;
import com.amuzil.caliber.physics.bullet.math.Convert;
import com.jme3.bullet.collision.ManifoldPoints;
import com.jme3.bullet.collision.PersistentManifolds;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.NeoForge;


@EventBusSubscriber(modid = Avatar.MOD_ID)
public class PhysicsEvents {

    /**
     * Trigger all collision events (e.g. block vs element or element vs element).
     *
     * @param event the event context
     */
    @SubscribeEvent
    public static void onPhysicsSpaceStep(PhysicsSpaceEvent.Step event) {
        MinecraftSpace space = event.getSpace();

        for (long manifoldId: space.listManifoldIds()) {
            PhysicsCollisionObject objA = PhysicsCollisionObject.findInstance(PersistentManifolds.getBodyAId(manifoldId));
            PhysicsCollisionObject objB = PhysicsCollisionObject.findInstance(PersistentManifolds.getBodyBId(manifoldId));

            long[] pointIds = PersistentManifolds.listPointIds(manifoldId);
            float maxImpulse = 0f;
            for (long pointId : pointIds) {
                float impulse = ManifoldPoints.getAppliedImpulse(pointId);
                if (impulse > maxImpulse) maxImpulse = impulse;
            }

            if (maxImpulse <= 0f) continue;

            float impulse = maxImpulse;

            if (objA instanceof ElementRigidBody rigidBodyA && objB instanceof ElementRigidBody rigidBodyB) {
                NeoForge.EVENT_BUS.post(new CollisionEvent(CollisionEvent.Type.ELEMENT, rigidBodyA, rigidBodyB, impulse));
            } else if (objA instanceof TerrainRigidBody terrain && objB instanceof ElementRigidBody rigidBody) {
                NeoForge.EVENT_BUS.post(new CollisionEvent(CollisionEvent.Type.BLOCK, rigidBody, terrain, impulse));
            } else if (objA instanceof ElementRigidBody rigidBody && objB instanceof TerrainRigidBody terrain) {
                NeoForge.EVENT_BUS.post(new CollisionEvent(CollisionEvent.Type.BLOCK, rigidBody, terrain, impulse));
            }
        }
    }

    @SubscribeEvent
    public static void onCollision(CollisionEvent event) {
        if (event.getType() == CollisionEvent.Type.ELEMENT) {
            if (event.getMain() instanceof EntityRigidBody wall && wall.getCollisionShape() instanceof MinecraftShape.Compound) {
                if (event.getImpulse() > 60f && wall.getElement().cast() instanceof AvatarRigidBlock rigidBlock) {
                    shatterWall(rigidBlock, event.getOther().getLinearVelocity(new Vector3f()));
//                    System.out.printf("Collision detected with impulse: %.2f%n", event.getImpulse());
                }
            }
        }
    }

    public static void shatterWall(AvatarRigidBlock wall, Vector3f impactVelocity) {
        if (!wall.isAlive()) return;
        wall.discard(); // discard FIRST before spawning fragments
        Level level = wall.level();

        Vector3f wallPos = wall.getRigidBody().getPhysicsLocation(new Vector3f());
        Quaternion wallRot = wall.getRigidBody().getPhysicsRotation(new Quaternion());
        Vector3f wallLinearVel = wall.getRigidBody().getLinearVelocity(new Vector3f());
        Vector3f wallAngularVel = wall.getRigidBody().getAngularVelocity(new Vector3f());

        int rows = 3, cols = 3;
        float size = 1.0f;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                float offsetX = (col - (cols - 1) / 2.0f) * size;
                float offsetY = (row - (rows - 1) / 2.0f) * size;

                // Calculate fragment world position from compound's transform
                Vector3f localOffset = new Vector3f(offsetX, offsetY, 0);
                Matrix3f rotMat = wallRot.toRotationMatrix(new Matrix3f());
                Vector3f worldOffset = rotMat.mult(localOffset, new Vector3f());
                Vector3f fragmentPos = wallPos.add(worldOffset);

                AvatarRigidBlock fragment = RigidBlockFactory.createBlock(level, wall.getBlockState(), (LivingEntity) wall.owner(), wall.maxLifetime(), size);
                fragment.setPos(Convert.toMinecraftVec3(fragmentPos));
                fragment.getRigidBody().setPhysicsLocation(fragmentPos);
                fragment.getRigidBody().setPhysicsRotation(wallRot);

                // Transfer wall velocity + add angular contribution at fragment offset
                Vector3f angularContribution = wallAngularVel.cross(worldOffset);
                fragment.getRigidBody().setLinearVelocity(wallLinearVel.add(angularContribution));
                fragment.getRigidBody().setAngularVelocity(wallAngularVel);

                level.addFreshEntity(fragment);
            }
        }

        // Remove the compound wall entity
        wall.discard();
    }
}
