package com.amuzil.av3.api.magus.skill.traits.skilltraits;

import com.amuzil.av3.api.magus.skill.traits.SkillTrait;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlockState;


/**
 * Huge trait class, stores the source block of the Skill.
 */
public class BlockTrait extends SkillTrait {

    private IForgeBlockState state;
    private BlockPos pos;

    // Note: If you want to know how long a usable BlockState has been selected, use another
    // TimedTrait.
    public BlockTrait(String name, IForgeBlockState state, BlockPos pos) {
        super(name);
        this.state = state;
        this.pos = pos;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        tag.putInt("value", Block.getId((BlockState) state));
        tag.putIntArray("value", new int[] {
                pos.getX(), pos.getY(), pos.getZ()
        });
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        super.deserializeNBT(nbt);
        state = Block.stateById(nbt.getInt("value"));
        int[] blockPos = nbt.getIntArray("value");
        pos = new BlockPos(blockPos[0],  blockPos[1], blockPos[2]);
    }

    public void setState(BlockState state) {
        this.state = state;
        markDirty();
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
        markDirty();
    }

    public BlockState getState() {
        return (BlockState) state;
    }

    public IForgeBlockState getForgeState() {
        return this.state;
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public void reset() {
        super.reset();
        //Default source info is 0,0,0 with Air.
        setPos(BlockPos.ZERO);
        setState(Blocks.AIR.defaultBlockState());
    }
}
