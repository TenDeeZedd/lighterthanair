package net.deezedd.lighterthanair;

import net.deezedd.lighterthanair.client.ClientSetup;
import net.deezedd.lighterthanair.entity.ModEntities;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.deezedd.lighterthanair.client.renderer.entity.SmallBalloonRenderer;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = LighterThanAir.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
//@EventBusSubscriber(modid = LighterThanAir.MODID, value = Dist.CLIENT)
public class LighterThanAirClient {


    public LighterThanAirClient(IEventBus modEventBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::onRegisterKeyMappings);

        // ===== PŘIDAT TENTO LISTENER =====
        modEventBus.addListener(this::onRegisterRenderers);
        // =================================
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        ClientSetup.init(event);
    }

    // ===== PŘIDAT TUTO METODU =====
    private void onRegisterRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        ClientSetup.onRegisterEntityRenderers(event); // Voláme naši existující metodu

        // Zaregistrujeme renderer pro balón
        event.registerEntityRenderer(ModEntities.SMALL_BALLOON.get(), SmallBalloonRenderer::new);
    }
    // ==============================

    private void onRegisterKeyMappings(final RegisterKeyMappingsEvent event) {
        // Zde bude registrace kláves (pro stoupání/klesání)
    }

}
