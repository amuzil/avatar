package com.amuzil.omegasource.bending;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BendingSelection {
    public List<BlockPos> blockPositions;
    public List<Long> entityIds;
    public List<String> skillIds;
    public BendingSelection.Type type;

    public BendingSelection(List<BlockPos> positions, List<Long> entities, List<String> skills, BendingSelection.Type type) {
        blockPositions = positions;
        entityIds = entities;
        skillIds = skills;
        this.type = type;
    }

    public BendingSelection()
    {
        Reset();
    }

    public void Reset() {
        blockPositions = new ArrayList<>();
        entityIds = new ArrayList<>();
        skillIds = new ArrayList<>();
        type = BendingSelection.Type.NONE;
    }

    public void AddBlockPositions(List<BlockPos> pos) {
        blockPositions.addAll(pos);
        type = BendingSelection.Type.BLOCK;
    }

    public void AddBlockPosition(BlockPos pos) {
        blockPositions.add(pos);
        type = BendingSelection.Type.BLOCK;
    }

    @Override
    public String toString() {
        var suffix = switch (type) {
            case SELF -> "Player";
            case BLOCK -> String.valueOf(blockPositions.size());
            case ENTITY -> String.valueOf(entityIds.size());
            case SKILL -> String.valueOf(skillIds.size());
            default -> "";
        };
        return type + ": " + suffix;
    }

    void AddSkillId(String skillId) {
        skillIds.add(skillId);
        type = BendingSelection.Type.SKILL;
    }

    void AddEntityId(long entityId) {
        entityIds.add(entityId);
        type = BendingSelection.Type.SKILL;
    }

    void AddSkillIds(List<String> skillIds) {
        this.skillIds.addAll(skillIds);
        type = BendingSelection.Type.SKILL;
    }

    void AddEntityIds(List<Long> entityIds) {
        this.entityIds.addAll(entityIds);
        type = BendingSelection.Type.SKILL;
    }

    void RemoveEntity(long entityId) {
        entityIds.remove(entityId);
    }

    BendingSelection Copy() {
        var positions = new ArrayList<>(blockPositions);
        var entityIds = new ArrayList<>(this.entityIds);
        var skillIds = new ArrayList<>(this.skillIds);
        return new BendingSelection(positions, entityIds, skillIds, type);
    }

    public enum Type {
        NONE,
        SELF,
        ENTITY,
        SKILL,
        BLOCK;

        public static final Type[] selectionTypes = Arrays.copyOfRange(Type.values(), 0, 3);
    }
}
