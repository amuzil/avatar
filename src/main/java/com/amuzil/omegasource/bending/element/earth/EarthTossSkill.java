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
import net.minecraft.world.level.block.Rotation;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.mod.util.RelocationUtilKt;

import static com.amuzil.omegasource.bending.form.BendingForms.STRIKE;
import static com.amuzil.omegasource.utils.ship.VSUtils.tossBlock;


public class EarthTossSkill extends EarthSkill {

    public EarthTossSkill() {
        super(Avatar.MOD_ID, "earth_toss");
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 1.5f));
        addTrait(new SizeTrait(Constants.SIZE, 1.0f));

        this.startPaths = SkillPathBuilder.getInstance()
                .simple(new ActiveForm(STRIKE, true))
                .build();
    }

    @Override
    public boolean shouldStart(Bender bender, FormPath formPath) {
        return formPath.simple().hashCode() == startPaths().simple().hashCode();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);

        if (!bender.getEntity().level().isClientSide()) {
            ServerLevel level = (ServerLevel) bender.getEntity().level();
            BlockPos blockPos = bender.getSelection().blockPos();
            if (bender.getSelection().target() == BendingSelection.Target.BLOCK
                    && blockPos != null
                    && !VSGameUtilsKt.isBlockInShipyard(level, blockPos)
                    && !level.getBlockState(blockPos).isAir()) {
                String dimensionId = VSGameUtilsKt.getDimensionId(level);
                ServerShip ship = VSGameUtilsKt.getShipObjectWorld(level).createNewShipAtBlock(VectorConversionsMCKt.toJOML(blockPos), false, 1, dimensionId);
                BlockPos centerPos = VectorConversionsMCKt.toBlockPos(ship.getChunkClaim().getCenterBlockCoordinates(VSGameUtilsKt.getYRange(level),new Vector3i()));
                RelocationUtilKt.relocateBlock(level, blockPos, centerPos, true, ship, Rotation.NONE);
//                BlockPos selectedCentre = null;
//                int deltaX, deltaY, deltaZ;
//                for (BlockPos selectedBlock: bender.getSelection().blockPos()) {
//                    if(selectedCentre == null) {
//                        selectedCentre = selectedBlock;
//                        RelocationUtilKt.relocateBlock(level, selectedCentre, centerPos, true, ship, Rotation.NONE);
//                    } else {
//                        deltaX = selectedCentre.getX() - selectedBlock.getX();
//                        deltaY = selectedCentre.getY() - selectedBlock.getY();
//                        deltaZ = selectedCentre.getZ() - selectedBlock.getZ();
//
//                        RelocationUtilKt.relocateBlock(level, selectedBlock, centerPos.offset(deltaX, deltaY, deltaZ), true, ship, Rotation.NONE);
//                    }
//                }
                Vector3dc shipyardPos = ship.getTransform().getPositionInShip();
                BlockPos shipyardBlockPos = BlockPos.containing(VectorConversionsMCKt.toMinecraft(shipyardPos));
                bender.getSelection().setBlockPos(shipyardBlockPos);
//                if (ship != null)
//                    System.out.println("Ship created: " + ship.getId() + " " + bender.getSelection().blockPos());
            }
            if (blockPos != null && VSGameUtilsKt.isBlockInShipyard(level, blockPos)) {
                LoadedServerShip serverShip = VSGameUtilsKt.getShipObjectManagingPos(level, blockPos);
                if (serverShip != null) {
                    EarthController earthController = serverShip.getAttachment(EarthController.class);
                    if (earthController != null) {
                        tossBlock(bender.getEntity(), earthController, serverShip);
                    }
                }
            }
        }

        SkillData data = bender.getSkillData(this);
        data.setSkillState(SkillState.IDLE);
    }
}