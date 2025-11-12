package com.amuzil.av3.bending.element.earth;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.skill.EarthSkill;
import com.amuzil.av3.data.capability.Bender;
import com.amuzil.av3.utils.Constants;
import com.amuzil.av3.utils.bending.OriginalBlocks;
import com.amuzil.magus.skill.data.SkillPathBuilder;
import com.amuzil.magus.skill.event.SkillTickEvent;
import com.amuzil.magus.skill.traits.skilltraits.KnockbackTrait;
import com.amuzil.magus.skill.traits.skilltraits.SizeTrait;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.amuzil.av3.bending.form.BendingForms.*;


public class EarthQuakeSkill extends EarthSkill {
    private static final Logger log = LoggerFactory.getLogger(EarthQuakeSkill.class);
    OriginalBlocks originalBlocks = new OriginalBlocks();
    protected Consumer<SkillTickEvent> cleanupRunnable;
    Consumer<SkillTickEvent> blockQuaker;
    private BlockPos epicenter;
    private int currentQuakeDistance = 0;
    private int ticksPassed = 0;
    private int ticksStopped = 0;
    private Random random = new Random();

    public EarthQuakeSkill() {
        super(Avatar.MOD_ID, "earth_quake");
        addTrait(new KnockbackTrait(Constants.KNOCKBACK, 1.5f));
        addTrait(new SizeTrait(Constants.SIZE, 1.0f));

        this.startPaths = SkillPathBuilder.getInstance()
                .add(RAISE)
                .add(LOWER)
                .build();

        this.stopPaths = SkillPathBuilder.getInstance()
                .add(BLOCK)
                .build();
    }

    @Override
    public void start(Bender bender) {
    }

    @Override
    public void run(Bender bender) {
        super.run(bender);
    }

    @Override
    public void stop(Bender bender) {
        super.stop(bender);
    }

    public static LinkedHashMap<BlockPos, BlockState> collectRing(
            LevelReader level, BlockPos epicentre, int radius, Predicate<BlockPos> includePos, boolean skipUnloaded) {
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

    private static void sample(
            LevelReader level, BlockPos pos, Predicate<BlockPos> includePos, boolean skipUnloaded, LinkedHashMap<BlockPos, BlockState> sink) {
        if (!includePos.test(pos)) {
            return;
        }
        BlockState state = level.getBlockState(pos);
        sink.put(pos, state);
    }

    public void startCleanup() {
        cleanupRunnable = (skillTickEvent) -> {
            ticksStopped++;
            if (ticksStopped >= 400)
                originalBlocks.restore((ServerLevel) bender.getEntity().level());
        };
        this.listen(SkillTickEvent.class, cleanupRunnable);
    }

    public void stopCleanup() {
        this.hush(cleanupRunnable);
    }
}
