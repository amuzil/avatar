package com.amuzil.omegasource.utils.bending;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.bending.element.Element;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;


public class BendingMaterialUtil {
    public static final TagKey<Block> AIRBENDING_MATERIAL = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "airbending_material.json"));
    public static final TagKey<Block> EARTHBENDING_MATERIAL = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "earthbending_material.json"));
    public static final TagKey<Block> FIREBENDING_MATERIAL = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "firebending_material.json"));
    public static final TagKey<Block> WATERBENDING_MATERIAL = BlockTags.create(ResourceLocation.fromNamespaceAndPath(Avatar.MOD_ID, "waterbending_material.json"));

    public static List<TagKey<Block>> getBendableMaterialsForElement(Element element) {
        List<TagKey<Block>> toReturn = new ArrayList<>();

        switch (element.type()) {
            case AIR:
                toReturn.add(AIRBENDING_MATERIAL);
                break;
            case EARTH:
                toReturn.add(EARTHBENDING_MATERIAL);
                break;
            case FIRE:
                toReturn.add(FIREBENDING_MATERIAL);
                break;
            case WATER:
                toReturn.add(WATERBENDING_MATERIAL);
                break;
            default: return toReturn;
        }
        return toReturn;
    }
}
