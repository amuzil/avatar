package com.amuzil.omegasource.bending;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class BendingSelection implements INBTSerializable<CompoundTag> {
    public List<BlockPos> blockPositions = new ArrayList<>();
    public List<UUID> entityIds = new ArrayList<>();
    public List<String> skillIds = new ArrayList<>();
    public Target target = Target.NONE;

    public BendingSelection(List<BlockPos> positions, List<UUID> entities, List<String> skills, Target target) {
        blockPositions = positions;
        entityIds = entities;
        skillIds = skills;
        this.target = target;
    }

    public BendingSelection(Target target) {
        reset();
        this.target = target;
    }

    public BendingSelection()
    {
        reset();
    }

    public BendingSelection(CompoundTag tag) {
        this.deserializeNBT(tag);
    }

    public void reset() {
        blockPositions.clear();
        entityIds.clear();
        skillIds.clear();
        target = Target.NONE;
    }

    public void addBlockPositions(List<BlockPos> pos) {
        if (target != Target.BLOCK) {
            entityIds = new ArrayList<>();
            skillIds = new ArrayList<>();
            target = Target.BLOCK;
        }
        blockPositions.addAll(pos);
    }

    public void addBlockPosition(BlockPos pos) {
        if (target != Target.BLOCK) {
            entityIds = new ArrayList<>();
            skillIds = new ArrayList<>();
            target = Target.BLOCK;
        }
        if (!blockPositions.contains(pos)) {
            blockPositions.add(pos);
        }
//        else {
//            if (!blockPositions.contains(pos.north()))
//                blockPositions.add(pos.north());
//            if (!blockPositions.contains(pos.south()))
//                blockPositions.add(pos.south());
//            if (!blockPositions.contains(pos.east()))
//                blockPositions.add(pos.east());
//            if (!blockPositions.contains(pos.west()))
//                blockPositions.add(pos.west());
//        }
    }

    public void addSkillId(String skillId) {
        if (target != Target.SKILL) {
            entityIds = new ArrayList<>();
            blockPositions = new ArrayList<>();
            target = Target.SKILL;
        }
        skillIds.add(skillId);
    }

    public void addEntityId(UUID entityId) {
        if (target != Target.ENTITY) {
            skillIds = new ArrayList<>();
            blockPositions = new ArrayList<>();
            target = Target.ENTITY;
        }
        entityIds.add(entityId);
    }

    public void addSkillIds(List<String> skillIds) {
        if (target != Target.SKILL) {
            entityIds = new ArrayList<>();
            blockPositions = new ArrayList<>();
            target = Target.SKILL;
        }
        this.skillIds.addAll(skillIds);
    }

    public void addEntityIds(List<UUID> entityIds) {
        if (target != Target.ENTITY) {
            skillIds = new ArrayList<>();
            blockPositions = new ArrayList<>();
            target = Target.ENTITY;
        }
        this.entityIds.addAll(entityIds);
    }

    public void removeEntity(UUID entityId) {
        entityIds.remove(entityId);
    }

    BendingSelection copy() {
        var positions = new ArrayList<>(this.blockPositions);
        var entityIds = new ArrayList<>(this.entityIds);
        var skillIds = new ArrayList<>(this.skillIds);
        return new BendingSelection(positions, entityIds, skillIds, target);
    }

    @Override
    public String toString() {
        var suffix = switch (target) {
            case SELF -> "Player";
            case BLOCK -> String.valueOf(blockPositions.size());
            case ENTITY -> String.valueOf(entityIds.size());
            case SKILL -> String.valueOf(skillIds.size());
            default -> "";
        };
        return target + ": " + suffix;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();

        compoundTag.putString("targetType", this.target.toString());

        switch (this.target) {
            case BLOCK:
                ListTag blockPositions = new ListTag();
                for (int i = 0; i < this.blockPositions.size(); i++) {
                    CompoundTag blockPos = new CompoundTag();
                    BlockPos current = this.blockPositions.get(i);
                    blockPos.putInt("x", current.getX());
                    blockPos.putInt("y", current.getY());
                    blockPos.putInt("z", current.getZ());
                    blockPositions.add(i, blockPos);
                }
                compoundTag.put("blockPositions", blockPositions);
                break;
            case ENTITY:
                ListTag entities = new ListTag();
                for (int i = 0; i < this.entityIds.size(); i++) {
                    UUID current = this.entityIds.get(i);
                    entities.add(i, NbtUtils.createUUID(current));
                }
                compoundTag.put("entities", entities);
                break;
            case SKILL:
                ListTag skills = new ListTag();
                for (int i = 0; i < this.skillIds.size(); i++) {
                    String current = this.skillIds.get(i);
                    skills.add(i, StringTag.valueOf(current));
                }
                compoundTag.put("skills", skills);
                break;
        }
        return compoundTag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        String targetType = tag.getString("targetType");
        this.target = Enum.valueOf(Target.class, targetType);

        this.blockPositions = new ArrayList<>();
        this.entityIds = new ArrayList<>();
        this.skillIds = new ArrayList<>();

        switch (this.target) {
            case BLOCK:
                ListTag blockPositions = (ListTag)tag.get("blockPositions");
                for (int i = 0; i < blockPositions.size(); i++) {
                    CompoundTag blockPos = (CompoundTag) blockPositions.get(i);
                    int x = blockPos.getInt("x");
                    int y = blockPos.getInt("y");
                    int z = blockPos.getInt("z");
                    BlockPos current = new BlockPos(x,y,z);
                    this.blockPositions.add(current);
                }
                break;
            case ENTITY:
                ListTag entities = (ListTag)tag.get("entities");
                for (int i = 0; i < entities.size(); i++) {
                    UUID current = NbtUtils.loadUUID(entities.get(i));
                    this.entityIds.add(current);
                }
                break;
            case SKILL:
                ListTag skills = (ListTag)tag.get("skills");
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
