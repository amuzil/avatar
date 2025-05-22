package com.amuzil.omegasource.bending.element.earth;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.KnockbackTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.omegasource.bending.BendingSelection;
import com.amuzil.omegasource.bending.skill.EarthSkill;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.utils.Constants;
import com.amuzil.omegasource.utils.ship.EarthController;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Rotation;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.mod.util.RelocationUtilKt;

import static com.amuzil.omegasource.bending.form.BendingForms.BLOCK;


public class EarthBlockSkill extends EarthSkill {

    public EarthBlockSkill() {
        super(Avatar.MOD_ID, "earth_block");
        addTrait(new KnockbackTrait(1.5f, Constants.KNOCKBACK));
        addTrait(new SizeTrait(1.0f, Constants.SIZE));

        this.startPaths = SkillPathBuilder.getInstance()
                .simple(new ActiveForm(BLOCK, true))
                .build();

        this.runPaths = SkillPathBuilder.getInstance()
                .simple(new ActiveForm(BLOCK, true))
                .build();
    }

    @Override
    public boolean shouldStart(LivingEntity entity, FormPath formPath) {
        return formPath.simple().hashCode() == getStartPaths().simple().hashCode();
    }

    @Override
    public boolean shouldRun(LivingEntity entity, FormPath formPath) {
        return formPath.simple().hashCode() == getRunPaths().simple().hashCode();
    }

    @Override
    public void start(LivingEntity entity) {
        super.start(entity);

        Bender bender = (Bender) Bender.getBender(entity);
        if (!entity.level().isClientSide()) {
            ServerLevel level = (ServerLevel) entity.level();
            if (bender.getSelection().target == BendingSelection.Target.BLOCK
                    && !bender.getSelection().blockPositions.isEmpty()
                    && !VSGameUtilsKt.isBlockInShipyard(level, bender.blockPos)
                    && !level.getBlockState(bender.blockPos).isAir()) {
                String dimensionId = VSGameUtilsKt.getDimensionId(level);
                ServerShip ship = VSGameUtilsKt.getShipObjectWorld(level).createNewShipAtBlock(VectorConversionsMCKt.toJOML(bender.blockPos), false, 1, dimensionId);
                BlockPos centerPos = VectorConversionsMCKt.toBlockPos(ship.getChunkClaim().getCenterBlockCoordinates(VSGameUtilsKt.getYRange(level),new Vector3i()));
                RelocationUtilKt.relocateBlock(level, bender.blockPos, centerPos, true, ship, Rotation.NONE);
                Vector3dc shipyardPos = ship.getTransform().getPositionInShip();
                BlockPos shipyardBlockPos = BlockPos.containing(VectorConversionsMCKt.toMinecraft(shipyardPos));
                bender.setBlockPos(shipyardBlockPos);
                if (ship != null) {
                    System.out.println("Ship created: " + ship.getId() + " " + centerPos);
                }
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
                    EarthController gtfa = EarthController.getOrCreate(serverShip, bender.getEntity());
                    if (gtfa != null) {
                        hoverBlock(serverShip, gtfa);
                    }
                }
            }
        }
    }

    private static void hoverBlock(LoadedServerShip ship, EarthController gtfa) {
        double gravity = 10; // Acceleration due to gravity
        double mass = ship.getInertiaData().getMass(); // Mass of the ship
//        System.out.println("Mass: " + mass);
        double requiredForce = (gravity * mass) * 10; // Force needed to counteract gravity
        Vector3d v3d2 = new Vector3d(0, requiredForce, 0);
        gtfa.applyInvariantForce(v3d2);
    }
}
