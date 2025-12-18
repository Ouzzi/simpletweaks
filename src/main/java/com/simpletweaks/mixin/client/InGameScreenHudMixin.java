package com.simpletweaks.mixin.client;

import com.simpletweaks.item.custom.LaserPointerItem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameScreenHudMixin {

    @Shadow @Final private MinecraftClient client;


    @Inject(method = "renderCrosshair", at = @At("HEAD"))
    private void onRenderCrosshairStart(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (client.player != null && client.player.isUsingItem() && client.player.getActiveItem().getItem() instanceof LaserPointerItem) {
            // In 1.21.3/1.21.11 ist dies der offizielle Weg, um die HUD-Farbe zu manipulieren.
            // Es schreibt intern in die DynamicUniforms.
            //RenderSystem.setShaderColor(1.0f, 0.0f, 0.0f, 1.0f);
        }
    }

    @Inject(method = "renderCrosshair", at = @At("RETURN"))
    private void onRenderCrosshairEnd(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        // Farbe auf Weiß (Standard) zurücksetzen
        //aRenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        if (client.player != null && client.player.isUsingItem() && client.player.getActiveItem().getItem() instanceof LaserPointerItem) {
            HitResult hit = client.crosshairTarget;
            if (hit != null && hit.getType() != HitResult.Type.MISS) {
                double dist = hit.getPos().distanceTo(client.player.getEyePos());
                String distStr = String.format("%.1fm", dist);
                int x = context.getScaledWindowWidth() / 2 + 10;
                int y = context.getScaledWindowHeight() / 2 - 4;
                context.drawText(client.textRenderer, distStr, x, y, 0xFF5555, true);
            }
        }
    }
}