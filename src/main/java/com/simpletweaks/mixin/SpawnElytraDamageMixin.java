package com.simpletweaks.mixin;

import com.simpletweaks.item.ModItems;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld; // Import hinzugefügt
import org.spongepowered.asm.mixin.Mixin;
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
        }
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