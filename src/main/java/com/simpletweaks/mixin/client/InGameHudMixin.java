package com.simpletweaks.mixin.client;

import com.simpletweaks.component.ModDataComponentTypes;
import com.simpletweaks.item.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.ExperienceBar;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceBar.class)
public abstract class InGameHudMixin {

    @Shadow @Final private MinecraftClient client;

    @Inject(method = "renderBar", at = @At("HEAD"), cancellable = true)
    private void onRenderBar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        PlayerEntity player = this.client.player;
        if (player == null) return;

        ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);
        if (chest.isOf(ModItems.SPAWN_ELYTRA)) {
            // Vanilla XP Bar unterdrücken
            ci.cancel();

            // --- Timer Daten holen ---
            Integer ticksLeft = chest.get(ModDataComponentTypes.FLIGHT_TIME);

            String text;
            int color;

            if (ticksLeft == null) {
                // Im Spawn-Bereich (kein Timer aktiv)
                text = "Active";
                color = 0x55FFFF; // Aqua
            } else {
                // Zeit formatieren (MM:SS)
                int seconds = ticksLeft / 20;
                text = String.format("%02d:%02d", seconds / 60, seconds % 60);

                // Farbe je nach verbleibender Zeit
                if (seconds < 30) {
                    boolean blink = (this.client.world != null && this.client.world.getTime() % 10 < 5);
                    color = blink ? 0xFF5555 : 0xFFFF55; // Rot/Gelb blinkend
                } else {
                    color = 0x55FF55; // Grün
                }
            }

            // --- Rendering ---
            int screenWidth = context.getScaledWindowWidth();
            int screenHeight = context.getScaledWindowHeight();

            // Position: Genau dort, wo sonst die XP-Bar ist (Mitte über Hotbar)
            int xPos = screenWidth / 2;
            int yPos = screenHeight - 46 + 3;

            // Text zentriert zeichnen
            int textWidth = this.client.textRenderer.getWidth(text);
            // yPos - 6 rückt den Text optisch schön in die Mitte des freien Platzes
            context.drawTextWithShadow(this.client.textRenderer, text, xPos - (textWidth / 2), yPos - 6, color | 0xFF000000);
        }
    }
}