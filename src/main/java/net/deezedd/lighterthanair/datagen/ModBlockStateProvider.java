package net.deezedd.lighterthanair.datagen;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.block.ModBlocks;
import net.deezedd.lighterthanair.block.WindCompassBlock;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, LighterThanAir.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // Registrace pro korouhev (aby měla particle model)
        simpleBlock(ModBlocks.WEATHER_VANE.get(),
                models().withExistingParent("weather_vane_particle", "minecraft:block/block")
                        .texture("particle", blockTexture(ModBlocks.WEATHER_VANE.get())));

        // --- PŘIDÁME LOGIKU PRO KOMPAS ---
        // Vytvoříme "falešný" model jen pro částice (stejně jako u korouhve)
        var compassParticleModel = models().withExistingParent("wind_compass_particle", "minecraft:block/block")
                .texture("particle", blockTexture(ModBlocks.WIND_COMPASS.get()));

        // Registrujeme varianty pro FLOATING=true a FLOATING=false
        getVariantBuilder(ModBlocks.WIND_COMPASS.get())
                .partialState().with(WindCompassBlock.FLOATING, false)
                .addModels(new ConfiguredModel(compassParticleModel))
                .partialState().with(WindCompassBlock.FLOATING, true)
                .addModels(new ConfiguredModel(compassParticleModel));
        // --- KONEC LOGIKY KOMPASU ---
    }
}
