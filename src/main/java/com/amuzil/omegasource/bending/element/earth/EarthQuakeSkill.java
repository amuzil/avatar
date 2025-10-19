package com.amuzil.omegasource.bending.element.earth;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.form.FormPath;
import com.amuzil.omegasource.api.magus.skill.data.SkillData;
import com.amuzil.omegasource.api.magus.skill.data.SkillPathBuilder;
import com.amuzil.omegasource.api.magus.skill.event.SkillTickEvent;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.KnockbackTrait;
import com.amuzil.omegasource.api.magus.skill.traits.skilltraits.SizeTrait;
import com.amuzil.omegasource.bending.skill.EarthSkill;
import com.amuzil.omegasource.capability.Bender;
import com.amuzil.omegasource.utils.Constants;
import com.amuzil.omegasource.utils.ship.EarthController;
import com.amuzil.omegasource.utils.ship.OriginalBlock;
import com.amuzil.omegasource.utils.ship.OriginalBlocks;
import com.amuzil.omegasource.utils.ship.VSUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.core.jmx.Server;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.core.api.ships.Ship;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;
import org.valkyrienskies.mod.util.RelocationUtilKt;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.amuzil.omegasource.bending.form.BendingForms.*;


public class EarthQuakeSkill extends EarthSkill {
    private static final Logger log = LoggerFactory.getLogger(EarthQuakeSkill.class);
    OriginalBlocks originalLevelState = new OriginalBlocks();
    Map<BlockPos, ServerShip> quakingBlocks = new HashMap<>();
    private BlockPos epicenter;
    private int currentQuakeDistance = 0;
    private int ticksPassed = 0;
    private int ticksStopped = 0;
    private Random random = new Random();
    private Queue<Map.Entry<BlockPos, BlockState>> queue = new ArrayDeque<>();

    public EarthQuakeSkill() {
        super(Avatar.MOD_ID, "earth_quake");
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 1.5f));
        addTrait(new SizeTrait(Constants.SIZE, 1.0f));

        this.startPaths = SkillPathBuilder.getInstance()
