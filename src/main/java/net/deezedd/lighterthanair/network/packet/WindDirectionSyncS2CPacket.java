package net.deezedd.lighterthanair.network.packet;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.client.ClientWindData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record WindDirectionSyncS2CPacket(int windDirection) implements CustomPacketPayload {
    // PayloadType zůstává stejný
    public static final Type<WindDirectionSyncS2CPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "wind_direction_sync"));

    // Definujeme StreamCodec podle moderního způsobu
    // Používá RegistryFriendlyByteBuf pro PLAY fázi
    public static final StreamCodec<RegistryFriendlyByteBuf, WindDirectionSyncS2CPacket> STREAM_CODEC = StreamCodec.of(
            // ZMĚNA ZDE: Použijeme lambda výraz, který zavolá naši instanční metodu write
            (buffer, packet) -> packet.write(buffer),
            // Čtení pomocí konstruktoru zůstává stejné
            WindDirectionSyncS2CPacket::new);

    // Konstruktor pro čtení z bufferu (musí brát RegistryFriendlyByteBuf)
    public WindDirectionSyncS2CPacket(final RegistryFriendlyByteBuf buffer) {
        this(buffer.readByte());
    }

    // Metoda pro zápis do bufferu (musí brát RegistryFriendlyByteBuf)
    // Už nemá @Override
    public void write(final RegistryFriendlyByteBuf buffer) {
        buffer.writeByte(windDirection);
    }

    // Metoda type() zůstává stejná
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    // Metoda handle() zůstává stejná
    public static void handle(final WindDirectionSyncS2CPacket msg, final IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientWindData.setWindDirection(msg.windDirection);
            // LighterThanAir.LOGGER.debug("Received wind direction sync packet: {}", msg.windDirection);
        });
    }
}
