package com.simpletweaks.mixin.client;

import com.simpletweaks.client.gui.PickupNotifierHud;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameScreenHudMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void renderPickupNotifier(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        // Hier rufen wir unseren Renderer direkt auf
        PickupNotifierHud.render(context, tickCounter);
    }
}