package com.simpletweaks.mixin;

import com.simpletweaks.Simpletweaks;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmlandBlock.class)
public class FarmlandTrampleMixin {

    // Corrected fallDistance to double
    @Inject(method = "onLandedUpon", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/FarmlandBlock;setToDirt(Lnet/minecraft/entity/Entity;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;)V"), cancellable = true)
    private void preventTrampleIfFeatherFalling(World world, BlockState state, BlockPos pos, Entity entity, double fallDistance, CallbackInfo ci) {
        if (!Simpletweaks.getConfig().tweaks.preventFarmlandTrampleWithFeatherFalling) return;

        if (entity instanceof LivingEntity living) {
            // Use getOrThrow to get the registry
            var registry = world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT);

            // FIX: Use getOptional() instead of getEntry() for RegistryKeys
            var featherFalling = registry.getOptional(Enchantments.FEATHER_FALLING);

            if (featherFalling.isPresent()) {
                int level = EnchantmentHelper.getEquipmentLevel(featherFalling.get(), living);
                if (level > 0) {
                    // Cancel the trample, block remains farmland
                    ci.cancel();
                }
            }
        }
    }
}