//                .simple(new ActiveForm(ARC, true))
                .complex(new ActiveForm(RAISE, true))
                .complex(new ActiveForm(LOWER, true))
                .build();

        this.stopPaths = SkillPathBuilder.getInstance()
                .simple(new ActiveForm(BLOCK, true))
                .build();
    }

    @Override
    public boolean shouldStart(Bender bender, FormPath formPath) {
        return formPath.complex().hashCode() == startPaths().complex().hashCode();
    }

    @Override
    public boolean shouldRun(Bender bender, FormPath formPath) {
        return bender.getSkillData(this).getSkillState().equals(SkillState.RUN);
    }

    @Override
    public boolean shouldStop(Bender bender, FormPath formPath) {
        return formPath.simple().hashCode() == stopPaths().simple().hashCode();
    }

    @Override
    public void start(Bender bender) {
        super.start(bender);
        System.out.println("EarthQuakeSkill started on server");
        int numRings = 3;
        // get player position casting as epicentre.
        epicenter = bender.getEntity().blockPosition().below();
        skillData.setSkillState(SkillState.RUN);
    }

    @Override
    public void run(Bender bender) {
        super.run(bender);
        ticksPassed++;
        Level level = bender.getEntity().level();
        ServerLevel serverLevel = (ServerLevel) level;
        if(currentQuakeDistance < 3 && ticksPassed >= 20) // todo - configurable/skilldata based
        {
            currentQuakeDistance++;
            ticksPassed -= 20;

            LinkedHashMap<BlockPos, BlockState> currentRing = collectRing(
                    level, epicenter, currentQuakeDistance,
                    pos -> !level.getBlockState(pos).isAir(),
                    true
            );
            queue.addAll(currentRing.entrySet());
        }
        if(!queue.isEmpty() && ticksPassed % 2 == 0) {
            String dimensionId = VSGameUtilsKt.getDimensionId(level);
            Map.Entry<BlockPos, BlockState> entry = queue.remove();
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();

            originalLevelState.add(new OriginalBlock(pos, state));

            ServerShip ship = VSUtils.createNewShipAtBlock(serverLevel, VectorConversionsMCKt.toJOML(pos), false, 1, dimensionId);
            BlockPos centerPos = VectorConversionsMCKt.toBlockPos(ship.getChunkClaim().getCenterBlockCoordinates(VSGameUtilsKt.getYRange(level), new Vector3i()));
            System.out.println(pos.toString());
            VSUtils.relocateBlock(level, pos, centerPos, true, ship, Rotation.NONE);

            quakingBlocks.put(pos, ship);
        }
//        for (int i = 0; i < quakingBlocks.size(); i++) {
//            BlockPos pos = (BlockPos) quakingBlocks.keySet().toArray()[i];
////            ServerShip quakingBlock = quakingBlocks.get(pos);
//            LoadedServerShip serverShip = VSGameUtilsKt.getShipObjectManagingPos(serverLevel, pos);
//            if (serverShip != null) {
//                EarthController earthController = EarthController.getOrCreate(serverShip, bender);
//                earthController.applyInvariantForce(new Vector3d(0, random.nextInt(5), 0));
//            }
//
//        }

        System.out.println("EarthQuakeSkill running on server");
    }


    public static LinkedHashMap<BlockPos, BlockState> collectRing(LevelReader level,
                                                                         BlockPos epicentre,
                                                                         int radius,
                                                                         Predicate<BlockPos> includePos,
                                                                         boolean skipUnloaded) {
        final int cx = epicentre.getX();
        final int cy = epicentre.getY();
        final int cz = epicentre.getZ();

        // Radius >= 1: walk the perimeter of the square ring without duplicates
        int r = radius;
        LinkedHashMap<BlockPos, BlockState> ring = new LinkedHashMap<>();

        // Top edge (z = cz - r), x from -r to +r
        for (int dx = -r; dx <= r; dx++) {
            BlockPos pos = new BlockPos(cx + dx, cy, cz - r);
            sample(level, pos, includePos, skipUnloaded, ring);
        }

        // Bottom edge (z = cz + r), x from -r to +r
        for (int dx = -r; dx <= r; dx++) {
            BlockPos pos = new BlockPos(cx + dx, cy, cz + r);
            sample(level, pos, includePos, skipUnloaded, ring);
        }

        // Left edge (x = cx - r), z from (-r + 1) to (r - 1) to avoid corner duplicates
        for (int dz = -r + 1; dz <= r - 1; dz++) {
            BlockPos pos = new BlockPos(cx - r, cy, cz + dz);
            sample(level, pos, includePos, skipUnloaded, ring);
        }

        // Right edge (x = cx + r), z from (-r + 1) to (r - 1)
        for (int dz = -r + 1; dz <= r - 1; dz++) {
            BlockPos pos = new BlockPos(cx + r, cy, cz + dz);
            sample(level, pos, includePos, skipUnloaded, ring);
        }

        return ring;
    }

    private static void sample(LevelReader level,
                               BlockPos pos,
                               Predicate<BlockPos> includePos,
                               boolean skipUnloaded,
                               LinkedHashMap<BlockPos, BlockState> sink) {
        if (skipUnloaded && !level.hasChunkAt(pos)) {
            return; // don’t force-load chunks
        }
        if (!includePos.test(pos)) {
            return;
        }
        BlockState state = level.getBlockState(pos);
        sink.put(pos, state);
    }

    protected Consumer<SkillTickEvent> cleanupRunnable = (skillTickEvent) -> {
        System.out.println("EarthQuakeSkill ticking stop on server");
        ticksStopped++;
        if(ticksStopped >= 400) {
            originalLevelState.restore((ServerLevel) bender.getEntity().level());
            super.stop(bender);
            System.out.println("EarthQuakeSkill stopped on server");
            this.stopCleanup();
        }
    };

    public void startCleanup() {
        this.listen(SkillTickEvent.class, cleanupRunnable);
    }

    public void stopCleanup() {
        this.hush(cleanupRunnable);
    }

    @Override
    public void stop(Bender bender) {
        System.out.println("EarthQuakeSkill requested stop on server");
        this.startCleanup(); // defer skill stop and removal till cleaned up blocks
    }
}
