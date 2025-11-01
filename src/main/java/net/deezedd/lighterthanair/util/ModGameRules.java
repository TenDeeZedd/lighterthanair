package net.deezedd.lighterthanair.util;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.network.ModMessages;
import net.deezedd.lighterthanair.network.packet.WindDirectionSyncS2CPacket;
import net.deezedd.lighterthanair.world.WindDirectionSavedData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

public class ModGameRules {

    // ... (všechna gamerules zůstávají stejná)

    // --- Pravidla pro Směr Větru ---
    public static final GameRules.Key<GameRules.BooleanValue> RULE_WINDENABLED =
            GameRules.register("windEnabled", GameRules.Category.MISC, GameRules.BooleanValue.create(true,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Enabled set to: " + value.get()); // Logger L1
                        if (value.get()) { // P3 a P7
                            triggerDirectionUpdate(server, true); // Při zapnutí ihned změní směr
                        }
                    }));

    public static final GameRules.Key<GameRules.BooleanValue> RULE_WINDLOCKDIRECTION =
            GameRules.register("windLockDirection", GameRules.Category.MISC, GameRules.BooleanValue.create(false,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Direction Lock set to: " + value.get()); // Logger L3
                        if (!value.get()) { // P3
                            triggerDirectionUpdate(server, false); // Při odemčení resetuje časovač
                        }
                    }));

    // P1: Přidáno pravidlo pro manuální nastavení směru
    public static final GameRules.Key<GameRules.IntegerValue> RULE_WINDSETDIRECTION =
            GameRules.register("windSetDirection", GameRules.Category.MISC, GameRules.IntegerValue.create(0,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Direction manually set to: " + value.get() + " (Will apply if lock is enabled)"); // Logger L2
                    }));

    public static final GameRules.Key<GameRules.IntegerValue> RULE_WINDDIRECTIONBASEINTERVAL =
            GameRules.register("windDirectionBaseInterval", GameRules.Category.MISC, GameRules.IntegerValue.create(300,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Direction Base Interval set to: " + value.get() + "s");
                        triggerDirectionUpdate(server, false); // P8: Reset časovače
                    }));

    public static final GameRules.Key<GameRules.IntegerValue> RULE_WINDDIRECTIONRANDOMINTERVAL =
            GameRules.register("windDirectionRandomInterval", GameRules.Category.MISC, GameRules.IntegerValue.create(300,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Direction Random Interval set to: " + value.get() + "s");
                        triggerDirectionUpdate(server, false); // P8: Reset časovače
                    }));

    // P5: Přejmenováno
    public static final GameRules.Key<GameRules.BooleanValue> RULE_WINDDIRECTIONCHAOTICSTORMS =
            GameRules.register("windDirectionChaoticStorms", GameRules.Category.MISC, GameRules.BooleanValue.create(true,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Direction Chaotic Storms set to: " + value.get()); // Logger L5
                    }));


    // --- Pravidla pro Sílu Větru ---
    public static final GameRules.Key<GameRules.BooleanValue> RULE_WINDSTRENGTHEABLED =
            GameRules.register("windStrengthEnabled", GameRules.Category.MISC, GameRules.BooleanValue.create(true,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Strength Enabled set to: " + value.get()); // Logger L1
                        if (value.get()) { // P3 a P7
                            triggerStrengthUpdate(server, true); // Při zapnutí ihned změní sílu
                        }
                    }));

    public static final GameRules.Key<GameRules.BooleanValue> RULE_WINDLOCKSTRENGTH =
            GameRules.register("windLockStrength", GameRules.Category.MISC, GameRules.BooleanValue.create(false,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Strength Lock set to: " + value.get()); // Logger L3
                        if (!value.get()) { // P3
                            triggerStrengthUpdate(server, false); // Při odemčení resetuje časovač
                        }
                    }));

    // P2: Změněn default z -1 na 0
    public static final GameRules.Key<GameRules.IntegerValue> RULE_WINDSETSTRENGTH =
            GameRules.register("windSetStrength", GameRules.Category.MISC, GameRules.IntegerValue.create(0,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Strength manually set to: " + value.get() + " (Will apply if lock is enabled)"); // Logger L2
                    }));

    public static final GameRules.Key<GameRules.IntegerValue> RULE_WINDSTRENGTHBASEINTERVAL =
            GameRules.register("windStrengthBaseInterval", GameRules.Category.MISC, GameRules.IntegerValue.create(300,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Strength Base Interval set to: " + value.get() + "s");
                        triggerStrengthUpdate(server, false); // P8: Reset časovače
                    }));

    public static final GameRules.Key<GameRules.IntegerValue> RULE_WINDSTRRANDOMINTERVAL =
            GameRules.register("windStrengthRandomInterval", GameRules.Category.MISC, GameRules.IntegerValue.create(300,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Strength Random Interval set to: " + value.get() + "s");
                        triggerStrengthUpdate(server, false); // P8: Reset časovače
                    }));

    // P5: Přidáno
    public static final GameRules.Key<GameRules.BooleanValue> RULE_WINDSTRENGTHCHAOTICSTORMS =
            GameRules.register("windStrengthChaoticStorms", GameRules.Category.MISC, GameRules.BooleanValue.create(true,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Strength Chaotic Storms set to: " + value.get()); // Logger L5
                    }));


    // --- Pomocné metody pro resetování časovačů (P3, P7, P8) ---

    public static void triggerDirectionUpdate(MinecraftServer server, boolean forceNewValue) {
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) return;

        WindDirectionSavedData windData = WindDirectionSavedData.get(overworld);
        GameRules gameRules = overworld.getGameRules();

        // Zámek a master-disable mají přednost
        if (!gameRules.getBoolean(RULE_WINDENABLED) || gameRules.getBoolean(RULE_WINDLOCKDIRECTION)) {
            return;
        }

        if (forceNewValue) {
            windData.setRandomDirectionInternal(overworld.random);
        }

        // ===== OPRAVA ZDE: Logika pro výpočet doby trvání je nyní centralizovaná zde =====
        boolean chaoticStorms = gameRules.getBoolean(RULE_WINDDIRECTIONCHAOTICSTORMS);
        boolean isThundering = overworld.isThundering();
        int duration;

        if (chaoticStorms && isThundering) {
            // Speciální interval pro bouřky (1-5 sekund)
            duration = (1 + overworld.random.nextInt(5)) * 20;
            LighterThanAir.LOGGER.info("Chaotic Storm Direction tick!");
        } else {
            // Standardní interval
            int baseInterval = gameRules.getInt(RULE_WINDDIRECTIONBASEINTERVAL) * 20;
            int randomInterval = gameRules.getInt(RULE_WINDDIRECTIONRANDOMINTERVAL) * 20;
            duration = baseInterval + overworld.random.nextInt(randomInterval + 1);
            LighterThanAir.LOGGER.info("Dynamic Wind Direction tick!");
        }
        // ==============================================================================

        windData.setNextChangeTick(overworld.getGameTime() + duration);
        LighterThanAir.LOGGER.info("New Wind Direction timer set. Current Direction: " + windData.getCurrentDirection() + ". Next change in " + (duration / 20) + " seconds.");
    }

    public static void triggerStrengthUpdate(MinecraftServer server, boolean forceNewValue) {
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) return;

        WindDirectionSavedData windData = WindDirectionSavedData.get(overworld);
        GameRules gameRules = overworld.getGameRules();

        // Zámek a master-disable mají přednost
        if (!gameRules.getBoolean(RULE_WINDSTRENGTHEABLED) || gameRules.getBoolean(RULE_WINDLOCKSTRENGTH)) {
            return;
        }

        // ===== OPRAVA ZDE: Logika pro výpočet doby trvání A HODNOTY je nyní centralizovaná zde =====
        boolean chaoticStorms = gameRules.getBoolean(RULE_WINDSTRENGTHCHAOTICSTORMS);
        boolean isThundering = overworld.isThundering();
        int duration;

        if (forceNewValue) {
            if (chaoticStorms && isThundering) {
                windData.setStrength(4); // Vynutíme sílu 4
            } else {
                windData.setRandomStrengthInternal(overworld); // Vypočítá sílu podle počasí
            }
        }
        // else: ponechá stávající hodnotu (jen resetuje časovač)

        if (chaoticStorms && isThundering) {
            // Speciální interval pro bouřky (1-5 sekund)
            duration = (1 + overworld.random.nextInt(5)) * 20;
            LighterThanAir.LOGGER.info("Chaotic Storm Strength tick!");
        } else {
            // Standardní interval
            int baseInterval = gameRules.getInt(RULE_WINDSTRENGTHBASEINTERVAL) * 20;
            int randomInterval = gameRules.getInt(RULE_WINDSTRRANDOMINTERVAL) * 20;
            duration = baseInterval + overworld.random.nextInt(randomInterval + 1);
            LighterThanAir.LOGGER.info("Dynamic Wind Strength tick!");
        }
        // =======================================================================================

        windData.setNextStrengthChangeTick(overworld.getGameTime() + duration);
        LighterThanAir.LOGGER.info("New Wind Strength timer set. Current Strength: " + windData.getCurrentStrength() + ". Next change in " + (duration / 20) + " seconds.");
    }

    public static void register() {
        // Tato metoda je volána v LighterThanAir.java, aby se pravidla načetla
    }
}
