package net.deezedd.lighterthanair.item;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.block.ModBlocks;
import net.deezedd.lighterthanair.item.custom.WeatherVaneBlockItem;
import net.deezedd.lighterthanair.item.custom.WindCompassBlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LighterThanAir.MODID);

    public static final List<String> VANILLA_COLORS = List.of(
            "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray",
            "light_gray", "cyan", "purple", "blue", "brown", "green", "red", "black"
    );



    public static final DeferredItem<Item> WEATHER_VANE_ITEM = ITEMS.register("weather_vane",
            () -> new WeatherVaneBlockItem(ModBlocks.WEATHER_VANE.get(), new Item.Properties()));

    public static final DeferredItem<Item> WIND_COMPASS_ITEM = ITEMS.register("wind_compass",
            () -> new WindCompassBlockItem(ModBlocks.WIND_COMPASS.get(), new Item.Properties()));

    public static final DeferredItem<Item> REINFORCED_FABRIC = ITEMS.register("reinforced_fabric",
            () -> new Item(new Item.Properties()));

    public static final Map<String, DeferredItem<Item>> SMALL_BALLOON_ENVELOPES = new HashMap<>();

    public static void registerBalloonEnvelopes() {
        for (String color : VANILLA_COLORS) {
            String name = "small_" + color + "_balloon_envelope";
            DeferredItem<Item> envelopeItem = ITEMS.register(name,
                    () -> new Item(new Item.Properties()));
            SMALL_BALLOON_ENVELOPES.put(color, envelopeItem);
        }
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
