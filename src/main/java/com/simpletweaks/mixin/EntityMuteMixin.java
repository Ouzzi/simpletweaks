package com.simpletweaks.mixin;

import com.simpletweaks.Simpletweaks;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMuteMixin {

    @Shadow public abstract boolean hasCustomName();
    @Shadow public abstract Text getCustomName();

    @Inject(method = "isSilent", at = @At("HEAD"), cancellable = true)
    private void checkIfMutedByName(CallbackInfoReturnable<Boolean> cir) {
        // Performance: Erst prüfen ob Custom Name existiert
        if (this.hasCustomName()) {
            String name = this.getCustomName().getString();
            // Zugriff auf die Liste in der Config
            List<String> suffixes = Simpletweaks.getConfig().tweaks.nametagMuteSuffixes;

            if (suffixes != null) {
                for (String suffix : suffixes) {
                    if (suffix != null && !suffix.isEmpty() && name.endsWith(suffix)) {
                        cir.setReturnValue(true); // Entity ist stumm
                        return; // Sobald ein Treffer gefunden wurde, abbrechen
                    }
                }
            }
        }
    }
}