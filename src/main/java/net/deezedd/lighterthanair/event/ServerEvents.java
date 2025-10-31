package net.deezedd.lighterthanair.event;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.network.ModMessages;
import net.deezedd.lighterthanair.network.packet.WindDirectionSyncS2CPacket;
import net.deezedd.lighterthanair.util.ModGameRules;
import net.deezedd.lighterthanair.world.WindDirectionSavedData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = LighterThanAir.MODID)
public class ServerEvents {

    private static int stormTickCounter = 0;
    private static boolean wasStormingLastTick = false;
    private static int preStormDirection = 0;

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        ServerLevel overworld = event.getServer().overworld();
        if (overworld == null) return;
        GameRules gameRules = overworld.getGameRules();

        // 1. Globální kontrola - pokud je vítr vypnutý, neděláme nic
        if (!gameRules.getBoolean(ModGameRules.RULE_WINDENABLED)) {
            if (wasStormingLastTick) wasStormingLastTick = false; // Reset bouřky
            return;
        }

        WindDirectionSavedData windData = WindDirectionSavedData.get(overworld);
        long currentTime = overworld.getGameTime();

        // 2. Kontrola bouřky (má nejvyšší prioritu)
        boolean chaoticStorms = gameRules.getBoolean(ModGameRules.RULE_WINDCHAOTICSTORMS);
        if (chaoticStorms && overworld.isThundering()) {
            // Bouřka právě začala?
            if (!wasStormingLastTick) {
                wasStormingLastTick = true;
                // Uložíme si aktuální směr (ať už byl zamčený nebo dynamický)
                preStormDirection = windData.getCurrentDirection();
                LighterThanAir.LOGGER.info("Chaotic storm started! Saving pre-storm direction: {}", preStormDirection);
            }

            stormTickCounter++;
            if (stormTickCounter >= 100) { // Každých 5 sekund
                stormTickCounter = 0;
                int newDirection = overworld.random.nextInt(8); // 0-7
                windData.setDirection(newDirection);
                ModMessages.sendToAllClients(new WindDirectionSyncS2CPacket(newDirection));
                LighterThanAir.LOGGER.info("Chaotic Storm! Wind changed to index: {}", newDirection);
            }
            return; // Přeskočíme normální logiku
        }

        // 3. Logika po bouřce (přechod)
        if (wasStormingLastTick && !(chaoticStorms && overworld.isThundering())) {
            wasStormingLastTick = false;
            stormTickCounter = 0;
            LighterThanAir.LOGGER.info("Storm ended. Re-evaluating wind direction...");

            boolean isLocked = gameRules.getBoolean(ModGameRules.RULE_WINDDIRECTIONLOCK);
            int directionToRestore;

            if (isLocked) {
                // Případ 1: Hráč má zamčený směr -> vrátíme směr před bouřkou
                directionToRestore = preStormDirection;
                LighterThanAir.LOGGER.info("Reverting to locked direction: {}", directionToRestore);
            } else {
                // --- SPRÁVNÝ VÝPOČET A NASTAVENÍ ČASOVAČE ---
                directionToRestore = windData.getCurrentDirection();
                int minTicks = gameRules.getInt(ModGameRules.RULE_WINDMINDURATIONTICKS);
                int randTicks = gameRules.getInt(ModGameRules.RULE_WINDRANDOMDURATIONTICKS); // Používáme přejmenované pravidlo
                if (randTicks < 0) randTicks = 0;

                int duration = minTicks + overworld.random.nextInt(randTicks + 1);

                // Nastavíme POUZE časovač, směr neměníme
                windData.setNextChangeTick(currentTime + duration);

                LighterThanAir.LOGGER.info("Storm ended (dynamic). Keeping dir {}. Next change in {} ticks (min={}, rand={})",
                        directionToRestore, duration, minTicks, randTicks);
                // --- KONEC OPRAVY ---
            }

            windData.setDirection(directionToRestore); // Obnovíme směr
            ModMessages.sendToAllClients(new WindDirectionSyncS2CPacket(directionToRestore)); // Pošleme update
        }

        // 4. Normální logika (pokud není bouřka a NENÍ zamčeno)
        if (gameRules.getBoolean(ModGameRules.RULE_WINDDIRECTIONLOCK)) {

            // --- OPRAVA ZDE (proti spamu) ---
            // Získáme obě hodnoty
            int savedDataDir = windData.getCurrentDirection();
            GameRules.IntegerValue rule = gameRules.getRule(ModGameRules.RULE_WINDDIRECTION);

            // Synchronizujeme gamerule, POUZE pokud se liší
            if (rule.get() != savedDataDir) {
                LighterThanAir.LOGGER.info("Syncing windLocked gamerule to saved data: {}", savedDataDir);
                rule.set(savedDataDir, event.getServer()); // Toto spustí listener v ModGameRules
            }
            // --- KONEC OPRAVY ---

            windData.setNextChangeTick(currentTime + 100);
            return;
        }

        // 5. Dynamická změna (pokud není bouřka, není zamčeno)
        if (currentTime >= windData.getNextChangeTick()) {
            int minTicks = gameRules.getInt(ModGameRules.RULE_WINDMINDURATIONTICKS);
            int randTicks = gameRules.getInt(ModGameRules.RULE_WINDRANDOMDURATIONTICKS); // Používáme přejmenované pravidlo
            if (randTicks < 0) randTicks = 0; // Pojistka

            // Přidáme logování pro kontrolu hodnot
            LighterThanAir.LOGGER.debug("Calculating next wind change: minTicks={}, randTicks={}", minTicks, randTicks);

            int duration = minTicks + overworld.random.nextInt(randTicks + 1);

            // --- SPRÁVNÉ VOLÁNÍ METODY ---
            // Voláme metodu jen se 2 argumenty (level a finální duration)
            windData.setRandomDirectionAndPlanNext(overworld, duration);
            // --- KONEC OPRAVY ---

            int newDirection = windData.getCurrentDirection();

            // ... (zbytek kódu, set gamerule, log, odeslání packetu) ...
            gameRules.getRule(ModGameRules.RULE_WINDDIRECTION).set(newDirection, event.getServer());
            LighterThanAir.LOGGER.info("Wind direction randomized to: {} (Next change in {} ticks)", newDirection, duration); // Logujeme duration
            ModMessages.sendToAllClients(new WindDirectionSyncS2CPacket(newDirection));
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            GameRules gameRules = player.serverLevel().getGameRules();
            int directionIndex = 0; // Výchozí pro vypnutý stav

            if (gameRules.getBoolean(ModGameRules.RULE_WINDENABLED)) {
                // Vždy pošleme aktuální, reálný směr větru uložený na serveru
                directionIndex = WindDirectionSavedData.get(player.serverLevel()).getCurrentDirection();
            }

            WindDirectionSyncS2CPacket packet = new WindDirectionSyncS2CPacket(directionIndex);
            ModMessages.sendToPlayer(player, packet);
            LighterThanAir.LOGGER.info("Sent initial wind direction {} (Enabled: {}) to player {}", directionIndex, gameRules.getBoolean(ModGameRules.RULE_WINDENABLED), player.getName().getString());
        }
    }
}
