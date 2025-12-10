package com.simpletweaks.mixin.client;

import com.simpletweaks.item.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.hud.bar.Bar; // Wichtig: Import für die Bar Klasse
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(InGameHud.class)
public abstract class InGameHudLevelMixin {

    @Shadow @Final private MinecraftClient client;

    // Wir nutzen @Redirect, um den spezifischen Aufruf von Bar.drawExperienceLevel in renderMainHud abzufangen
    @Redirect(
            method = "renderMainHud",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/bar/Bar;drawExperienceLevel(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;I)V"
            )
    )
    private void redirectDrawExperienceLevel(DrawContext context, TextRenderer textRenderer, int experienceLevel) {
        if (this.client.player != null && this.client.player.getEquippedStack(EquipmentSlot.CHEST).isOf(ModItems.SPAWN_ELYTRA)) {
            return;
        }
        Bar.drawExperienceLevel(context, textRenderer, experienceLevel);
    }
}