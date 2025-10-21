package com.amuzil.omegasource.bending.element.earth;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.api.magus.form.ActiveForm;
import com.amuzil.omegasource.api.magus.form.FormPath;
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
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.valkyrienskies.core.api.ships.LoadedServerShip;
import org.valkyrienskies.core.api.ships.ServerShip;
import org.valkyrienskies.mod.common.VSGameUtilsKt;
import org.valkyrienskies.mod.common.util.VectorConversionsMCKt;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.amuzil.omegasource.bending.form.BendingForms.*;


public class EarthQuakeSkill extends EarthSkill {
    private static final Logger log = LoggerFactory.getLogger(EarthQuakeSkill.class);
    OriginalBlocks originalLevelState = new OriginalBlocks();
    Map<BlockPos, EarthController> quakingBlocks = new HashMap<>();
    Consumer<SkillTickEvent> blockQuaker;
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
    public boolean shouldStop(Bender bender, FormPath formPath) {
        return formPath.simple().hashCode() == stopPaths().simple().hashCode();
    }

    @Override
    public void start(Bender bender) {
        System.out.println("EarthQuakeSkill started on server");
        // get player position casting as epicentre.
        epicenter = bender.getEntity().blockPosition()
                .below();
        System.out.println("Epicenter(start): " + epicenter);
        skillData.setSkillState(SkillState.RUN);
        super.start(bender);

        Level level = bender.getEntity().level();
        ServerLevel serverLevel = (ServerLevel) level;

        blockQuaker = event -> {
            for (int i = 0; i < quakingBlocks.size(); i++) {
                Map.Entry<BlockPos, EarthController> entry = (Map.Entry<BlockPos, EarthController>) quakingBlocks.entrySet().toArray()[i];
                BlockPos pos = entry.getKey();
                EarthController earthController = entry.getValue();
                if(earthController == null) {
                    LoadedServerShip serverShip = VSGameUtilsKt.getShipObjectManagingPos(serverLevel, pos);
                    if(serverShip == null) return;
                    earthController = EarthController.getOrCreate(serverShip, bender);
                    quakingBlocks.put(pos, earthController);
                }
                System.out.println("TICKING FORCE INSIDE LOOP");

                double force = random.nextDouble(200) + 200;
                earthController.applyInvariantForce(new Vector3d(0, force, 0));
                System.out.println("force applied: " + force);
            }
        };

        this.listen(SkillTickEvent.class, blockQuaker);
    }

    @Override
    public void run(Bender bender) {
        super.run(bender);
        ticksPassed++;
        Level level = bender.getEntity().level();
        ServerLevel serverLevel = (ServerLevel) level;

        if(currentQuakeDistance < 1 && ticksPassed >= 20) // todo - configurable/skilldata based
        {
            currentQuakeDistance++;
            ticksPassed -= 20;

            LinkedHashMap<BlockPos, BlockState> currentRing = collectRing(
                    level, epicenter, currentQuakeDistance,
                    pos -> !level.getBlockState(pos).isAir(),
                    true
            );
            String dimensionId = VSGameUtilsKt.getDimensionId(level);
            if(!currentRing.isEmpty()) {
//                BlockPos ringOwner = (BlockPos) currentRing.keySet().toArray()[0];
//                Map.Entry<BlockPos, BlockState> entry = currentRing.entrySet().stream().findFirst().get();
//                BlockPos pos = entry.getKey();
//                BlockState state = entry.getValue();

//                originalLevelState.add(new OriginalBlock(pos, state));

//                BlockPos centerPos = VectorConversionsMCKt.toBlockPos(ship.getChunkClaim().getCenterBlockCoordinates(VSGameUtilsKt.getYRange(level), new Vector3i()));
//                VSUtils.relocateBlock(level, pos, centerPos, true, ship, Rotation.NONE);
                ServerShip ship = VSUtils.createNewShipAtBlock(serverLevel, VectorConversionsMCKt.toJOML(epicenter), false, 1, dimensionId);
                BlockPos centerPos = VectorConversionsMCKt.toBlockPos(ship.getChunkClaim().getCenterBlockCoordinates(VSGameUtilsKt.getYRange(level), new Vector3i()));

                currentRing.forEach((pos, state) -> {
                    originalLevelState.add(new OriginalBlock(pos, state));

                    System.out.println(pos);
                    VSUtils.relocateBlock(level, pos, centerPos, true, ship, Rotation.NONE);
                });

                quakingBlocks.put(centerPos, null);

//                LoadedServerShip serverShip = VSGameUtilsKt.getShipObjectManagingPos(serverLevel, centerPos);
//                EarthController earthController = EarthController.getOrCreate(serverShip, bender);

            }

//            queue.addAll(currentRing.entrySet());
        }
//        System.out.println("EarthQuakeSkill is running on server");
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

    protected Consumer<SkillTickEvent> cleanupRunnable;

    public void startCleanup() {
        cleanupRunnable = (skillTickEvent) -> {
            System.out.println("EarthQuakeSkill ticking stop on server");
            ticksStopped++;
            if(ticksStopped >= 400) {
                originalLevelState.restore((ServerLevel) bender.getEntity().level());
                super.stop(bender);
                System.out.println("EarthQuakeSkill stopped on server");
                this.stopCleanup();
            }
        };
        this.listen(SkillTickEvent.class, cleanupRunnable);
    }

    public void stopCleanup() {
        this.hush(cleanupRunnable);
    }

    @Override
    public void stop(Bender bender) {
        this.hush(blockQuaker);
        System.out.println("EarthQuakeSkill requested stop on server");
//        this.startCleanup(); // defer skill stop and removal till cleaned up blocks
    }
}
