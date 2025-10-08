package com.amuzil.omegasource.utils.ship;

import com.amuzil.omegasource.bending.BendingSelection;
import com.amuzil.omegasource.capability.Bender;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
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

//        Vector3d pivot = getPivot(bender);
        Vector3d pivot = getPivotRight(bender);

        ShipTeleportData shipTeleportData = new ShipTeleportDataImpl(
                pivot,
                relativeVector3.toEulerRotationFromMCEntity(0, bender.getEntity().getYRot()),
                c, c, VSGameUtilsKt.getDimensionId(level), null, null);
        ValkyrienSkiesMod.getVsCore().teleportShip(serverShipWorld, serverShip, shipTeleportData);
    }

    private static void getPivot(Bender bender) {
        // Calculate the pivot point in front of the player
        Vec3[] pose = new Vec3[]{bender.getEntity().position(), bender.getEntity().getLookAngle()};
        pose[1] = pose[1].scale((1.7)).add((0), (bender.getEntity().getEyeHeight()), (0));
        Vec3 newPos = pose[1].add(pose[0]);
        Vector3d pivot = new Vector3d(newPos.x, newPos.y, newPos.z);
    }

    private static @NotNull Vector3d getPivotRight(Bender bender) {
        // Calculate the pivot point in front & slightly to the right of the player
        Vec3 eyePos = bender.getEntity().position().add(0, bender.getEntity().getEyeHeight(), 0);
        Vec3 look = bender.getEntity().getLookAngle();
        // --- Compute a right vector (perpendicular to look & up) ---
        Vec3 up = new Vec3(0, 1, 0);
        Vec3 right = look.cross(up).normalize();
        // --- Move the point forward and slightly to the right ---
        double forwardDistance = 1.7; // how far in front
        double rightOffset = 0.7;     // how far to the right (tweak as needed)
        Vec3 newPos = eyePos.add(look.scale(forwardDistance)).add(right.scale(rightOffset));
        // --- Convert to Vector3d for your pivot system ---
        Vector3d pivot = new Vector3d(newPos.x, newPos.y, newPos.z);
        return pivot;
    }

    public static void tossBlock(LivingEntity entity, EarthController earthController, LoadedServerShip ship) {
        double mass = ship.getInertiaData().getMass();
        Vec3 vec3 = entity.getLookAngle().normalize()
                .multiply(900*mass, 600*mass, 900*mass);
        Vector3d force = VectorConversionsMCKt.toJOML(vec3);
        earthController.applyInvariantForce(force);
    }

    public static void raiseBlock(LoadedServerShip ship, EarthController earthController) {
        double gravity = 10; // Acceleration due to gravity
        double mass = ship.getInertiaData().getMass(); // Mass of the ship
//        System.out.println("Mass: " + mass);
        double requiredForce = (gravity * mass) * 10; // Force needed to counteract gravity
        Vector3d force = new Vector3d(0, requiredForce, 0);
        earthController.applyInvariantForce(force);
    }

    public static void assembleEarthShip(Bender bender) {
        // TODO - Make way to customize ship assembly
        ServerLevel level = (ServerLevel) bender.getEntity().level();
        BlockPos blockPos = bender.getSelection().blockPos();
        if (blockPos != null) {
            BlockState blockState = level.getBlockState(blockPos);
            if (bender.getSelection().target() == BendingSelection.Target.BLOCK
                    && !VSGameUtilsKt.isBlockInShipyard(level, blockPos)
                    && !blockState.isAir()) {
                String dimensionId = VSGameUtilsKt.getDimensionId(level);
                ServerShip ship = VSGameUtilsKt.getShipObjectWorld(level).createNewShipAtBlock(VectorConversionsMCKt.toJOML(blockPos), false, 1, dimensionId);
                BlockPos centerPos = VectorConversionsMCKt.toBlockPos(ship.getChunkClaim().getCenterBlockCoordinates(VSGameUtilsKt.getYRange(level), new Vector3i()));
//                RelocationUtilKt.relocateBlock(level, blockPos, centerPos, true, ship, Rotation.NONE);
                bender.getSelection().addOriginalBlocks(ship.getId(), blockPos, blockState);
                RelocationUtilKt.relocateBlock(level, blockPos, centerPos, true, ship, Rotation.NONE);
                Vector3dc shipyardPos = ship.getTransform().getPositionInShip();
                BlockPos shipyardBlockPos = BlockPos.containing(VectorConversionsMCKt.toMinecraft(shipyardPos));
                bender.getSelection().setBlockPos(shipyardBlockPos); // Important: Update BlockPos to the shipyard position
            }
        }
    }
}
