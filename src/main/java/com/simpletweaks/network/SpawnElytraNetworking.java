package com.simpletweaks.network;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.component.ModDataComponentTypes;
import com.simpletweaks.item.ModItems;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf; // WICHTIG: PacketByteBuf statt RegistryByteBuf
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public class SpawnElytraNetworking {

    // Leeres Paket, nur als Signal "Space gedrückt"
    public record BoostPayload() implements CustomPayload {
        public static final CustomPayload.Id<BoostPayload> ID = new CustomPayload.Id<>(Identifier.of(Simpletweaks.MOD_ID, "elytra_boost"));

        // FIX: PacketByteBuf statt RegistryByteBuf für maximale Kompatibilität
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

                    // Kostet z.B. 10% pro Boost
                    float cost = 1.0f / Simpletweaks.getConfig().spawn.maxBoosts;

                    if (boost >= cost) {
                        // Boost anwenden
                        float strength = Simpletweaks.getConfig().spawn.boostStrength;
                        Vec3d look = player.getRotationVector();
                        Vec3d vel = player.getVelocity();
                        player.setVelocity(vel.add(look.x * strength, look.y * strength, look.z * strength));
                        player.velocityModified = true;

                        // Sound
                        // FIX: getEntityWorld() statt getWorld()
                        player.getEntityWorld().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.PLAYERS, 1.0f, 1.0f);

                        // Abziehen
                        chest.set(ModDataComponentTypes.BOOST_LEVEL, boost - cost);
                    }
                }
            });
        });
    }
}