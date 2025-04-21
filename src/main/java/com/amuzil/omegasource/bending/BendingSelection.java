package com.amuzil.omegasource.bending;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BendingSelection {
    public List<BlockPos> blockPositions;
    public List<Long> entityIds;
    public List<String> skillIds;
    public Target target;

    public BendingSelection(List<BlockPos> positions, List<Long> entities, List<String> skills, Target target) {
        blockPositions = positions;
        entityIds = entities;
        skillIds = skills;
        this.target = target;
    }

    public BendingSelection()
    {
        Reset();
    }

    public void Reset() {
        blockPositions = new ArrayList<>();
        entityIds = new ArrayList<>();
        skillIds = new ArrayList<>();
        target = Target.NONE;
    }

    public void AddBlockPositions(List<BlockPos> pos) {
        blockPositions.addAll(pos);
        target = Target.BLOCK;
    }

    public void AddBlockPosition(BlockPos pos) {
        blockPositions.add(pos);
        target = Target.BLOCK;
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

    void AddSkillId(String skillId) {
        skillIds.add(skillId);
        target = Target.SKILL;
    }

    void AddEntityId(long entityId) {
        entityIds.add(entityId);
        target = Target.SKILL;
    }

    void AddSkillIds(List<String> skillIds) {
        this.skillIds.addAll(skillIds);
        target = Target.SKILL;
    }

    void AddEntityIds(List<Long> entityIds) {
        this.entityIds.addAll(entityIds);
        target = Target.SKILL;
    }

    void RemoveEntity(long entityId) {
        entityIds.remove(entityId);
    }

    BendingSelection Copy() {
        var positions = new ArrayList<>(blockPositions);
        var entityIds = new ArrayList<>(this.entityIds);
        var skillIds = new ArrayList<>(this.skillIds);
        return new BendingSelection(positions, entityIds, skillIds, target);
    }

    public enum Target {
        NONE,
        SELF,
        ENTITY,
        SKILL,
        BLOCK;

        public static final Target[] TYPES = Arrays.copyOfRange(Target.values(), 0, 3);
    }
}
