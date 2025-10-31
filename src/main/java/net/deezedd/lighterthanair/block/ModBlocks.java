package net.deezedd.lighterthanair.block;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.item.ModItems;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(LighterThanAir.MODID);

    public static final DeferredBlock<Block> WEATHER_VANE = BLOCKS.register("weather_vane",
            () -> new WeatherVaneBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(2.0f)
                    .requiresCorrectToolForDrops()
                    .sound(SoundType.METAL)
                    .noOcclusion()));

    public static final DeferredBlock<Block> WIND_COMPASS = BLOCKS.register("wind_compass",
            () -> new WindCompassBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.WOOD)
                    .strength(1.0f)
                    .sound(SoundType.WOOD)
                    .noOcclusion()));



    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);


    }
}
