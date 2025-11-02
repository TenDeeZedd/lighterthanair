package net.deezedd.lighterthanair;

import net.deezedd.lighterthanair.block.ModBlocks;
import net.deezedd.lighterthanair.blockentity.ModBlockEntities;
import net.deezedd.lighterthanair.datagen.DataGenerators;
import net.deezedd.lighterthanair.item.ModCreativeTabs;
import net.deezedd.lighterthanair.item.ModItems;
import net.deezedd.lighterthanair.network.ModMessages;
import net.deezedd.lighterthanair.sound.ModSounds;
import net.deezedd.lighterthanair.util.ModGameRules;
import net.deezedd.lighterthanair.util.recipe.ModRecipes;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(LighterThanAir.MODID)
public class LighterThanAir {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "dzlta";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();



    public LighterThanAir(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);


        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);

        ModBlocks.registerBalloonCrates();

        ModCreativeTabs.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModSounds.register(modEventBus);
        ModRecipes.register(modEventBus);
        ModMessages.register(modEventBus);
        modEventBus.addListener(DataGenerators::gatherData);
        ModGameRules.register();


        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (LighterThanAir) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {

    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts

    }
}
