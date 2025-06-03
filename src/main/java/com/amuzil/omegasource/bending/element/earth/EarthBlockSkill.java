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
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.Vector3i;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.world.ServerShipWorld;
import org.valkyrienskies.core.apigame.ShipTeleportData;
import org.valkyrienskies.core.impl.game.ShipTeleportDataImpl;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.ValkyrienSkiesMod;
import org.valkyrienskies.mod.common.command.RelativeValue;
import org.valkyrienskies.mod.common.command.RelativeVector3;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.mod.util.RelocationUtilKt;

import static com.amuzil.omegasource.bending.form.BendingForms.BLOCK;


public class EarthBlockSkill extends EarthSkill {

    public EarthBlockSkill() {
        super(Avatar.MOD_ID, "earth_block");
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 1.5f));
        addTrait(new SizeTrait(Constants.SIZE, 1.0f));

        this.startPaths = SkillPathBuilder.getInstance()
                .simple(new ActiveForm(BLOCK, true))
                .build();

        this.runPaths = SkillPathBuilder.getInstance()
                .simple(new ActiveForm(BLOCK, true))
                .build();

        this.stopPaths = SkillPathBuilder.getInstance()
                .build();
    }

    @Override
    public boolean shouldStart(Bender bender, FormPath formPath) {
        return formPath.simple().hashCode() == getStartPaths().simple().hashCode();
    }

    @Override
    public boolean shouldStop(Bender bender, FormPath formPath) {
        return formPath.simple().hashCode() == getStopPaths().simple().hashCode();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);

        if (!bender.getEntity().level().isClientSide()) {
            ServerLevel level = (ServerLevel) bender.getEntity().level();
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
            }
        }

        SkillData data = bender.getSkillData(this);
        data.setSkillState(SkillState.RUN);
    }

    @Override
    public void run(Bender bender) {
        super.run(bender);
        if (!bender.getEntity().level().isClientSide()) {
            ServerLevel level = (ServerLevel) bender.getEntity().level();
            if (VSGameUtilsKt.isBlockInShipyard(level, bender.blockPos)) {
                LoadedServerShip serverShip = VSGameUtilsKt.getShipObjectManagingPos(level, bender.blockPos);
                ServerShipWorld serverShipWorld = (ServerShipWorld) VSGameUtilsKt.getVsCore().getHooks().getCurrentShipServerWorld();
                if (serverShip != null && serverShipWorld != null) {
                    int yvar;
                    if(serverShip.getShipAABB()!=null)
                        yvar = (int) Math.ceil((double) (serverShip.getShipAABB().maxY() - serverShip.getShipAABB().minY()) / 2);
                    else
                        yvar = 1;

                    Vector3d pivot = bender.getEntity().getPosition(0).add(0, bender.getEntity().getEyeHeight() + yvar, 0).toVector3f().get(new Vector3d());
                    RelativeValue value = new RelativeValue(0, true);
                    RelativeVector3 relativeVector3 = new RelativeVector3(value, value, value);
                    Vector3d c = new Vector3d();

                    ShipTeleportData shipTeleportData = new ShipTeleportDataImpl(
                            pivot,
                            relativeVector3.toEulerRotationFromMCEntity(0, bender.getEntity().getYRot()),
                            c, c, VSGameUtilsKt.getDimensionId(level), null, null);
                    ValkyrienSkiesMod.getVsCore().teleportShip(serverShipWorld, serverShip, shipTeleportData);

//                    EarthController gtfa = EarthController.getOrCreate(serverShip, bender.getEntity());
//                    if (gtfa != null) {
//                        hoverBlock(serverShip, gtfa);
//                    }
                }
            }
        }
    }

    @Override
    public void stop(Bender bender) {
        super.stop(bender);

        if (!bender.getEntity().level().isClientSide()) {
            ServerLevel level = (ServerLevel) bender.getEntity().level();
            if (VSGameUtilsKt.isBlockInShipyard(level, bender.blockPos)) {
                LoadedServerShip serverShip = VSGameUtilsKt.getShipObjectManagingPos(level, bender.blockPos);
                if (serverShip != null) {
                    EarthController gtfa = EarthController.getOrCreate(serverShip, bender.getEntity());
                    if (gtfa != null) {
                        gtfa.tickCount.set(0);
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

    private static void raiseBlock(LoadedServerShip ship, EarthController gtfa) {
        double gravity = 10; // Acceleration due to gravity
        double mass = ship.getInertiaData().getMass(); // Mass of the ship
//        System.out.println("Mass: " + mass);
        double requiredForce = (gravity * mass) * 10; // Force needed to counteract gravity
        Vector3d v3d2 = new Vector3d(0, requiredForce, 0);
        gtfa.applyInvariantForce(v3d2);
    }
}
