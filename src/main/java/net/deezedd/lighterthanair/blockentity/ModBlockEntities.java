package net.deezedd.lighterthanair.blockentity;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.block.ModBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, LighterThanAir.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WeatherVaneBlockEntity>> WEATHER_VANE_BE =
            BLOCK_ENTITIES.register("weather_vane_be", () ->
                    BlockEntityType.Builder.of(WeatherVaneBlockEntity::new,
                            ModBlocks.WEATHER_VANE.get()
                    ).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<WindCompassBlockEntity>> WIND_COMPASS_BE =
            BLOCK_ENTITIES.register("wind_compass_be", () ->
                    BlockEntityType.Builder.of(WindCompassBlockEntity::new,
                            ModBlocks.WIND_COMPASS.get()
                    ).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
