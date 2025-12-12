package com.simpletweaks.network;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.component.ModDataComponentTypes;
import com.simpletweaks.item.ModItems;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class SpawnElytraNetworking {

    public record BoostPayload() implements CustomPayload {
        public static final CustomPayload.Id<BoostPayload> ID = new CustomPayload.Id<>(Identifier.of(Simpletweaks.MOD_ID, "elytra_boost"));
        public static final PacketCodec<PacketByteBuf, BoostPayload> CODEC = PacketCodec.unit(new BoostPayload());

        @Override
        public Id<? extends CustomPayload> getId() { return ID; }
    }

    public static void register() {
        PayloadTypeRegistry.playC2S().register(BoostPayload.ID, BoostPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(BoostPayload.ID, (payload, context) -> {
            context.server().execute(() -> {
                var player = context.player();
                ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);

                // FIX: isGliding() statt isFallFlying()
                if (chest.isOf(ModItems.SPAWN_ELYTRA)) {

                    Float boost = chest.get(ModDataComponentTypes.BOOST_LEVEL);
                    if (boost == null) boost = 0.0f;

                    // FIX: Sicherstellen, dass der Config-Wert min. 1 ist, um Division durch Null zu vermeiden
                    int maxBoosts = Math.max(1, Simpletweaks.getConfig().spawn.maxBoosts);
                    float cost = 1.0f / maxBoosts;

                    // FIX: Epsilon Toleranz (0.001f), damit 0.333332 >= 0.333333 funktioniert
                    if (boost >= cost - 0.001f) {

                        // Boost anwenden
                        float strength = Simpletweaks.getConfig().spawn.boostStrength;
                        Vec3d look = player.getRotationVector();
                        Vec3d vel = player.getVelocity();
                        player.setVelocity(vel.add(look.x * strength, look.y * strength, look.z * strength));

                        // FIX: Statt velocityModified = true zu setzen (was protected ist),
                        // senden wir dem Client direkt das Update-Paket.
                        player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));

                        player.getEntityWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.PLAYERS, 0.3f, 1.0f);

                        // Abziehen und säubern
                        float newLevel = boost - cost;
                        // Wenn wir sehr nah an 0 sind (kleiner als Toleranz), erzwingen wir 0.0
                        if (newLevel < 0.001f) {
                            newLevel = 0.0f;
                        }

                        chest.set(ModDataComponentTypes.BOOST_LEVEL, newLevel);
                    }
                }
            });
        });
    }
}