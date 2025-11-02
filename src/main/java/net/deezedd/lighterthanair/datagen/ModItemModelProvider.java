package net.deezedd.lighterthanair.datagen;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.block.ModBlocks;
import net.deezedd.lighterthanair.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

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
        // ======================
    }
}

