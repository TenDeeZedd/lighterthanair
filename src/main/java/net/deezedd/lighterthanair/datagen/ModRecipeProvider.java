package net.deezedd.lighterthanair.datagen;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.item.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pRegistries) {
        super(pOutput, pRegistries);
    }

    @Override
    protected void buildRecipes(RecipeOutput pRecipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModItems.WEATHER_VANE_ITEM.get()) // Výstup: naše korouhev
                // Definujeme tvar receptu (řádky shora dolů)
                .pattern("I N") // První řádek: Ingot, Prázdno, Nugget
                .pattern("II ") // Druhý řádek: Ingot, Ingot, Prázdno
                .pattern(" N ") // Třetí řádek: Prázdno, Nugget, Prázdno
                // Definujeme, co znamenají jednotlivé znaky
                .define('I', Items.IRON_INGOT) // 'I' je Iron Ingot
                .define('N', Items.IRON_NUGGET) // 'N' je Iron Nugget
                // Zajistíme, že recept bude odemčen, když hráč sebere železo
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                // Uložíme recept
                .save(pRecipeOutput);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.WIND_COMPASS_ITEM.get()) // Změň na ModItems.WIND_COMPASS.get()
                .pattern("FL ") // F=Feather, L=Log, mezera=Prázdno
                .pattern("LIL") // L=Log, I=Ingot
                .pattern(" L ") // L=Log
                .define('F', Items.FEATHER)
                .define('L', ItemTags.LOGS) // Použijeme Tag pro logy
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_feather", has(Items.FEATHER))
                // Musíme dát každému receptu unikátní jméno (ID)
                .save(pRecipeOutput, ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "wind_compass_1"));

        // Recept 2: Pírko vpravo nahoře (pozice 3)
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.WIND_COMPASS_ITEM.get())
                .pattern(" LF")
                .pattern("LIL")
                .pattern(" L ")
                .define('F', Items.FEATHER)
                .define('L', ItemTags.LOGS)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_feather", has(Items.FEATHER))
                .save(pRecipeOutput, ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "wind_compass_2"));

        // Recept 3: Pírko vlevo dole (pozice 7)
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.WIND_COMPASS_ITEM.get())
                .pattern(" L ")
                .pattern("LIL")
                .pattern("FL ")
                .define('F', Items.FEATHER)
                .define('L', ItemTags.LOGS)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_feather", has(Items.FEATHER))
                .save(pRecipeOutput, ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "wind_compass_3"));

        // Recept 4: Pírko vpravo dole (pozice 9)
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.WIND_COMPASS_ITEM.get())
                .pattern(" L ")
                .pattern("LIL")
                .pattern(" LF")
                .define('F', Items.FEATHER)
                .define('L', ItemTags.LOGS)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_feather", has(Items.FEATHER))
                .save(pRecipeOutput, ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "wind_compass_4"));

    }
}
