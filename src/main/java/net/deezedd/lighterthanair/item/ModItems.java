package net.deezedd.lighterthanair.item;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.block.ModBlocks;
import net.deezedd.lighterthanair.item.custom.WeatherVaneBlockItem;
import net.deezedd.lighterthanair.item.custom.WindCompassBlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LighterThanAir.MODID);

    public static final DeferredItem<Item> WEATHER_VANE_ITEM = ITEMS.register("weather_vane",
            () -> new WeatherVaneBlockItem(ModBlocks.WEATHER_VANE.get(), new Item.Properties()));

    public static final DeferredItem<Item> WIND_COMPASS_ITEM = ITEMS.register("wind_compass",
            () -> new WindCompassBlockItem(ModBlocks.WIND_COMPASS.get(), new Item.Properties()));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
