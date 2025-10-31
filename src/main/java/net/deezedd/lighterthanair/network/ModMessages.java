package net.deezedd.lighterthanair.network;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.network.packet.WindDirectionSyncS2CPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;


public final class ModMessages {

    public static void register(final IEventBus modEventBus) {
        modEventBus.addListener(ModMessages::registerPayloadHandlers);
    }

    private static void registerPayloadHandlers(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(LighterThanAir.MODID).versioned("1");

        // Použijeme správnou metodu playToClient
        registrar.playToClient(
                WindDirectionSyncS2CPacket.TYPE, // Typ packetu
                WindDirectionSyncS2CPacket.STREAM_CODEC, // Náš nový StreamCodec
                WindDirectionSyncS2CPacket::handle // Metoda pro zpracování na klientovi
        );

        // Zde bychom registrovali C2S packety pomocí playToServer
    }

    // --- METODY PRO ODESÍLÁNÍ --- (zůstávají stejné)
    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload message) {
        PacketDistributor.sendToPlayer(player, message);
    }
    public static void sendToAllClients(CustomPacketPayload message) {
        PacketDistributor.sendToAllPlayers(message);
    }
}
