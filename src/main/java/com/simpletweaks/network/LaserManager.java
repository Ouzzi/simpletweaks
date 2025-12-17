package com.simpletweaks.network;

import com.simpletweaks.Simpletweaks;
import io.netty.buffer.ByteBuf; // WICHTIG
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.Uuids;
import org.joml.Vector3f;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LaserManager {

    public static final Map<UUID, LaserData> ACTIVE_LASERS = new ConcurrentHashMap<>();

    // --- MANUELLE CODECS (Um Symbol-Fehler zu vermeiden) ---

    // Einfacher Boolean Codec
    public static final PacketCodec<ByteBuf, Boolean> BOOL_CODEC = new PacketCodec<>() {
        @Override
        public Boolean decode(ByteBuf buf) {
            return buf.readBoolean();
        }
        @Override
        public void encode(ByteBuf buf, Boolean value) {
            buf.writeBoolean(value);
        }
    };

    // Einfacher Vector3f Codec (3 Floats)
    public static final PacketCodec<ByteBuf, Vector3f> VECTOR3F_CODEC = new PacketCodec<>() {
        @Override
        public Vector3f decode(ByteBuf buf) {
            return new Vector3f(buf.readFloat(), buf.readFloat(), buf.readFloat());
        }
        @Override
        public void encode(ByteBuf buf, Vector3f value) {
            buf.writeFloat(value.x);
            buf.writeFloat(value.y);
            buf.writeFloat(value.z);
        }
    };

    public static void register() {
        PayloadTypeRegistry.playC2S().register(LaserPayload.ID, LaserPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LaserPayload.ID, LaserPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(LaserPayload.ID, (payload, context) -> {
            ServerPlayerEntity sender = context.player();
            // Sicherer Cast auf ServerWorld
            ServerWorld world = (ServerWorld) sender.getEntityWorld();

            for (ServerPlayerEntity player : world.getPlayers()) {
                if (player != sender) {
                    ServerPlayNetworking.send(player, payload);
                }
            }
        });
    }

    @Environment(EnvType.CLIENT)
    public static void registerClient() {
        ClientPlayNetworking.registerGlobalReceiver(LaserPayload.ID, (payload, context) -> {
            ACTIVE_LASERS.put(payload.playerUuid(), new LaserData(payload.pos(), System.currentTimeMillis()));
        });
    }

    public record LaserData(Vector3f pos, long timestamp) {}

    public record LaserPayload(UUID playerUuid, Vector3f pos, boolean active) implements CustomPayload {
        public static final Id<LaserPayload> ID = new Id<>(Identifier.of(Simpletweaks.MOD_ID, "laser_pos"));

        public static final PacketCodec<RegistryByteBuf, LaserPayload> CODEC = PacketCodec.tuple(
                Uuids.PACKET_CODEC, LaserPayload::playerUuid,
                VECTOR3F_CODEC, LaserPayload::pos,     // Hier nutzen wir unseren manuellen Codec
                BOOL_CODEC, LaserPayload::active,      // Hier nutzen wir unseren manuellen Codec
                LaserPayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    public static void tick() {
        long now = System.currentTimeMillis();
        ACTIVE_LASERS.entrySet().removeIf(entry -> now - entry.getValue().timestamp > 200);
    }
}