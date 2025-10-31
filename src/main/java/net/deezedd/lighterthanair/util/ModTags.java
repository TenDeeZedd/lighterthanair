package net.deezedd.lighterthanair.util;

import net.deezedd.lighterthanair.LighterThanAir;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> MEDIUM_FERMENTATION_BARRELS =
                create("medium_fermentation_barrels");

        private static TagKey<Block> create(String name) {
            return TagKey.create(BuiltInRegistries.BLOCK.key(), ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, name));
        }
    }

    public static class Items {
        //public static final TagKey<Item> DRYABLE_GRAPES =
        //        create("dryable_grapes");

        //public static final TagKey<Item> FRUIT_PRESS_INPUTS =
        //        create("fruit_press_inputs");

        private static TagKey<Item> create(String name) {
            return TagKey.create(BuiltInRegistries.ITEM.key(), ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, name));
        }

    }


}
