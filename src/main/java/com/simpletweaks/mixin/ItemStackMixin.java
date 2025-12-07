package com.simpletweaks.mixin;

import com.simpletweaks.Simpletweaks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    /**
     * Wir greifen ein, wenn der Stack fragt "Wie groß darf ich sein?".
     * Das überschreibt auch die Data Components.
     */
    @Inject(method = "getMaxCount", at = @At("HEAD"), cancellable = true)
    private void modifyMaxStackSize(CallbackInfoReturnable<Integer> cir) {
        // 1. Performance Check: Ist es eine Rakete?
        if (this.getItem() == Items.FIREWORK_ROCKET) {

            // 2. Config Check (sicherstellen, dass sie geladen ist)
            if (Simpletweaks.getConfig() != null) {
                int limit = Simpletweaks.getConfig().balancing.rocketStackSize;

                // Nur eingreifen, wenn das Limit tatsächlich anders ist als 64
                if (limit < 64) {
                    cir.setReturnValue(limit);
                }
            }
        }
    }
}