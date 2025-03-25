package com.amuzil.omegasource.bending;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;


public class BendingSelection {
    public List<BlockPos> BlockPositions;
    public List<Long> EntityIds;
    public List<String> TechniqueIds;
    public SelectionType Type;

    public BendingSelection(List<BlockPos> positions, List<Long> entities, List<String> techniques, SelectionType type) {
        BlockPositions = positions;
        EntityIds = entities;
        TechniqueIds = techniques;
        Type = type;
    }

    public BendingSelection()
    {
        Reset();
    }

    public void Reset() {
        BlockPositions = new ArrayList<>();
        EntityIds = new ArrayList<>();
        TechniqueIds = new ArrayList<>();
        Type = SelectionType.None;
    }

    public void AddBlockPositions(List<BlockPos> pos) {
        BlockPositions.addAll(pos);
        Type = SelectionType.Block;
    }

    public void AddBlockPosition(BlockPos pos) {
        BlockPositions.add(pos);
        Type = SelectionType.Block;
    }

    @Override
    public String toString() {
        var suffix = "";
        switch (Type)
        {
            case Self:
                suffix = "Player";
                break;
            case Block:
                suffix = String.valueOf(BlockPositions.size());
                break;
            case Entity:
                suffix = String.valueOf(EntityIds.size());
                break;
            case Technique:
                suffix = String.valueOf(TechniqueIds.size());
                break;
        }
        return Type + ": " + suffix;
    }

    void AddTechniqueId(String techniqueId) {
        TechniqueIds.add(techniqueId);
        Type = SelectionType.Technique;
    }

    void AddEntityId(long entityId) {
        EntityIds.add(entityId);
        Type = SelectionType.Technique;
    }

    void AddTechniqueIds(List<String> techniqueIds) {
        TechniqueIds.addAll(techniqueIds);
        Type = SelectionType.Technique;
    }

    void AddEntityIds(List<Long> entityIds) {
        EntityIds.addAll(entityIds);
        Type = SelectionType.Technique;
    }

    void RemoveEntity(long entityId) {
        EntityIds.remove(entityId);
    }

    BendingSelection Copy() {
        var positions = new ArrayList<BlockPos>();
        positions.addAll(BlockPositions);

        var entityIds = new ArrayList<Long>();
        entityIds.addAll(EntityIds);

        var techniqueIds = new ArrayList<String>();
        techniqueIds.addAll(TechniqueIds);

        return new BendingSelection(positions, entityIds, techniqueIds, Type);
    }

    public enum SelectionType {
        None,
        Self,
        Technique,
        Block,
        Entity,
    }
}
