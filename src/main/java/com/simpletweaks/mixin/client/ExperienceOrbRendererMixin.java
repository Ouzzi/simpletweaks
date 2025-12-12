package com.simpletweaks.mixin.client;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.client.IOrbValue;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.ExperienceOrbEntityRenderer;
import net.minecraft.client.render.entity.state.ExperienceOrbEntityRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ExperienceOrbEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrbEntityRenderer.class)
public class ExperienceOrbRendererMixin {

    // 1. Datenübertragung: Entity -> State
    @Inject(method = "updateRenderState(Lnet/minecraft/entity/ExperienceOrbEntity;Lnet/minecraft/client/render/entity/state/ExperienceOrbEntityRenderState;F)V", at = @At("TAIL"))
    private void captureValue(ExperienceOrbEntity entity, ExperienceOrbEntityRenderState state, float tickDelta, CallbackInfo ci) {
        // Wir nutzen unser Interface, um den Wert zu speichern
        ((IOrbValue) state).simpletweaks$setValue(entity.getValue());
    }

    // 2. Rendering: State -> Bildschirm
    @Inject(method = "render(Lnet/minecraft/client/render/entity/state/ExperienceOrbEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V", at = @At("HEAD"))
    private void scaleOrb(ExperienceOrbEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci) {
        if (Simpletweaks.getConfig().optimization.scaleXpOrbs) {
            // Wert aus dem State holen
            int value = ((IOrbValue) state).simpletweaks$getValue();

            // Scaling Logik (Basis 1.0 + Wert/500)
            float scale = 1.0f + (value / 500.0f);

            // Limit (max 3x Größe)
            if (scale > 3.0f) scale = 3.0f;

            matrices.scale(scale, scale, scale);
        }
    }
}