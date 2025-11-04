package net.deezedd.lighterthanair.datagen;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.block.ModBlocks;
import net.deezedd.lighterthanair.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, LighterThanAir.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        // ===== OPRAVA ZDE =====
        // Pro generování item modelu nepotřebujeme DeferredBlock/Holder, stačí nám název.
        for (String color : ModBlocks.VANILLA_COLORS) {
            String name = "small_" + color + "_balloon_crate";
            // Vytvoří item model, který odkazuje na block model se stejným jménem
            withExistingParent(name, modLoc("block/small_" + color + "_balloon_crate"));
        }

        simpleItem(ModItems.ITEMS.getEntries().stream()
                .filter(item -> item.getId().getPath().equals("reinforced_fabric"))
                .findFirst()
                .orElseThrow());

        // 2. Všech 16 barevných plášťů
        for (String color : ModBlocks.VANILLA_COLORS) {
            String name = "small_" + color + "_balloon_envelope";

            // Najdeme item a zaregistrujeme pro něj model
            simpleItem(ModItems.ITEMS.getEntries().stream()
                    .filter(item -> item.getId().getPath().equals(name))
                    .findFirst()
                    .orElseThrow());
        }
        // ======================
    }

    private void simpleItem(DeferredHolder<Item, ? extends Item> item) {
        withExistingParent(item.getId().getPath(), "item/generated")

                // ===== OPRAVA CHYBY ZDE =====
                // Místo 'new ResourceLocation(...)' použijeme 'ResourceLocation.fromNamespaceAndPath(...)'
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(item.getId().getNamespace(), "item/" + item.getId().getPath()));
        // ============================
    }
}

