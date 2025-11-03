package net.deezedd.lighterthanair.util;

import net.deezedd.lighterthanair.LighterThanAir;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        // (Stávající tagy)
        public static final TagKey<Block> NEEDS_REINFORCED_TOOL = tag("needs_reinforced_tool");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, name));
        }
    }

    public static class Items {

        public static final TagKey<Item> SMALL_BALLOON_ENVELOPES = tag("small_balloon_envelopes");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, name));
        }
    }
}
