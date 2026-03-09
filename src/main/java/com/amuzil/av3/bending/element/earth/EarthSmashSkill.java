package com.amuzil.av3.bending.element.earth;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.skill.EarthSkill;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.entity.api.ICollisionModule;
import com.amuzil.av3.entity.api.modules.ModuleRegistry;
import com.amuzil.av3.entity.api.modules.collision.EarthCollisionModule;
import com.amuzil.av3.entity.construct.AvatarRigidBlock;
import com.amuzil.av3.network.AvatarNetwork;
import com.amuzil.av3.network.packets.client.TriggerFXPacket;
import com.amuzil.av3.utils.Constants;
import com.amuzil.av3.utils.bending.BendingMaterial;
import com.amuzil.av3.utils.bending.OriginalBlocks;
import com.amuzil.av3.utils.bending.RigidBlockFactory;
import com.amuzil.magus.skill.data.SkillPathBuilder;
import com.amuzil.magus.skill.traits.skilltraits.*;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.*;

import static com.amuzil.av3.bending.form.BendingForms.EXPAND;
import static com.amuzil.av3.utils.bending.SkillHelper.canEarthBend;


public class EarthSmashSkill extends EarthSkill {
    OriginalBlocks originalBlocks = new OriginalBlocks();
    private final float  maxRadius       = 6.0f;      // how far the wave expands (blocks)
    private final float  rippleSpeed     = 0.6f;      // blocks per tick the wave-front advances
    private final int    ringCount       = 8;         // rigid blocks spawned per ring
    private final int    blockLifetime   = 800;       // ms each rigid block lives
    private final float  blockSize       = 1.0f;      // must match renderer's natural 1x1x1 block scale
    private final float  impulseStrength = 6.0f;     // outward physics impulse magnitude
    private final int    fxInterval      = 3;         // fire TriggerFXPacket once every N blocks spawned
    private final String fxKey           = "earth_block";

    // --- Runtime state (reset each activation) ---
    private int  tickCount    = 0;
    private int  fxBlockCount = 0; // counts spawned blocks to throttle FX packets
    private Vec3 smashOrigin  = null;
    private final List<AvatarRigidBlock> activeRippleBlocks = new ArrayList<>();

