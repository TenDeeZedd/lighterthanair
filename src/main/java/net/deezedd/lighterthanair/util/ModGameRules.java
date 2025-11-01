package net.deezedd.lighterthanair.util;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.network.ModMessages;
import net.deezedd.lighterthanair.network.packet.WindDirectionSyncS2CPacket;
import net.deezedd.lighterthanair.world.WindDirectionSavedData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

import java.util.function.BiConsumer;

public class ModGameRules {

    // --- Pravidla pro Směr Větru ---
    // (Definice gamerules zůstávají stejné, včetně P1 a P2...)
    public static final GameRules.Key<GameRules.BooleanValue> RULE_WINDENABLED =
            GameRules.register("windEnabled", GameRules.Category.MISC, GameRules.BooleanValue.create(true,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Enabled set to: " + value.get());
                        if (value.get()) {
                            triggerDirectionUpdate(server, true);
                        }
                    }));

    public static final GameRules.Key<GameRules.BooleanValue> RULE_WINDLOCKDIRECTION =
            GameRules.register("windLockDirection", GameRules.Category.MISC, GameRules.BooleanValue.create(false,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Direction Lock set to: " + value.get());
                        if (!value.get()) {
                            triggerDirectionUpdate(server, false);
                        }
                    }));

    public static final GameRules.Key<GameRules.IntegerValue> RULE_WINDSETDIRECTION =
            GameRules.register("windSetDirection", GameRules.Category.MISC, GameRules.IntegerValue.create(0,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Direction manually set to: " + value.get());
                        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
                        if (overworld == null) return;
                        GameRules gameRules = overworld.getGameRules();
                        if (!gameRules.getBoolean(RULE_WINDENABLED)) return;

                        WindDirectionSavedData windData = WindDirectionSavedData.get(overworld);
                        windData.setDirection(value.get());

                        if (!gameRules.getBoolean(RULE_WINDLOCKDIRECTION)) {
                            triggerDirectionUpdate(server, false);
                        }
                    }));

    public static final GameRules.Key<GameRules.IntegerValue> RULE_WINDDIRECTIONBASEINTERVAL =
            GameRules.register("windDirectionBaseInterval", GameRules.Category.MISC, GameRules.IntegerValue.create(300,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Direction Base Interval set to: " + value.get() + "s");
                        triggerDirectionUpdate(server, false);
                    }));

    public static final GameRules.Key<GameRules.IntegerValue> RULE_WINDDIRECTIONRANDOMINTERVAL =
            GameRules.register("windDirectionRandomInterval", GameRules.Category.MISC, GameRules.IntegerValue.create(300,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Direction Random Interval set to: " + value.get() + "s");
                        triggerDirectionUpdate(server, false);
                    }));

    public static final GameRules.Key<GameRules.BooleanValue> RULE_WINDDIRECTIONCHAOTICSTORMS =
            GameRules.register("windDirectionChaoticStorms", GameRules.Category.MISC, GameRules.BooleanValue.create(true,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Direction Chaotic Storms set to: " + value.get());
                    }));


    // --- Pravidla pro Sílu Větru ---
    // (Beze změny)
    public static final GameRules.Key<GameRules.BooleanValue> RULE_WINDSTRENGTHEABLED =
            GameRules.register("windStrengthEnabled", GameRules.Category.MISC, GameRules.BooleanValue.create(true,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Strength Enabled set to: " + value.get());
                        if (value.get()) {
                            triggerStrengthUpdate(server, true);
                        }
                    }));

    public static final GameRules.Key<GameRules.BooleanValue> RULE_WINDLOCKSTRENGTH =
            GameRules.register("windLockStrength", GameRules.Category.MISC, GameRules.BooleanValue.create(false,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Strength Lock set to: " + value.get());
                        if (!value.get()) {
                            triggerStrengthUpdate(server, false);
                        }
                    }));

    public static final GameRules.Key<GameRules.IntegerValue> RULE_WINDSETSTRENGTH =
            GameRules.register("windSetStrength", GameRules.Category.MISC, GameRules.IntegerValue.create(0,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Strength manually set to: " + value.get());
                        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
                        if (overworld == null) return;
                        GameRules gameRules = overworld.getGameRules();
                        if (!gameRules.getBoolean(RULE_WINDSTRENGTHEABLED)) return;

                        WindDirectionSavedData windData = WindDirectionSavedData.get(overworld);
                        windData.setStrength(value.get());

                        if (!gameRules.getBoolean(RULE_WINDLOCKSTRENGTH)) {
                            triggerStrengthUpdate(server, false);
                        }
                    }));

    public static final GameRules.Key<GameRules.IntegerValue> RULE_WINDSTRENGTHBASEINTERVAL =
            GameRules.register("windStrengthBaseInterval", GameRules.Category.MISC, GameRules.IntegerValue.create(300,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Strength Base Interval set to: " + value.get() + "s");
                        triggerStrengthUpdate(server, false);
                    }));

    public static final GameRules.Key<GameRules.IntegerValue> RULE_WINDSTRRANDOMINTERVAL =
            GameRules.register("windStrengthRandomInterval", GameRules.Category.MISC, GameRules.IntegerValue.create(300,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Strength Random Interval set to: " + value.get() + "s");
                        triggerStrengthUpdate(server, false);
                    }));

    public static final GameRules.Key<GameRules.BooleanValue> RULE_WINDSTRENGTHCHAOTICSTORMS =
            GameRules.register("windStrengthChaoticStorms", GameRules.Category.MISC, GameRules.BooleanValue.create(true,
                    (server, value) -> {
                        LighterThanAir.LOGGER.info("Wind Strength Chaotic Storms set to: " + value.get());
                    }));


    // --- Pomocné metody pro resetování časovačů ---

    public static void triggerDirectionUpdate(MinecraftServer server, boolean forceNewValue) {
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) return;

        WindDirectionSavedData windData = WindDirectionSavedData.get(overworld);
        GameRules gameRules = overworld.getGameRules();
        RandomSource random = overworld.random;

        if (!gameRules.getBoolean(RULE_WINDENABLED) || gameRules.getBoolean(RULE_WINDLOCKDIRECTION)) {
            return;
        }

        boolean chaoticStorms = gameRules.getBoolean(RULE_WINDDIRECTIONCHAOTICSTORMS);
        boolean isThundering = overworld.isThundering();
        int duration;
        String logMessage;

        // ===== OPRAVA ZDE (Logika kmitající kotvy) =====
        if (chaoticStorms && isThundering) {
            // --- CHAOTICKÁ BOUŘKA ---
            duration = (3 + random.nextInt(3)) * 20; // 3-5 sekund
            logMessage = "Chaotic Storm Direction tick!";

            if (forceNewValue) {
                // 1. Inicializujeme kotvu, pokud ještě není
                if (!windData.isStormAnchorInitialized()) {
                    LighterThanAir.LOGGER.info("Storm anchor not set. Anchoring to current direction: " + windData.getCurrentDirection());
                    windData.setStormAnchorDirection(windData.getCurrentDirection());
                    windData.setStormAnchorInitialized(true);
                }

                int anchorDir = windData.getStormAnchorDirection();

                // 2. Vypočítáme "kmitající" směr (vždy -1, 0, nebo +1 od kotvy)
                int change = random.nextInt(3) - 1; // Výsledek je -1, 0, nebo 1
                int newDir = (anchorDir + change + 8) % 8; // (+8 pro zajištění kladného modula)

                // Nastavíme tento nový kmitající směr
                windData.setDirection(newDir);

                // 3. Máme 10% šanci, že posuneme "kotvu" na tento nový směr
                if (random.nextFloat() < 0.10f) { // 10% šance
                    LighterThanAir.LOGGER.info("Wind storm anchor is drifting! New anchor: " + newDir);
                    windData.setStormAnchorDirection(newDir);
                }
            }
            // else: !forceNewValue (jen resetujeme časovač, směr ponecháme)

        } else {
            // --- DYNAMICKÁ (NORMÁLNÍ) ZMĚNA ---

            // Pokud byla kotva nastavena (bouřka právě skončila), resetujeme ji
            if (windData.isStormAnchorInitialized()) {
                LighterThanAir.LOGGER.info("Non-storm tick. Resetting wind anchor.");
                windData.setStormAnchorInitialized(false);
            }

            int baseInterval = gameRules.getInt(RULE_WINDDIRECTIONBASEINTERVAL) * 20;
            int randomInterval = gameRules.getInt(RULE_WINDDIRECTIONRANDOMINTERVAL) * 20;
            duration = baseInterval + random.nextInt(randomInterval + 1);
            logMessage = "Dynamic Wind Direction tick!";

            if (forceNewValue) {
                windData.setRandomDirectionInternal(random); // Standardní náhodný směr 0-7
            }
        }

        LighterThanAir.LOGGER.info(logMessage);
        // ===================================================

        windData.setNextChangeTick(overworld.getGameTime() + duration);
        LighterThanAir.LOGGER.info("New Wind Direction timer set. Current Direction: " + windData.getCurrentDirection() + ". Next change in " + (duration / 20) + " seconds.");
    }

    // Metoda triggerStrengthUpdate zůstává stejná jako v předchozí verzi
    public static void triggerStrengthUpdate(MinecraftServer server, boolean forceNewValue) {
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) return;

        WindDirectionSavedData windData = WindDirectionSavedData.get(overworld);
        GameRules gameRules = overworld.getGameRules();
        RandomSource random = overworld.random;

        if (!gameRules.getBoolean(RULE_WINDSTRENGTHEABLED) || gameRules.getBoolean(RULE_WINDLOCKSTRENGTH)) {
            return;
        }

        boolean chaoticStorms = gameRules.getBoolean(RULE_WINDSTRENGTHCHAOTICSTORMS);
        boolean isThundering = overworld.isThundering();
        int duration;
        String logMessage;

        if (forceNewValue) {
            if (chaoticStorms && isThundering) {
                windData.setStrength(4);
            } else {
                windData.setRandomStrengthInternal(overworld);
            }
        }

        if (chaoticStorms && isThundering) {
            duration = (3 + random.nextInt(3)) * 20; // 3-5 sekund
            logMessage = "Chaotic Storm Strength tick!";
        } else {
            int baseInterval = gameRules.getInt(RULE_WINDSTRENGTHBASEINTERVAL) * 20;
            int randomInterval = gameRules.getInt(RULE_WINDSTRRANDOMINTERVAL) * 20;
            duration = baseInterval + random.nextInt(randomInterval + 1);
            logMessage = "Dynamic Wind Strength tick!";
        }

        LighterThanAir.LOGGER.info(logMessage);

        windData.setNextStrengthChangeTick(overworld.getGameTime() + duration);
        LighterThanAir.LOGGER.info("New Wind Strength timer set. Current Strength: " + windData.getCurrentStrength() + ". Next change in " + (duration / 20) + " seconds.");
    }

    public static void register() {
        // Tato metoda je volána v LighterThanAir.java, aby se pravidla načetla
    }
}
