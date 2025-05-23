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
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.GameTickForceApplier;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.mod.util.RelocationUtilKt;

import static com.amuzil.omegasource.bending.form.BendingForms.STRIKE;


public class EarthTossSkill extends EarthSkill {

    public EarthTossSkill() {
        super(Avatar.MOD_ID, "earth_toss");
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 1.5f));
        addTrait(new SizeTrait(Constants.SIZE, 1.0f));

        this.startPaths = SkillPathBuilder.getInstance()
                .complex(new ActiveForm(STRIKE, true))
                .build();

//        this.runPaths = SkillPathBuilder.getInstance()
//                .simple(new ActiveForm(STRIKE, true))
//                .build();
    }

    @Override
    public boolean shouldStart(LivingEntity entity, FormPath formPath) {
        return super.shouldStart(entity, formPath);
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
                BlockPos selectedCentre = null;
                int deltaX, deltaY, deltaZ;
                for (BlockPos selectedBlock :
                        bender.getSelection().blockPositions) {
                    if(selectedCentre == null) {
                        selectedCentre = selectedBlock;
                        RelocationUtilKt.relocateBlock(level, selectedCentre, centerPos, true, ship, Rotation.NONE);
                    } else {
                        deltaX = selectedCentre.getX() - selectedBlock.getX();
                        deltaY = selectedCentre.getY() - selectedBlock.getY();
                        deltaZ = selectedCentre.getZ() - selectedBlock.getZ();

                        RelocationUtilKt.relocateBlock(level, selectedBlock, centerPos.offset(deltaX, deltaY, deltaZ), true, ship, Rotation.NONE);
                    }
                }
                Vector3dc shipyardPos = ship.getTransform().getPositionInShip();
                BlockPos shipyardBlockPos = BlockPos.containing(VectorConversionsMCKt.toMinecraft(shipyardPos));
                bender.setBlockPos(shipyardBlockPos);
                if (ship != null) {
                    System.out.println("Ship created: " + ship.getId() + " " + bender.blockPos);
                }
            }
            if (VSGameUtilsKt.isBlockInShipyard(level, bender.blockPos)) {
                LoadedServerShip serverShip = VSGameUtilsKt.getShipObjectManagingPos(level, bender.blockPos);
                if (serverShip != null) {
                    GameTickForceApplier gtfa = serverShip.getAttachment(GameTickForceApplier.class);
                    if (gtfa != null) {
                        tossBlock(entity, gtfa);
                    }
                }
            }
        }

        if (bender != null) {
            SkillData data = bender.getSkillData(this);
            data.setSkillState(SkillState.IDLE);
        }
    }

    private static void tossBlock(LivingEntity entity, GameTickForceApplier gtfa) {
        Vec3 vec3 = entity.getLookAngle().normalize()
                .multiply(75000, 45000, 75000);
        Vector3d v3d = VectorConversionsMCKt.toJOML(vec3);
        gtfa.applyInvariantForce(v3d);
    }
}