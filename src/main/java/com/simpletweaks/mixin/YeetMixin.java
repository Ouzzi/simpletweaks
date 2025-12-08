package com.simpletweaks.mixin;

import com.simpletweaks.Simpletweaks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class YeetMixin {

    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;", at = @At("RETURN"))
    private void onDropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        // Config Check
        if (!Simpletweaks.getConfig().tweaks.enableYeet) return;

        ItemEntity itemEntity = cir.getReturnValue();
        if (itemEntity == null) return;

        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // Yeet nur wenn Sneaking (Shift)
        if (player.isSneaking()) {
            float strength = Simpletweaks.getConfig().tweaks.yeetStrength;

            // Aktuelle Geschwindigkeit holen
            Vec3d currentVel = itemEntity.getVelocity();

            // Geschwindigkeit verstärken
            // Wir multiplizieren X/Z stark, Y etwas weniger, damit es nicht nur nach oben fliegt
            itemEntity.setVelocity(currentVel.multiply(strength, strength * 0.5, strength));

            // Optional: Pickup Delay etwas erhöhen, damit man es nicht sofort wieder aufsammelt
            itemEntity.setPickupDelay(20);
        }
    }
}