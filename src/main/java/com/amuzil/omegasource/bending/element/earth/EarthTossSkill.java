package com.amuzil.omegasource.bending.element.earth;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.skill.SkillCategory;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.KnockbackTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.omegasource.bending.BendingEffect;
import com.amuzil.omegasource.bending.BendingSelection;
import com.amuzil.omegasource.bending.element.Elements;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.utils.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.GameTickForceApplier;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.mod.util.RelocationUtilKt;

import static com.amuzil.omegasource.bending.form.BendingForms.BLOCK;


public class EarthTossSkill extends BendingEffect {

    ServerShip ship;

    public EarthTossSkill() {
        super(Avatar.MOD_ID, "earth_toss", Elements.EARTH);
        addTrait(new KnockbackTrait(1.5f, Constants.KNOCKBACK));
        addTrait(new SizeTrait(1.0f, Constants.SIZE));

        this.startPaths = SkillPathBuilder.getInstance()
                .complex(new ActiveForm(BLOCK, true))
                .build();

        this.runPaths = SkillPathBuilder.getInstance()
                .complex(new ActiveForm(BLOCK, true))
                .build();
    }

    @Override
    public SkillCategory getCategory() {
        return Elements.EARTH;
    }

    @Override
    public void start(LivingEntity entity) {
        super.start(entity);
        ship = null;

        Bender bender = (Bender) Bender.getBender(entity);
        if (!entity.level().isClientSide()) {
            ServerLevel level = (ServerLevel) entity.level();
            if (bender.getSelection().target == BendingSelection.Target.BLOCK
                    && !bender.getSelection().blockPositions.isEmpty()
                    && !VSGameUtilsKt.isBlockInShipyard(level, bender.blockPos)) {
                System.out.println("Starting Earth Toss!");
                String dimensionId = VSGameUtilsKt.getDimensionId(level);
                ship = VSGameUtilsKt.getShipObjectWorld(level).createNewShipAtBlock(VectorConversionsMCKt.toJOML(bender.blockPos), false, 1, dimensionId);
                BlockPos centerPos = VectorConversionsMCKt.toBlockPos(ship.getChunkClaim().getCenterBlockCoordinates(VSGameUtilsKt.getYRange(level),new Vector3i()));
                RelocationUtilKt.relocateBlock(level, bender.blockPos, centerPos, true, ship, Rotation.NONE);
                System.out.println("Ship created: " + ship.getId() + " " + ship.getChunkClaim());
            }
        }

        if (bender != null) {
            SkillData data = bender.getSkillData(this);
            data.setState(SkillState.RUN);
            resetCooldown(data);
        }
    }

    @Override
    public void run(LivingEntity entity) {
        super.run(entity);
        if (!entity.level().isClientSide()) {
            Bender bender = (Bender) Bender.getBender(entity);
            ServerLevel level = (ServerLevel) entity.level();

            if (VSGameUtilsKt.isBlockInShipyard(level, bender.blockPos)) {
                LoadedServerShip serverShip = VSGameUtilsKt.getShipObjectManagingPos(level, bender.blockPos);
                if (serverShip != null) {
                    System.out.println("Running Earth Toss! " + bender.blockPos + " " + serverShip.getId());

                    GameTickForceApplier gtfa = serverShip.getAttachment(GameTickForceApplier.class);
                    if (gtfa != null) {
                        Vec3 vec3 = entity.getLookAngle().normalize().multiply(10000, 10000, 10000);
                        Vector3d v3d = VectorConversionsMCKt.toJOML(vec3);
                        gtfa.applyInvariantForce(v3d);
                    }
                }
            }
        }
    }
}
