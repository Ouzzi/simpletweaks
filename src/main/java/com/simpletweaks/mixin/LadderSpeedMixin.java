package com.simpletweaks.mixin;

import com.simpletweaks.Simpletweaks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LadderSpeedMixin {

    @Inject(method = "applyClimbingSpeed", at = @At("RETURN"), cancellable = true)
    private void modifyClimbingSpeed(Vec3d motion, CallbackInfoReturnable<Vec3d> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        // WICHTIG: Erst prüfen, ob das Entity überhaupt klettert!
        // Sonst wird jeder Sprung überschrieben.
        if (!self.isClimbing()) return;

        // Config laden
        double configSpeed = Simpletweaks.getConfig().tweaks.ladderClimbingSpeed;

        // Nicht eingreifen, wenn man schleciht (Sneaking), um stehen zu bleiben
        if (self.isSneaking()) return;

        Vec3d current = cir.getReturnValue();

        // Nur eingreifen, wenn wir uns nach oben bewegen
        if (current.y > 0.0) {
            // Y-Speed überschreiben, X und Z beibehalten
            cir.setReturnValue(new Vec3d(current.x, configSpeed, current.z));
        }
    }
}