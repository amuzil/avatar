package com.amuzil.omegasource.utils.ship;

import net.minecraft.server.level.ServerLevel;

import java.util.ArrayList;
import java.util.List;


public class OriginalBlocks {

    private final List<OriginalBlock> originalBlocks;
    private int tickCount = 0;
    private boolean startedTicking = false;

    public OriginalBlocks() {
        this.originalBlocks = new ArrayList<>();
    }

    public OriginalBlocks(List<OriginalBlock> originalBlocks) {
        this.originalBlocks = originalBlocks;
    }

    public List<OriginalBlock> get() {
        return originalBlocks;
    }

    public void add(OriginalBlock additionalBlock) {
        this.originalBlocks.add(additionalBlock);
    }
    public void addAll(List<OriginalBlock> additionalBlocks) {
        this.originalBlocks.addAll(additionalBlocks);
    }

    public int incrementAndGetTickCount() {
        tickCount++;
        return tickCount;
    }

    public boolean startedTicking() {
        return startedTicking;
    }

    public void startTicking(boolean ticking) {
        this.startedTicking = ticking;
    }

    public void restore(ServerLevel level) {
        originalBlocks.forEach(block -> {
                level.setBlock(block.pos(), block.state(), 3);
        });
    }
}
