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

        for (String color : ModBlocks.VANILLA_COLORS) {
            String name = "small_" + color + "_balloon_crate";
            // Item model parent definition
            withExistingParent(name, modLoc("block/small_" + color + "_balloon_crate"));
        }

        simpleItem(ModItems.ITEMS.getEntries().stream()
                .filter(item -> item.getId().getPath().equals("reinforced_fabric"))
                .findFirst()
                .orElseThrow());

        for (String color : ModBlocks.VANILLA_COLORS) {
            String name = "small_" + color + "_balloon_envelope";

            simpleItem(ModItems.ITEMS.getEntries().stream()
                    .filter(item -> item.getId().getPath().equals(name))
                    .findFirst()
                    .orElseThrow());
        }

    }

    private void simpleItem(DeferredHolder<Item, ? extends Item> item) {
        withExistingParent(item.getId().getPath(), "item/generated")
                .texture("layer0", ResourceLocation.fromNamespaceAndPath(item.getId().getNamespace(), "item/" + item.getId().getPath()));
    }
}

