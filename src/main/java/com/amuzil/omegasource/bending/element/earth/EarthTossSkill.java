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
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.GameTickForceApplier;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.mod.util.RelocationUtilKt;

import static com.amuzil.omegasource.bending.form.BendingForms.BLOCK;


public class EarthTossSkill extends BendingEffect {

    public EarthTossSkill() {
        super(Avatar.MOD_ID, "earth_toss", Elements.EARTH);
        addTrait(new KnockbackTrait(1.5f, Constants.KNOCKBACK));
        addTrait(new SizeTrait(1.0f, Constants.SIZE));

        this.startPaths = SkillPathBuilder.getInstance()
                .complex(new ActiveForm(BLOCK, true))
                .build();

        this.runPaths = SkillPathBuilder.getInstance()
                .simple(new ActiveForm(BLOCK, true))
                .build();
    }

    @Override
    public SkillCategory getCategory() {
        return Elements.EARTH;
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
                    GameTickForceApplier gtfa = serverShip.getAttachment(GameTickForceApplier.class);
                    if (gtfa != null) {
//                        hoverBlock(serverShip, gtfa);
                        tossBlock(entity, gtfa);
                    }
                }
            }
        }
    }

    private static void hoverBlock(LoadedServerShip ship, GameTickForceApplier gtfa) {
        double gravity = 10; // Acceleration due to gravity
        double mass = ship.getInertiaData().getMass(); // Mass of the ship
        double requiredForce = (gravity * mass) + 5000.0D; // Force needed to counteract gravity
        Vector3d v3d2 = new Vector3d(0, requiredForce, 0);
        System.out.println("Levitating force 2: " + v3d2 + " " + requiredForce);
        gtfa.applyInvariantForce(v3d2);
    }

    private static void tossBlock(LivingEntity entity, GameTickForceApplier gtfa) {
        Vec3 vec3 = entity.getLookAngle().normalize()
                .add(0, 1, 0)
                .multiply(10000, 10000, 10000);
        System.out.println("Applying force: " + vec3);
        Vector3d v3d = VectorConversionsMCKt.toJOML(vec3);
        gtfa.applyInvariantForce(v3d);
    }
}
