package net.deezedd.lighterthanair.network;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.network.packet.WindDirectionSyncS2CPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload; // Důležitý pro 'send'
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent; // Důležitý pro @SubscribeEvent
import net.neoforged.fml.common.Mod; // Důležitý pro @Mod.EventBusSubscriber
import net.neoforged.neoforge.network.PacketDistributor; // Důležitý pro 'send'
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent; // Důležitý pro 'register'
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModMessages {

    /**
     * 2. Toto je "hlavní" registrační metoda, kterou voláš ze své LighterThanAir.java.
     * Přijímá IEventBus.
     */
    public static void register(IEventBus modEventBus) {
        // Přihlásíme naši metodu 'registerPayloads' k naslouchání eventu
        modEventBus.addListener(ModMessages::registerPayloads);
    }

    /**
     * 3. Tato metoda se spustí, až když NeoForge spustí event RegisterPayloadHandlersEvent.
     * Zde proběhne skutečná registrace packetu.
     */
    private static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        // Použijeme .registrar(MODID) místo .registrar()
        final PayloadRegistrar registrar = event.registrar(LighterThanAir.MODID)
                .versioned("1.0");

        // 4. Oprava chyb 'play' a 'client'
        //    Použijeme .playToClient() a přímou referenci na metodu ::handle
        registrar.playToClient(
                WindDirectionSyncS2CPacket.TYPE,
                WindDirectionSyncS2CPacket.STREAM_CODEC,
                WindDirectionSyncS2CPacket::handle
        );
    }

    /**
     * 5. Oprava chyby "Cannot resolve method 'send(Object)'"
     * Metoda 'sendToPlayer' teď vyžaduje 'CustomPacketPayload'
     * a používá 'PacketDistributor'.
     */
    public static void sendToPlayer(CustomPacketPayload packet, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, packet);
    }
}