    public EarthSmashSkill() {
        super(Avatar.MOD_ID, "earth_smash");
        addTrait(new StringTrait(Constants.FX, "earth_block"));
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 1.5f));
        addTrait(new SizeTrait(Constants.SIZE, 1.0f));
        addTrait(new DamageTrait(Constants.DAMAGE, 6.5f));
        addTrait(new SpeedTrait(Constants.SPEED, 2.5d));
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 1.5f));

        startPaths = SkillPathBuilder.getInstance()
                .add(EXPAND)
                .build();

        stopPaths = SkillPathBuilder.getInstance().build();
    }

    @Override
    public void start(Bender bender) {
        super.startRun();
        LivingEntity entity = bender.getEntity();
        if (!canEarthBend(entity)) return; // Can't earth bend if too far from ground
        ServerLevel level = (ServerLevel) entity.level();
        BlockPos blockPos = entity.blockPosition().below();
        BlockState blockState = level.getBlockState(blockPos);
        if (!BendingMaterial.isBendable(blockState, element())) return;
        ResourceLocation id = Avatar.id(skillData.getTrait(Constants.FX, StringTrait.class).getInfo());

        Set<UUID> entityIds = bender.getSelection().entityIds();
        if (!entityIds.isEmpty()) {
            for (UUID entityId: entityIds) {
                if (level.getEntity(entityId) instanceof AvatarRigidBlock rigidBlock) {
                    AvatarNetwork.sendToClient(new TriggerFXPacket(id, rigidBlock.getId()), (ServerPlayer) entity);
                    rigidBlock.remove(Entity.RemovalReason.KILLED);
                }
            }
            bender.formPath.clear();
            bender.resetSelection();
            skillData.setSkillState(SkillState.IDLE);
            return;
        }

        // Store smash origin at ground level and reset wave state
        smashOrigin = new Vec3(entity.getX(), blockPos.getY() + 0.5, entity.getZ());
        tickCount    = 0;
        fxBlockCount = 0;
        activeRippleBlocks.clear();
    }

    @Override
    public void run(Bender bender) {
        super.run(bender);
        LivingEntity entity = bender.getEntity();
        ServerLevel level = (ServerLevel) entity.level();
        if (smashOrigin == null) {
            skillData.setSkillState(SkillState.IDLE);
            return;
        }

        tickCount++;
        float currentRadius = tickCount * rippleSpeed;

        if (currentRadius > maxRadius) {
            skillData.setSkillState(SkillState.IDLE);
            smashOrigin = null;
            activeRippleBlocks.clear();
            return;
        }

        spawnRippleRing(level, entity, currentRadius);
    }

    /**
     * Spawns a ring of {@link #ringCount} AvatarRigidBlocks at {@code radius} from
     * {@link #smashOrigin}. Each replaces a vanilla ground block and is launched
     * outward with a physics impulse to simulate an earth shockwave.
     */
    private void spawnRippleRing(ServerLevel level, LivingEntity owner, float radius) {
        ResourceLocation fxId = Avatar.id(fxKey);

        for (int i = 0; i < ringCount; i++) {
            double angle = (2.0 * Math.PI / ringCount) * i;
            double dx = Math.cos(angle) * radius;
            double dz = Math.sin(angle) * radius;

            Vec3 spawnPos = smashOrigin.add(dx, 0.0, dz);

            // Only check the exact surface block at smash origin Y — no downward scan,
            // so ripples stay exactly 1 block deep and never pull from below the surface.
            BlockPos groundPos = BlockPos.containing(spawnPos);
            BlockState groundState = level.getBlockState(groundPos);
            if (!BendingMaterial.isBendable(groundState, element())) continue;

            // Remove the vanilla block — the rigid block replaces it visually
            level.removeBlock(groundPos, false);

            Vec3 blockSpawnPos = new Vec3(
                    groundPos.getX() + 0.5,
                    groundPos.getY() + 0.5,
                    groundPos.getZ() + 0.5
            );

            AvatarRigidBlock rippleBlock = RigidBlockFactory.createBlock(level, groundState, owner, blockLifetime, blockSize);

            rippleBlock.setElement(element());
            rippleBlock.setFX(fxKey);
            rippleBlock.setPos(blockSpawnPos.x, blockSpawnPos.y, blockSpawnPos.z);
            rippleBlock.init();
             rippleBlock.addTraits(skillData.getTrait(Constants.DAMAGE, DamageTrait.class));
             rippleBlock.addTraits(new SizeTrait(Constants.SIZE, (float)  rippleBlock.getSize().getSize()));
             rippleBlock.addTraits(new CollisionTrait(Constants.COLLISION_TYPE, "Blaze", "Fireball", "AbstractArrow", "FireProjectile"));
             rippleBlock.addCollisionModule((ICollisionModule) ModuleRegistry.create(EarthCollisionModule.id));

            level.addFreshEntity(rippleBlock);
            activeRippleBlocks.add(rippleBlock);

            // Launch outward from the smash origin with a slight upward arc
            Vec3 outward = new Vec3(dx, 0.3, dz).normalize();
            rippleBlock.shoot(blockSpawnPos, outward, impulseStrength, 0.0);

            // Throttle FX sound — fire once every fxInterval blocks to avoid spam
            if (owner instanceof ServerPlayer serverPlayer) {
                fxBlockCount++;
                if (fxBlockCount % fxInterval == 0) {
                    AvatarNetwork.sendToClient(new TriggerFXPacket(fxId, rippleBlock.getId()), serverPlayer);
                }
            }
        }
    }
}
