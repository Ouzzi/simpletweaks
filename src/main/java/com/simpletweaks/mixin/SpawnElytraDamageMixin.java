package com.simpletweaks.mixin;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.item.ModItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity; // Um sicherzugehen, dass es ein Spieler ist
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class SpawnElytraDamageMixin {

    // 1. Fallschaden verhindern
    // (handleFallDamage hat kein ServerWorld Argument, aber fallDistance ist double)
    @Inject(method = "handleFallDamage", at = @At("HEAD"), cancellable = true)
    private void onFallDamage(double fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        ItemStack chest = entity.getEquippedStack(EquipmentSlot.CHEST);

        if (chest.isOf(ModItems.SPAWN_ELYTRA)) {
            cir.setReturnValue(false);
            return;
        }

        // 2. Check: Genereller Fallschutz im Spawn-Bereich (NEU)
        if (entity instanceof ServerPlayerEntity player) { // Nur für Spieler (Server-seitig)
            if (Simpletweaks.getConfig().spawn.disableFallDamageInSpawn && isInSpawnArea(player)) {
                cir.setReturnValue(false);
            }
        }
    }

    @Unique
    private boolean isInSpawnArea(ServerPlayerEntity player) {
        BlockPos playerPos = player.getBlockPos();
        World world = player.getEntityWorld();

        // Spawn Punkt ermitteln (wie im SpawnHandler)
        BlockPos center;
        if (Simpletweaks.getConfig().spawn.useWorldSpawnAsCenter) {
            center = world.getSpawnPoint().getPos();
        } else {
            center = new BlockPos(
                    Simpletweaks.getConfig().spawn.customSpawnElytraX,
                    playerPos.getY(), // Y ist für Radius meist egal (Zylinder), sonst center.getY()
                    Simpletweaks.getConfig().spawn.customSpawnElytraZ
            );
        }

        // Distanz prüfen (ignoriert Y-Höhe für Zylinder-Effekt, oder inkl. Y für Kugel)
        // Hier: Zylindrische Prüfung (2D Distanz), wie bei Elytra-Verlust üblich
        double distanceSq = playerPos.getSquaredDistance(center.getX(), playerPos.getY(), center.getZ());
        int radius = Simpletweaks.getConfig().spawn.spawnElytraRadius;

        return distanceSq <= (radius * radius);
    }

    // 2. Kinetischen Schaden (Wände) verhindern
    // FIX: ServerWorld world als ersten Parameter hinzufügen!
    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void onDamage(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        ItemStack chest = entity.getEquippedStack(EquipmentSlot.CHEST);

        if (chest.isOf(ModItems.SPAWN_ELYTRA)) {
            // Prüfen ob es "Fly Into Wall" (Kinetische Energie) ist
            if (source.isOf(DamageTypes.FLY_INTO_WALL)) {
                cir.setReturnValue(false); // Schaden abbrechen
            }
        }
    }
}