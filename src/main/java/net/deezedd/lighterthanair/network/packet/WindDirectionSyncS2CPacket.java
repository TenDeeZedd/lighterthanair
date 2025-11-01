package net.deezedd.lighterthanair.network.packet;

import net.deezedd.lighterthanair.LighterThanAir;
import net.deezedd.lighterthanair.client.ClientWindData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record WindDirectionSyncS2CPacket(int direction, int strength) implements CustomPacketPayload {

    // --- OPRAVA ZDE ---
    // Použijeme ResourceLocation.fromNamespaceAndPath() místo new ResourceLocation()
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(LighterThanAir.MODID, "wind_direction_sync");
    // ------------------

    // 2. Definice TYPU packetu
    public static final CustomPacketPayload.Type<WindDirectionSyncS2CPacket> TYPE = new CustomPacketPayload.Type<>(ID);

    // 3. Definuje, jak se packet čte a zapisuje
    public static final StreamCodec<FriendlyByteBuf, WindDirectionSyncS2CPacket> STREAM_CODEC = StreamCodec.of(
            // --- OPRAVA ZDE ---
            // Použijeme lambdu, abychom zajistili správné pořadí (Buf, Packet)
            (buf, packet) -> packet.write(buf), // Metoda pro zápis
            // ------------------
            WindDirectionSyncS2CPacket::new    // Metoda pro čtení (konstruktor z FriendlyByteBuf)
    );

    // 4. Konstruktor pro čtení (vyžadován StreamCodec)
    public WindDirectionSyncS2CPacket(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readInt());
    }

    // 5. Metoda pro zápis (vyžadována StreamCodec)
    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.direction);
        buf.writeInt(this.strength);
    }

    // 6. Implementace požadované metody type()
    @Override
    public CustomPacketPayload.Type<WindDirectionSyncS2CPacket> type() {
        return TYPE;
    }

    // 7. Handler (logika zůstává stejná)
    public void handle(IPayloadContext context) {
        context.enqueueWork(() -> {
            // Toto běží na klientovi
            ClientWindData.setCurrentDirection(this.direction);
            ClientWindData.setCurrentStrength(this.strength);
        });
    }
}
