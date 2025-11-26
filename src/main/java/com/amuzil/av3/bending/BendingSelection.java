package com.amuzil.av3.bending;

import com.amuzil.av3.utils.bending.OriginalBlock;
import com.amuzil.av3.utils.bending.OriginalBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.*;


public class BendingSelection implements INBTSerializable<CompoundTag> {
    public static final StreamCodec<RegistryFriendlyByteBuf, BendingSelection> STREAM_CODEC =
            StreamCodec.of(BendingSelection::encode, BendingSelection::decode);

    private final Map<Long, OriginalBlocks> originalBlocksMap = new HashMap<>();
    private BlockPos blockPos;
    private Set<UUID> entityIds = new LinkedHashSet<>();
    private List<String> skillIds = new ArrayList<>();
    private Target target = Target.NONE;

    public BendingSelection() {

    }

    public BendingSelection(HolderLookup.Provider provider, CompoundTag tag) {
        this.deserializeNBT(provider, tag);
    }

    public BendingSelection(BlockPos position, Set<UUID> entities, List<String> skills, Target target) {
        blockPos = position;
        entityIds = entities;
        skillIds = skills;
        this.target = target;
    }

    // For Syncing
    public static void encode(RegistryFriendlyByteBuf buf, BendingSelection data) {
        CompoundTag tag = data.serializeNBT(buf.registryAccess());
        buf.writeNbt(tag);
    }

    // For Syncing
    public static BendingSelection decode(RegistryFriendlyByteBuf buf) {
        CompoundTag tag = buf.readNbt();
        if (tag == null)
            return new BendingSelection();
        return new BendingSelection(buf.registryAccess(), tag);
    }

    public void reset() {
        blockPos = null;
        entityIds.clear();
        skillIds.clear();
        target = Target.NONE;
    }

    public void resetOriginalBlocks() {
        originalBlocksMap.clear();
    }

    public Map<Long, OriginalBlocks> originalBlocksMap() {
        return originalBlocksMap;
    }

    public List<String> skillIds() {
        return skillIds;
    }

    public Set<UUID> entityIds() {
        return entityIds;
    }

    public void addOriginalBlocks(long id, BlockPos blockPos, BlockState blockState) {
        originalBlocksMap.put(id, new OriginalBlocks(
                Collections.singletonList(new OriginalBlock(blockPos, blockState)))
        );
    }

    public void addOriginalBlocks(long id, List<OriginalBlock> originalBlocks) {
        originalBlocksMap.put(id, new OriginalBlocks(originalBlocks));
    }

    public void addOriginalBlocks(long id, OriginalBlocks originalBlocks) {
        originalBlocksMap.put(id, originalBlocks);
    }

    public void setOriginalBlocksMap(Map<Long, OriginalBlocks> originalBlocksMap) {
        this.originalBlocksMap.putAll(originalBlocksMap);
    }

    public void setSelf() {
        if (target != Target.SELF) {
            this.reset();
            target = Target.SELF;
        }
    }

    public void setBlockPos(BlockPos pos) {
        if (target != Target.BLOCK) {
            entityIds = new LinkedHashSet<>();
            skillIds = new ArrayList<>();
            target = Target.BLOCK;
        }
        blockPos = pos;
    }

    public BlockPos blockPos() {
        return blockPos;
    }

    public void addSkillId(String skillId) {
        if (target != Target.SKILL) {
            entityIds = new LinkedHashSet<>();
            blockPos = null;
            target = Target.SKILL;
        }
        skillIds.add(skillId);
    }

    public void addEntityId(UUID entityId) {
        if (target != Target.ENTITY) {
            skillIds = new ArrayList<>();
            blockPos = null;
            target = Target.ENTITY;
        }
        entityIds.add(entityId);
    }

    public Target target() {
        return target;
    }

    BendingSelection copy() {
        return new BendingSelection(blockPos, entityIds, skillIds, target);
    }

    @Override
    public String toString() {
        var suffix = switch (target) {
            case SELF -> "Bender";
            case BLOCK -> blockPos.toString();
            case ENTITY -> String.valueOf(entityIds.size());
            case SKILL -> String.valueOf(skillIds.size());
            default -> "";
        };
        return target + ": " + suffix;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("targetType", this.target.toString());
        int i = 0;

        switch (this.target) {
            case BLOCK:
                CompoundTag blockPos = new CompoundTag();
                blockPos.putInt("x", this.blockPos.getX());
                blockPos.putInt("y", this.blockPos.getY());
                blockPos.putInt("z", this.blockPos.getZ());
                compoundTag.put("blockPos", blockPos);
                break;
            case ENTITY:
                ListTag entities = new ListTag();
                for (UUID entityId: this.entityIds) {
                    entities.add(i, NbtUtils.createUUID(entityId));
                    i++;
                }
                compoundTag.put("entities", entities);
                break;
            case SKILL:
                ListTag skills = new ListTag();
                for (i = 0; i < this.skillIds.size(); i++) {
                    String current = this.skillIds.get(i);
                    skills.add(i, StringTag.valueOf(current));
                }
                compoundTag.put("skills", skills);
                break;
        }
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        String targetType = tag.getString("targetType");
        this.target = Enum.valueOf(Target.class, targetType);

        this.blockPos = null;
        this.entityIds = new LinkedHashSet<>();
        this.skillIds = new ArrayList<>();

        switch (this.target) {
            case BLOCK:
                CompoundTag blockPosTag = (CompoundTag) tag.get("blockPos");
                int x = blockPosTag.getInt("x");
                int y = blockPosTag.getInt("y");
                int z = blockPosTag.getInt("z");
                this.blockPos = new BlockPos(x, y, z);
                break;
            case ENTITY:
                ListTag entities = (ListTag) tag.get("entities");
                for (int i = 0; i < entities.size(); i++) {
                    UUID current = NbtUtils.loadUUID(entities.get(i));
                    this.entityIds.add(current);
                }
                break;
            case SKILL:
                ListTag skills = (ListTag) tag.get("skills");
                for (int i = 0; i < skills.size(); i++) {
                    String current = skills.get(i).getAsString();
                    this.skillIds.add(current);
                }
                break;
        }
    }

    public enum Target {
        NONE,
        SELF,
        ENTITY,
        SKILL,
        BLOCK;
    }
}
