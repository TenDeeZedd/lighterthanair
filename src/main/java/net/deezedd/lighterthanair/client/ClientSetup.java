package net.deezedd.lighterthanair.client;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.block.ModBlocks;
import net.deezedd.lighterthanair.client.renderer.block.WeatherVaneBlockRenderer;
import net.deezedd.lighterthanair.blockentity.ModBlockEntities;
import net.deezedd.lighterthanair.client.renderer.block.WindCompassBlockRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = LighterThanAir.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {

    }

    public static void init(FMLClientSetupEvent event) {

        // ===== OPRAVA ZDE (Problém 2) =====
        // Musíme všem bednám nastavit RenderType na CUTOUT

        // Projdeme všechny registrované bloky
        ModBlocks.BLOCKS.getEntries().stream()
                // Vyfiltrujeme jen ty, které jsou bedny
                .filter(blockHolder -> blockHolder.getId().getPath().contains("_balloon_crate"))
                // Pro každou bednu nastavíme správný RenderLayer
                .forEach(blockHolder -> {
                    ItemBlockRenderTypes.setRenderLayer(blockHolder.get(), RenderType.cutout());
                });
        // ===================================
    }

    @SubscribeEvent
    public static void onRegisterEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.WEATHER_VANE_BE.get(), WeatherVaneBlockRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.WIND_COMPASS_BE.get(), WindCompassBlockRenderer::new);
    }

}
