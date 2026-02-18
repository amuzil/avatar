package com.amuzil.av3.utils.bending;

import com.amuzil.av3.Avatar;
import com.amuzil.av3.bending.element.Element;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;


public class BendingMaterial {
    public static final TagKey<Block> AIRBENDING_MATERIAL = BlockTags.create(Avatar.id("airbending_material"));
    public static final TagKey<Block> EARTHBENDING_MATERIAL = BlockTags.create(Avatar.id("earthbending_material"));
    public static final TagKey<Block> FIREBENDING_MATERIAL = BlockTags.create(Avatar.id("firebending_material"));
    public static final TagKey<Block> WATERBENDING_MATERIAL = BlockTags.create(Avatar.id("waterbending_material"));

    public static TagKey<Block> getBendableMaterials(Element element) {
        return switch (element.type()) {
            case AIR -> AIRBENDING_MATERIAL;
            case EARTH -> EARTHBENDING_MATERIAL;
            case FIRE -> FIREBENDING_MATERIAL;
            case WATER -> WATERBENDING_MATERIAL;
        };
    }

    public static boolean isBendable(BlockState blockState, Element element) {
        TagKey<Block> bendableMaterials = getBendableMaterials(element);
        return blockState.is(bendableMaterials);
    }

    public static void debugEarthbendableTag(MinecraftServer server) {
        Registry<Block> blockRegistry = server.registryAccess()
                .registryOrThrow(Registries.BLOCK);

        HolderSet.Named<Block> earthbendable = blockRegistry
                .getTag(EARTHBENDING_MATERIAL)
                .orElse(null);

        if (earthbendable == null) {
            System.out.println("EARTHBENDABLE tag not found!");
            return;
        }

        System.out.println("=== EARTHBENDABLE BLOCKS ===");
        earthbendable.stream().forEach(holder -> {
            ResourceLocation id = blockRegistry.getKey(holder.value());
            System.out.println("  - " + id);
        });
        System.out.println("Total: " + earthbendable.size());
    }
}
