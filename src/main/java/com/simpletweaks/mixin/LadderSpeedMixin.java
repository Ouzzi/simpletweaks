package com.simpletweaks.mixin;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.config.SimpletweaksConfig;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LadderSpeedMixin {

    // --- Teil 1: Schnelleres Rutschen nach UNTEN (Ladder Slide) ---
    @Inject(method = "applyClimbingSpeed", at = @At("RETURN"), cancellable = true)
    private void modifyClimbingSpeedDown(Vec3d motion, CallbackInfoReturnable<Vec3d> cir) {
        // Cast 'this' zu LivingEntity
        LivingEntity entity = (LivingEntity) (Object) this;

        // Config laden
        var config = Simpletweaks.getConfig().qOL;

        // Nur für Spieler und wenn aktiviert
        if (entity instanceof PlayerEntity && config.enableFastLadderSlide) {

            // Grundbedingungen: Auf Leiter und Blick nach unten (> 45 Grad)
            if (entity.isClimbing() && entity.getPitch() > 45.0F) {

                // Prüfen, ob wir uns NICHT nach oben bewegen (um Konflikte zu vermeiden)
                Vec3d currentMotion = cir.getReturnValue();
                if (currentMotion.y > 0.0) return;

                boolean shouldSlide = false;

                // Prüfen, welche Taste gedrückt werden muss
                if (config.ladderSlideActivation == SimpletweaksConfig.SlideActivationMode.ALWAYS) {
                    // ALWAYS: Rutschen, solange man NICHT schleicht (Shift = Bremse)
                    if (!entity.isSneaking()) {
                        shouldSlide = true;
                    }
                }
                else if (config.ladderSlideActivation == SimpletweaksConfig.SlideActivationMode.CAMERA) {
                    // CAMERA: Rutschen, wenn man schleicht (Shift)
                    // Dies überschreibt das normale "Anhalten/Klammern" auf der Leiter
                    if (entity.isSneaking()) {
                        shouldSlide = true;
                    }
                }

                // Wenn Rutschen aktiv, Geschwindigkeit erzwingen
                if (shouldSlide) {
                    double targetSpeed = -config.ladderSlideSpeed;

                    // Sicherheitscheck, nicht schneller als -2.0 (um Glitches zu vermeiden)
                    if (targetSpeed < -2.0) targetSpeed = -2.0;

                    // X und Z beibehalten, nur Y hart setzen
                    cir.setReturnValue(new Vec3d(currentMotion.x, targetSpeed, currentMotion.z));
                }
            }
        }
    }

    // --- Teil 2: Schnelleres Klettern nach OBEN ---
    @Inject(method = "applyClimbingSpeed", at = @At("RETURN"), cancellable = true)
    private void modifyClimbingSpeedUp(Vec3d motion, CallbackInfoReturnable<Vec3d> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;

        // Basic Checks
        if (!entity.isClimbing()) return;
        if (entity.isSneaking()) return; // Shift = Anhalten

        // FIX FÜR "AUTOMATISCHES KLETTERN" / RESPONSIVITÄT:
        // Wir prüfen 'forwardSpeed'. Das ist der Input-Wert (Tastendruck).
        // Wenn der Spieler 'W' drückt, ist forwardSpeed > 0.
        // Wenn er loslässt, ist es 0.
        // Ohne diesen Check würde die Rest-Geschwindigkeit (Inertia) sofort wieder verstärkt werden -> Drift.
        if (entity.forwardSpeed <= 0.0f) return;

        Vec3d current = cir.getReturnValue();

        // Nur eingreifen, wenn wir uns nach oben bewegen
        if (current.y > 0.0) {
            double configSpeed = Simpletweaks.getConfig().qOL.ladderClimbingSpeed;
            cir.setReturnValue(new Vec3d(current.x, configSpeed, current.z));
        }
    }
}