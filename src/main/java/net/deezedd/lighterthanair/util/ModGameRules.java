package net.deezedd.lighterthanair.util;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.network.ModMessages;
import net.deezedd.lighterthanair.network.packet.WindDirectionSyncS2CPacket;
import net.deezedd.lighterthanair.world.WindDirectionSavedData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.GameRules;

import java.util.function.BiConsumer;

public class ModGameRules {

    public static GameRules.Key<GameRules.BooleanValue> RULE_WINDENABLED;
    public static GameRules.Key<GameRules.BooleanValue> RULE_WINDLOCKED;
    public static GameRules.Key<GameRules.IntegerValue> RULE_WINDDIRECTION;
    public static GameRules.Key<GameRules.IntegerValue> RULE_WINDMINDURATIONTICKS;
    public static GameRules.Key<GameRules.IntegerValue> RULE_WINDRANDOMDURATIONTICKS;
    public static GameRules.Key<GameRules.BooleanValue> RULE_WINDCHAOTICSTORMS;

    // --- Metoda pro registraci ---
    public static void register() {
        // Použijeme existující kategorii MISC
        RULE_WINDENABLED = register("windEnabled", GameRules.Category.MISC, GameRules.BooleanValue.create(true, ModGameRules::onWindEnabledChanged));
        RULE_WINDLOCKED = register("windLocked", GameRules.Category.MISC, GameRules.BooleanValue.create(false)); // Lock nemá listener, řeší se v onServerTick
        RULE_WINDDIRECTION = registerInteger("windDirection", GameRules.Category.MISC, 0, ModGameRules::onWindDirectionChanged);        // --- REGISTRACE NOVÝCH PRAVIDEL ---
        // 10 minut = 10 * 60 * 20 = 12000 ticků
        RULE_WINDMINDURATIONTICKS = registerInteger("windMinDurationTicks", GameRules.Category.MISC, 12000);
        // 15 minut = 15 * 60 * 20 = 18000 ticků
        RULE_WINDRANDOMDURATIONTICKS = registerInteger("windRandomDurationTicks", GameRules.Category.MISC, 18000);

        RULE_WINDCHAOTICSTORMS = registerBoolean("windChaoticStorms", GameRules.Category.MISC, true);
    }

    // Tuto metodu zavoláme z hlavní třídy modu pro spuštění registrace
    public static void init() {
        // Dummy call to ensure class loads and registers
    }

    private static void onWindEnabledChanged(MinecraftServer server, GameRules.BooleanValue rule) {
        boolean enabled = rule.get();
        GameRules gameRules = server.getGameRules();
        GameRules.IntegerValue directionRule = gameRules.getRule(RULE_WINDDIRECTION); // Použijeme opravený název
        int directionIndex = 0; // Výchozí pro vypnutý stav

        if (!enabled) {
            LighterThanAir.LOGGER.info("Wind disabled. Setting client direction to 0 (North).");
            // Pošleme 0, ale neměníme gamerule
            WindDirectionSyncS2CPacket packet = new WindDirectionSyncS2CPacket(0);
            ModMessages.sendToAllClients(packet);
        } else {
            // Při zapnutí pošleme aktuálně nastavený směr
            directionIndex = directionRule.get();
            LighterThanAir.LOGGER.info("Wind enabled. Sending current direction: {}", directionIndex);
            WindDirectionSyncS2CPacket packet = new WindDirectionSyncS2CPacket(directionIndex);
            ModMessages.sendToAllClients(packet);
        }
    }

    private static void onWindDirectionChanged(MinecraftServer server, GameRules.IntegerValue rule) {
        if (server == null || server.overworld() == null) return;

        int newIndex = rule.get();
        int clampedIndex = newIndex;
        GameRules gameRules = server.getGameRules();

        if (newIndex < 0 || newIndex > 7) {
            clampedIndex = 0;
            LighterThanAir.LOGGER.warn("Invalid windDirection '{}' set. Clamping to 0 (North).", newIndex);
            rule.set(clampedIndex, server); // Spustí tento listener znovu
            return;
        }

        LighterThanAir.LOGGER.info("Wind direction changed via gamerule to: {}", newIndex);

        // Okamžitě pošleme update klientům
        WindDirectionSyncS2CPacket packet = new WindDirectionSyncS2CPacket(newIndex);
        ModMessages.sendToAllClients(packet);

        WindDirectionSavedData windData = WindDirectionSavedData.get(server.overworld());
        ServerLevel overworld = server.overworld();

        windData.setDirection(clampedIndex);

        if (!gameRules.getBoolean(ModGameRules.RULE_WINDLOCKED)) {
            int minTicks = gameRules.getInt(ModGameRules.RULE_WINDMINDURATIONTICKS);
            int randTicks = gameRules.getInt(ModGameRules.RULE_WINDRANDOMDURATIONTICKS);
            if (randTicks < 0) randTicks = 0; // Pojistka

            int duration = minTicks + overworld.random.nextInt(randTicks + 1);
            windData.setNextChangeTick(overworld.getGameTime() + duration);
            LighterThanAir.LOGGER.info("Manual direction set. Restarting dynamic timer. Next change in {} ticks", duration);
        }

        // Musíme také aktualizovat WindDirectionSavedData, aby bouřka věděla, kam se vrátit
        if (server != null && server.overworld() != null) {
            WindDirectionSavedData.get(server.overworld()).setDirection(newIndex);
        }
    }

    private static <T extends GameRules.Value<T>> GameRules.Key<T> register(String name, GameRules.Category category, GameRules.Type<T> type) {
        return GameRules.register(name, category, type);
    }

    private static GameRules.Type<GameRules.BooleanValue> createBoolean(boolean defaultValue, BiConsumer<MinecraftServer, GameRules.BooleanValue> listener) {
        return GameRules.BooleanValue.create(defaultValue, listener);
    }

    private static GameRules.Key<GameRules.BooleanValue> registerBoolean(String name, GameRules.Category category, boolean defaultValue, BiConsumer<MinecraftServer, GameRules.BooleanValue> listener) {
        return register(name, category, createBoolean(defaultValue, listener));
    }

    private static GameRules.Key<GameRules.BooleanValue> registerBoolean(String name, GameRules.Category category, boolean defaultValue) {
        return register(name, category, GameRules.BooleanValue.create(defaultValue));
    }

    // Pro Integer s listenerem
    private static GameRules.Type<GameRules.IntegerValue> createInteger(int defaultValue, BiConsumer<MinecraftServer, GameRules.IntegerValue> listener) {
        return GameRules.IntegerValue.create(defaultValue, listener);
    }

    // Nová pomocná metoda pro registraci Integer s listenerem
    private static GameRules.Key<GameRules.IntegerValue> registerInteger(String name, GameRules.Category category, int defaultValue, BiConsumer<MinecraftServer, GameRules.IntegerValue> listener) {
        return register(name, category, createInteger(defaultValue, listener));
    }

    // Pro Min/Max Ticks (bez listeneru)
    private static GameRules.Key<GameRules.IntegerValue> registerInteger(String name, GameRules.Category category, int defaultValue) {
        return register(name, category, GameRules.IntegerValue.create(defaultValue));
    }

}
