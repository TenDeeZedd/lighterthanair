package net.deezedd.lighterthanair.datagen;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.item.ModItems;
import net.deezedd.lighterthanair.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput, pRegistries);
    }

    private static final Map<String, Item> COLOR_TO_DYE = Map.ofEntries(
            Map.entry("white", Items.WHITE_DYE),
            Map.entry("orange", Items.ORANGE_DYE),
            Map.entry("magenta", Items.MAGENTA_DYE),
            Map.entry("light_blue", Items.LIGHT_BLUE_DYE),
            Map.entry("yellow", Items.YELLOW_DYE),
            Map.entry("lime", Items.LIME_DYE),
            Map.entry("pink", Items.PINK_DYE),
            Map.entry("gray", Items.GRAY_DYE),
            Map.entry("light_gray", Items.LIGHT_GRAY_DYE),
            Map.entry("cyan", Items.CYAN_DYE),
            Map.entry("purple", Items.PURPLE_DYE),
            Map.entry("blue", Items.BLUE_DYE),
            Map.entry("brown", Items.BROWN_DYE),
            Map.entry("green", Items.GREEN_DYE),
            Map.entry("red", Items.RED_DYE),
            Map.entry("black", Items.BLACK_DYE)
    );

    @Override
    protected void buildRecipes(RecipeOutput pRecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModItems.WEATHER_VANE_ITEM.get())
                .pattern("I N")
                .pattern("II ")
                .pattern(" N ")
                .define('I', Items.IRON_INGOT)
                .define('N', Items.IRON_NUGGET)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(pRecipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.WIND_COMPASS_ITEM.get())
                .pattern("FL ")
                .pattern("LIL")
                .pattern(" L ")
                .define('F', Items.FEATHER)
                .define('L', ItemTags.LOGS)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_feather", has(Items.FEATHER))
                .save(pRecipeOutput, ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "wind_compass_1"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.WIND_COMPASS_ITEM.get())
                .pattern(" LF")
                .pattern("LIL")
                .pattern(" L ")
                .define('F', Items.FEATHER)
                .define('L', ItemTags.LOGS)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_feather", has(Items.FEATHER))
                .save(pRecipeOutput, ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "wind_compass_2"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.WIND_COMPASS_ITEM.get())
                .pattern(" L ")
                .pattern("LIL")
                .pattern("FL ")
                .define('F', Items.FEATHER)
                .define('L', ItemTags.LOGS)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_feather", has(Items.FEATHER))
                .save(pRecipeOutput, ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "wind_compass_3"));

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.WIND_COMPASS_ITEM.get())
                .pattern(" L ")
                .pattern("LIL")
                .pattern(" LF")
                .define('F', Items.FEATHER)
                .define('L', ItemTags.LOGS)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_feather", has(Items.FEATHER))
                .save(pRecipeOutput, ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "wind_compass_4"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.REINFORCED_FABRIC.get(), 1)
                .pattern("LWL")
                .pattern("WLW")
                .pattern("LWL")
                .define('L', Items.LEATHER)
                .define('W', ItemTags.WOOL)
                .unlockedBy("has_leather", has(Items.LEATHER))
                .unlockedBy("has_wool", has(ItemTags.WOOL))
                .save(pRecipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SMALL_BALLOON_ENVELOPES.get("brown").get(), 1)
                .requires(ModItems.REINFORCED_FABRIC.get(), 4)
                .unlockedBy("has_reinforced_fabric", has(ModItems.REINFORCED_FABRIC.get()))
                .save(pRecipeOutput, "small_brown_balloon_envelope_from_fabric");

        for (Map.Entry<String, Item> entry : COLOR_TO_DYE.entrySet()) {
            String color = entry.getKey();
            Item dye = entry.getValue();
            Item outputEnvelope = ModItems.SMALL_BALLOON_ENVELOPES.get(color).get();

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, outputEnvelope, 1)
                    .pattern(" D ")
                    .pattern("DED")
                    .pattern(" D ")
                    .define('D', dye)
                    .define('E', ModTags.Items.SMALL_BALLOON_ENVELOPES)
                    .unlockedBy("has_reinforced_fabric", has(ModItems.REINFORCED_FABRIC.get()))
                    .save(pRecipeOutput, "small_" + color + "_balloon_envelope_from_dyeing");
        }

    }
}
