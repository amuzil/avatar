package com.amuzil.omegasource.utils.ship;

import com.amuzil.omegasource.bending.BendingSelection;
import com.amuzil.omegasource.capability.Bender;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
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
import org.valkyrienskies.mod.common.util.GameTickForceApplier;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.mod.util.RelocationUtilKt;


public class VSUtils {

    public static void controlBlock(LoadedServerShip serverShip, ServerShipWorld serverShipWorld, ServerLevel level, Bender bender) {
//        int yVal;
//        if (serverShip.getShipAABB() != null)
//            yVal = (int) Math.ceil((double) (serverShip.getShipAABB().maxY() - serverShip.getShipAABB().minY()) / 2);
//        else
//            yVal = 1;
//        Vector3d pivot = bender.getEntity().position().add(0, bender.getEntity().getEyeHeight() + yVal, 0).toVector3f().get(new Vector3d());
        RelativeValue value = new RelativeValue(0, true);
        RelativeVector3 relativeVector3 = new RelativeVector3(value, value, value);
        Vector3d c = new Vector3d();

        Vec3[] pose = new Vec3[]{bender.getEntity().position(), bender.getEntity().getLookAngle()};
        pose[1] = pose[1].scale((1.7)).add((0), (bender.getEntity().getEyeHeight()), (0));
        Vec3 newPos = pose[1].add(pose[0]);
        Vector3d pivot = new Vector3d(newPos.x, newPos.y, newPos.z);

        ShipTeleportData shipTeleportData = new ShipTeleportDataImpl(
                pivot,
                relativeVector3.toEulerRotationFromMCEntity(0, bender.getEntity().getYRot()),
                c, c, VSGameUtilsKt.getDimensionId(level), null, null);
        ValkyrienSkiesMod.getVsCore().teleportShip(serverShipWorld, serverShip, shipTeleportData);
    }

    public static void tossBlock(LivingEntity entity, GameTickForceApplier gtfa, LoadedServerShip ship) {
        double mass = ship.getInertiaData().getMass();
        Vec3 vec3 = entity.getLookAngle().normalize()
                .multiply(1500*mass, 1000*mass, 1500*mass);
        Vector3d v3d = VectorConversionsMCKt.toJOML(vec3);
        gtfa.applyInvariantForce(v3d);
    }

    public static void raiseBlock(LoadedServerShip ship, EarthController gtfa) {
        double gravity = 10; // Acceleration due to gravity
        double mass = ship.getInertiaData().getMass(); // Mass of the ship
//        System.out.println("Mass: " + mass);
        double requiredForce = (gravity * mass) * 10; // Force needed to counteract gravity
        Vector3d v3d2 = new Vector3d(0, requiredForce, 0);
        gtfa.applyInvariantForce(v3d2);
    }

    public static void assembleEarthShip(Bender bender) {
        ServerLevel level = (ServerLevel) bender.getEntity().level();
        BlockPos blockPos = bender.getSelection().blockPos();
        BlockState blockState = level.getBlockState(blockPos);
        if (bender.getSelection().target() == BendingSelection.Target.BLOCK
                && blockPos != null
                && !VSGameUtilsKt.isBlockInShipyard(level, blockPos)
                && !blockState.isAir()) {
            String dimensionId = VSGameUtilsKt.getDimensionId(level);
            ServerShip ship = VSGameUtilsKt.getShipObjectWorld(level).createNewShipAtBlock(VectorConversionsMCKt.toJOML(blockPos), false, 1, dimensionId);
            BlockPos centerPos = VectorConversionsMCKt.toBlockPos(ship.getChunkClaim().getCenterBlockCoordinates(VSGameUtilsKt.getYRange(level), new Vector3i()));
//                RelocationUtilKt.relocateBlock(level, blockPos, centerPos, true, ship, Rotation.NONE);
            bender.getSelection().setOriginalBlockPositions(blockPos, blockState);
            RelocationUtilKt.relocateBlock(level, blockPos, centerPos, true, ship, Rotation.NONE);
            Vector3dc shipyardPos = ship.getTransform().getPositionInShip();
            BlockPos shipyardBlockPos = BlockPos.containing(VectorConversionsMCKt.toMinecraft(shipyardPos));
            bender.getSelection().setBlockPos(shipyardBlockPos); // Important: Update BlockPos to the shipyard position
        }
    }
}
