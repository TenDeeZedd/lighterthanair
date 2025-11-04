package net.deezedd.lighterthanair.block;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.function.Supplier;

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

    public static final List<String> VANILLA_COLORS = List.of(
            "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
            "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
    );

    public static void registerBalloonCrates() {
        for (String color : VANILLA_COLORS) {
            String name = "small_" + color + "_balloon_crate";

            // Zaregistrujeme blok
            DeferredBlock<Block> crateBlock = registerBlock(name,
                    // ===== UPRAVENO: Předáváme "color" do konstruktoru =====
                    () -> new BalloonCrateBlock(BlockBehaviour.Properties.of()
                            .mapColor(MapColor.WOOD)
                            .strength(2.5f)
                            .sound(SoundType.WOOD),
                            color)); // <-- Zde předáváme barvu

            // Zaregistrujeme odpovídající item
            registerBlockItem(name, crateBlock);
        }
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        return BLOCKS.register(name, block);
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }


    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);


    }
}
