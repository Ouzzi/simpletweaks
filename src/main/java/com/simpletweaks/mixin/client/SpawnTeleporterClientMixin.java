package com.simpletweaks.mixin.client;

import com.simpletweaks.block.entity.SpawnTeleporterBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(SpawnTeleporterBlockEntity.class)
public class SpawnTeleporterClientMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private static void clientTick(World world, BlockPos pos, BlockState state, SpawnTeleporterBlockEntity be, CallbackInfo ci) {
        if (!world.isClient()) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        UUID owner = be.getOwnerUuid();
        if (owner != null && owner.equals(client.player.getUuid())) {

            // Partikel spawnen (nur wenn Besitzer)
            // Nicht jeden Tick, sonst zu voll -> Random Check
            Random random = world.getRandom();
            if (random.nextInt(5) == 0) {
                double x = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.6;
                double y = pos.getY() + 0.1 + random.nextDouble() * 0.5; // Knapp über dem Boden
                double z = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.6;

                // "Enchant" Partikel fliegen langsam nach oben (magischer Effekt)
                world.addParticleClient(ParticleTypes.ENCHANT, x, y, z,
                        (random.nextDouble() - 0.5) * 0.05,
                        0.2, // Aufwärtsgeschwindigkeit
                        (random.nextDouble() - 0.5) * 0.05);
            }
        }
    }
}