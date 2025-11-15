// ============================================================================
// PlayerCollisionGenerator.java  (no AABB; uses libbulletjme events; no registration)
// ============================================================================
package com.amuzil.caliber.physics.bullet.collision.space.generator;

import com.amuzil.caliber.api.PlayerPhysicsElement;
import com.amuzil.caliber.physics.bullet.collision.body.PlayerRigidBody;
import com.amuzil.caliber.physics.bullet.collision.space.MinecraftSpace;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.math.Vector3f;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Reads libbulletjme collision events to decide when a player is actually supported by a collider.
 * Call preStep()/postStep() around each Bullet substep, onCollision() from MinecraftSpace.collision(...),
 * and publish() once after all substeps to push state into Player mixins.
 */
public final class PlayerCollisionGenerator {

    private PlayerCollisionGenerator(MinecraftSpace space) {
        this.space = space;
    }

    // one instance per space
    private static final Map<MinecraftSpace, PlayerCollisionGenerator> INSTANCES = new ConcurrentHashMap<>();
    private static PlayerCollisionGenerator get(MinecraftSpace space) {
        return INSTANCES.computeIfAbsent(space, PlayerCollisionGenerator::new);
    }

    /** Clear per-substep accumulators before each PhysicsSpace.update(...) call. */
    public static void preStep(MinecraftSpace space) {
        get(space).reset();
    }

    /** Reserved for post-substep work; currently unused. */
    public static void postStep(MinecraftSpace space) { /* no-op for now */ }

    /** Forward libbulletjme collision events here from MinecraftSpace.collision(...). */
    public static void onCollision(MinecraftSpace space, PhysicsCollisionEvent event) {
        get(space).accumulate(event);
    }

    /** After all substeps in a tick complete, publish the support state to the player mixins. */
    public static void publish(MinecraftSpace space) {
        get(space).publishToPlayers();
    }

    // ---- internals ----
    private final MinecraftSpace space;

    private static final class Accum {
        boolean onCollider;
        float bestScore;
        float upNudge;
        final Vector3f supportVel = new Vector3f();
        void reset() { onCollider = false; bestScore = 0f; upNudge = 0f; supportVel.set(0,0,0); }
    }

    // keyed by Minie PhysicsRigidBody for players only
    private final Map<PhysicsRigidBody, Accum> tickState = new IdentityHashMap<>();

    private void reset() {
        for (Accum a : tickState.values()) a.reset();
    }

    private void accumulate(PhysicsCollisionEvent event) {
        PhysicsCollisionObject a = event.getObjectA();
        PhysicsCollisionObject b = event.getObjectB();

        PhysicsRigidBody ra = (a instanceof PhysicsRigidBody) ? (PhysicsRigidBody) a : null;
        PhysicsRigidBody rb = (b instanceof PhysicsRigidBody) ? (PhysicsRigidBody) b : null;

        PlayerRigidBody pra = unwrap(ra);
        PlayerRigidBody prb = unwrap(rb);
        boolean aIsPlayer = pra != null;
        boolean bIsPlayer = prb != null;
        if (!aIsPlayer && !bIsPlayer) return;

        // Only act on resolving contacts to avoid noise/chatter
        if (event.getAppliedImpulse() <= 0f) return;

        Vector3f nWorld = event.getNormalWorldOnB(null); // world normal at contact

        if (aIsPlayer) {
            Accum acc = tickState.computeIfAbsent(ra, k -> new Accum());
            consider(acc, /*into player*/ nWorld, rb);
        }
        if (bIsPlayer) {
            Accum acc = tickState.computeIfAbsent(rb, k -> new Accum());
            consider(acc, /*into player*/ nWorld.negate(), ra);
        }
    }

    private void consider(Accum acc, Vector3f normalIntoPlayer, PhysicsRigidBody otherRigid) {
        float ny = normalIntoPlayer.y;
        boolean looksGround = ny > 0.5f;

        float score = (looksGround ? 2f : 0.5f) * Math.max(0f, ny);
        if (score > acc.bestScore) {
            acc.bestScore = score;
            acc.onCollider = looksGround;

            // Tiny upward depenetration nudge (tune to taste)
            acc.upNudge = looksGround ? Math.max(acc.upNudge, 0.03f + 0.07f * Math.min(1f, ny)) : 0f;

            if (otherRigid != null) {
                otherRigid.getLinearVelocity(acc.supportVel);
                acc.supportVel.y = 0f; // horizontal platform velocity only
            } else {
                acc.supportVel.set(0, 0, 0);
            }
        }
    }

    private void publishToPlayers() {
        for (PlayerRigidBody prb : space.getRigidBodiesByClass(PlayerRigidBody.class)) {
            var elem = prb.getElement();
            if (!(elem instanceof PlayerPhysicsElement ppe)) continue;
            if (!(elem instanceof Player player)) continue;

            PhysicsRigidBody rb = prb.getElement().getRigidBody();
            if (rb == null) continue;

            Accum a = tickState.get(rb);
            boolean onCollider = a != null && a.onCollider;
            Vec3 support = (a == null) ? Vec3.ZERO : new Vec3(a.supportVel.x, a.supportVel.y, a.supportVel.z);

            // tell the mixin; its travel() cancels vanilla only when true
            ppe.setGroundContact(onCollider, support);

            // optional: small post-step nudge to keep player from sinking
            if (a != null && a.onCollider && a.upNudge > 0f) {
                var pos = player.position();
                player.setPos(pos.x, pos.y + a.upNudge, pos.z);
                player.fallDistance = 0f;
            }
        }
    }

    private PlayerRigidBody unwrap(PhysicsRigidBody rb) {
        if (rb == null) return null;
        Object uo = rb.getUserObject();
        return (uo instanceof PlayerRigidBody prb) ? prb : null;
    }
}