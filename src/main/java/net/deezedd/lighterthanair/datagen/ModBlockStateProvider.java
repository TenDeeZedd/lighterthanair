package net.deezedd.lighterthanair.datagen;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.block.ModBlocks;
import net.deezedd.lighterthanair.block.WindCompassBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, LighterThanAir.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // Weather Vane "fake model" for Particle registration
        simpleBlock(ModBlocks.WEATHER_VANE.get(),
                models().withExistingParent("weather_vane_particle", "minecraft:block/block")
                        .texture("particle", blockTexture(ModBlocks.WEATHER_VANE.get())));

        // Wind Compass "fake model" for Particle registration
        var compassParticleModel = models().withExistingParent("wind_compass_particle", "minecraft:block/block")
                .texture("particle", blockTexture(ModBlocks.WIND_COMPASS.get()));

        // Variants: Floating
        getVariantBuilder(ModBlocks.WIND_COMPASS.get())
                .partialState().with(WindCompassBlock.FLOATING, false)
                .addModels(new ConfiguredModel(compassParticleModel))
                .partialState().with(WindCompassBlock.FLOATING, true)
                .addModels(new ConfiguredModel(compassParticleModel));

        registerBalloonCrateModels();
    }

    private void registerBalloonCrateModels() {
        ResourceLocation parentModel = modLoc("block/balloon_crate");

        for (String color : ModBlocks.VANILLA_COLORS) {
            String blockName = "small_" + color + "_balloon_crate";

            DeferredHolder<Block, ? extends Block> blockHolder = ModBlocks.BLOCKS.getEntries().stream()
                    .filter(b -> b.getId().getPath().equals(blockName))
                    .findFirst()
                    .orElseThrow();

            // Texture name definition
            ResourceLocation texture = modLoc("block/small_" + color + "_balloon_crate");

            // Model generation
            ModelFile generatedModel = models().withExistingParent(blockName, parentModel)
                    .texture("0", modLoc("block/balloon_crate"))
                    .texture("3", modLoc("block/balloon_crate_text_balloon"))
                    .texture("5", modLoc("block/balloon_crate_text_small"))
                    .texture("4", texture) // Naše dynamická textura
                    .texture("particle", modLoc("block/balloon_crate"))
                    .renderType("minecraft:cutout");

            horizontalBlock(blockHolder.get(), generatedModel);
        }
    }
}
