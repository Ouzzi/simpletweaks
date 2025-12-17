package com.simpletweaks.network;

import com.simpletweaks.Simpletweaks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LaserManager {

    // Speichert die aktiven Laser-Positionen aller Spieler (Client-Side)
    public static final Map<UUID, LaserData> ACTIVE_LASERS = new ConcurrentHashMap<>();

    public static void register() {
        // Payload registrieren
        PayloadTypeRegistry.playC2S().register(LaserPayload.ID, LaserPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LaserPayload.ID, LaserPayload.CODEC);

        // Server empfängt Position vom Client -> Broadcast an alle anderen
        ServerPlayNetworking.registerGlobalReceiver(LaserPayload.ID, (payload, context) -> {
            ServerPlayerEntity sender = context.player();
            // Weiterleiten an alle anderen Spieler
            for (ServerPlayerEntity player : sender.getServerWorld().getPlayers()) {
                if (player != sender) {
                    ServerPlayNetworking.send(player, payload);
                }
            }
        });
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient() {
        // Client empfängt Position von anderen Spielern
        ClientPlayNetworking.registerGlobalReceiver(LaserPayload.ID, (payload, context) -> {
            // Speichern mit kurzem Timeout (wir erwarten ständige Updates)
            ACTIVE_LASERS.put(payload.playerUuid(), new LaserData(payload.pos(), System.currentTimeMillis()));
        });
    }

    // Hilfsklasse für Client-Daten
    public record LaserData(Vector3f pos, long timestamp) {}

    // Das Paket (Payload)
    public record LaserPayload(UUID playerUuid, Vector3f pos, boolean active) implements CustomPayload {
        public static final Id<LaserPayload> ID = new Id<>(Identifier.of(Simpletweaks.MOD_ID, "laser_pos"));
        public static final PacketCodec<RegistryByteBuf, LaserPayload> CODEC = PacketCodec.tuple(
                PacketCodecs.UUID, LaserPayload::playerUuid,
                PacketCodecs.VECTOR3F, LaserPayload::pos,
                PacketCodecs.BOOL, LaserPayload::active,
                LaserPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    // Aufräum-Methode (für Tick Event)
    public static void tick() {
        long now = System.currentTimeMillis();
        // Entferne Einträge, die älter als 100ms sind (kein Update erhalten)
        ACTIVE_LASERS.entrySet().removeIf(entry -> now - entry.getValue().timestamp > 200);
    }
}