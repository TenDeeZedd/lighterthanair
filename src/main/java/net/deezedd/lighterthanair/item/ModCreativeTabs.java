package net.deezedd.lighterthanair.item;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LighterThanAir.MODID);

    public static final Supplier<CreativeModeTab> LIGHTERTHANAIR =
            CREATIVE_MODE_TABS.register("lta_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.dzlta.lta_tab"))
                    .icon(() -> new ItemStack(ModBlocks.WEATHER_VANE))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.WEATHER_VANE_ITEM);
                        output.accept(ModItems.WIND_COMPASS_ITEM);

                        ModItems.ITEMS.getEntries().stream()
                                .filter(itemHolder -> itemHolder.getId().getPath().contains("_balloon_crate"))
                                .forEach(itemHolder -> output.accept(itemHolder.get()));



                    })
                    .build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
