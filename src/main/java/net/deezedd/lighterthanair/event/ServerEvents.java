package net.deezedd.lighterthanair.event;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.network.ModMessages;
import net.deezedd.lighterthanair.network.packet.WindDirectionSyncS2CPacket;
import net.deezedd.lighterthanair.util.ModGameRules;
import net.deezedd.lighterthanair.world.WindDirectionSavedData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.Random;

@EventBusSubscriber(modid = LighterThanAir.MODID)
public class ServerEvents {

    private static boolean wasThundering = false;
    private static boolean wasRaining = false;
    private static long weatherChangeScheduledTick = 0;
    private static final int WEATHER_CHANGE_DELAY_TICKS = 40; // 2 sec

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (event.getServer().getTickCount() % 20 == 0) {
            ServerLevel overworld = event.getServer().getLevel(Level.OVERWORLD);
            if (overworld == null) return;

            MinecraftServer server = overworld.getServer();
            GameRules gameRules = overworld.getGameRules();
            WindDirectionSavedData windData = WindDirectionSavedData.get(overworld);

            long gameTime = overworld.getGameTime();
            boolean isThundering = overworld.isThundering();
            boolean isRaining = overworld.isRaining();

            // 1. Master control
            if (!gameRules.getBoolean(ModGameRules.RULE_WINDENABLED)) {
                windData.setDirection(0);
                windData.setStrength(0);
                syncWindToAllPlayers(overworld, windData.getCurrentDirection(), windData.getCurrentStrength());
                wasThundering = false;
                wasRaining = false;
                weatherChangeScheduledTick = 0;
                windData.setStormAnchorInitialized(false);
                return;
            }

            // 2. Delayed weather response logic
            boolean weatherChanged = (isThundering != wasThundering || isRaining != wasRaining);

            if (weatherChanged) {
                if (wasThundering && !isThundering) {
                    LighterThanAir.LOGGER.info("Thunderstorm ended, resetting wind anchor.");
                    windData.setStormAnchorInitialized(false);
                }
                LighterThanAir.LOGGER.info("Weather state changed (T:" + isThundering + ", R:" + isRaining + "). Scheduling wind update.");
                weatherChangeScheduledTick = gameTime + WEATHER_CHANGE_DELAY_TICKS;
            }

            if (weatherChangeScheduledTick > 0 && gameTime >= weatherChangeScheduledTick) {
                if (!weatherChanged) {
                    LighterThanAir.LOGGER.info("Executing scheduled weather update for stable weather (T:" + isThundering + ", R:" + isRaining + ").");
                    ModGameRules.triggerDirectionUpdate(server, true);
                    ModGameRules.triggerStrengthUpdate(server, true);
                    weatherChangeScheduledTick = 0;
                }
            }

            // 3. Wind Strenght
            handleWindStrength(overworld, gameRules, windData, gameTime, server);

            // 4. Wind Direction
            handleWindDirection(overworld, gameRules, windData, gameTime, server);

            // 5. Synch
            syncWindToAllPlayers(overworld, windData.getCurrentDirection(), windData.getCurrentStrength());

            // 6. Save
            wasThundering = isThundering;
            wasRaining = isRaining;
        }
    }

    private static void handleWindStrength(ServerLevel overworld, GameRules gameRules, WindDirectionSavedData windData,
                                           long gameTime, MinecraftServer server) {

        if (gameRules.getBoolean(ModGameRules.RULE_WINDLOCKSTRENGTH)) {
            windData.setStrength(gameRules.getInt(ModGameRules.RULE_WINDSETSTRENGTH));
            return;
        }

        if (!gameRules.getBoolean(ModGameRules.RULE_WINDSTRENGTHEABLED)) {
            windData.setStrength(0);
            return;
        }

        if (gameTime < windData.getNextStrengthChangeTick()) {
            return;
        }

        ModGameRules.triggerStrengthUpdate(server, true);
    }

    private static void handleWindDirection(ServerLevel overworld, GameRules gameRules, WindDirectionSavedData windData,
                                            long gameTime, MinecraftServer server) {

        if (gameRules.getBoolean(ModGameRules.RULE_WINDLOCKDIRECTION)) {
            windData.setDirection(gameRules.getInt(ModGameRules.RULE_WINDSETDIRECTION));
            return;
        }

        if (gameTime < windData.getNextChangeTick()) {
            return;
        }

        ModGameRules.triggerDirectionUpdate(server, true);
    }


    private static void syncWindToAllPlayers(ServerLevel level, int direction, int strength) {
        WindDirectionSyncS2CPacket packet = new WindDirectionSyncS2CPacket(direction, strength);
        for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
            ModMessages.sendToPlayer(packet, player);
        }
    }
}