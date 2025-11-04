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

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModMessages::registerPayloads);
    }

    private static void registerPayloads(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(LighterThanAir.MODID)
                .versioned("1.0");

        registrar.playToClient(
                WindDirectionSyncS2CPacket.TYPE,
                WindDirectionSyncS2CPacket.STREAM_CODEC,
                WindDirectionSyncS2CPacket::handle
        );
    }

    public static void sendToPlayer(CustomPacketPayload packet, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, packet);
    }
}
