package com.amuzil.omegasource.utils.bending;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;


public record OriginalBlock(BlockPos pos, BlockState state) {}