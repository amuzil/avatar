package com.amuzil.omegasource.api.magus.skill.utils.bending;

import com.amuzil.omegasource.Avatar;
import com.amuzil.omegasource.bending.element.Element;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;


public class BendingMaterialUtil {
    public static final TagKey<Block> AIRBENDING_MATERIAL = BlockTags.create(new ResourceLocation(Avatar.MOD_ID, "airbending_material.json"));
    public static final TagKey<Block> EARTHBENDING_MATERIAL = BlockTags.create(new ResourceLocation(Avatar.MOD_ID, "earthbending_material.json"));
    public static final TagKey<Block> FIREBENDING_MATERIAL = BlockTags.create(new ResourceLocation(Avatar.MOD_ID, "firebending_material.json"));
    public static final TagKey<Block> WATERBENDING_MATERIAL = BlockTags.create(new ResourceLocation(Avatar.MOD_ID, "waterbending_material.json"));

    public static List<TagKey<Block>> getBendableMaterialsForElement(Element element) {
        List<TagKey<Block>> toReturn = new ArrayList<>();

        switch (element.name()) {
            case "air":
                //add nothing
                toReturn.add(AIRBENDING_MATERIAL);
                break;
            case "water":
                toReturn.add(WATERBENDING_MATERIAL);
                break;
            case "earth":
                toReturn.add(EARTHBENDING_MATERIAL);
                break;
            case "fire":
                toReturn.add(FIREBENDING_MATERIAL);
                //add fire block?
                break;
            default: return toReturn;
        }
        return toReturn;
    }
}
