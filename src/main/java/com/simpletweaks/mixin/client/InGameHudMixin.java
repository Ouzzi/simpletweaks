package com.simpletweaks.mixin.client;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.component.ModDataComponentTypes;
import com.simpletweaks.item.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.bar.ExperienceBar;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceBar.class)
public abstract class InGameHudMixin {

    @Shadow @Final private MinecraftClient client;

    @Unique
    private static final Identifier TEXTURE_BACKGROUND = Identifier.of("minecraft", "hud/experience_bar_background");
    @Unique
    private static final Identifier TEXTURE_BLUE_PROGRESS = Identifier.of("minecraft", "hud/jump_bar_progress");

    @Inject(method = "renderBar", at = @At("HEAD"), cancellable = true)
    private void onRenderBar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        PlayerEntity player = this.client.player;
        if (player == null) return;

        ItemStack chest = player.getEquippedStack(EquipmentSlot.CHEST);

        if (chest.isOf(ModItems.SPAWN_ELYTRA)) {
            ci.cancel();

            int screenWidth = context.getScaledWindowWidth();
            int screenHeight = context.getScaledWindowHeight();

            int x = (screenWidth - 182) / 2;
            int y = screenHeight - 32 + 3;

            // --- HINTERGRUND ---
            context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURE_BACKGROUND, x, y, 182, 5);

            // --- FORTSCHRITT ---
            Float boostLevel = chest.get(ModDataComponentTypes.BOOST_LEVEL);
            if (boostLevel == null) boostLevel = 1.0f;

            int maxBoosts = Simpletweaks.getConfig().spawn.maxBoosts;
            if (maxBoosts < 1) maxBoosts = 1;

            int currentBoosts = Math.round(boostLevel * maxBoosts);

            if (currentBoosts > 0) {
                int progressWidth = (currentBoosts * 182) / maxBoosts;
                if (progressWidth > 0) {
                    context.drawGuiTexture(RenderPipelines.GUI_TEXTURED, TEXTURE_BLUE_PROGRESS, 182, 5, 0, 0, x, y, progressWidth, 5);
                }
            }

            // --- TEXT (TIMER) ---
            Integer ticksLeft = chest.get(ModDataComponentTypes.FLIGHT_TIME);
            String text;
            int color;

            if (ticksLeft == null) {
                text = "Active";
                color = 0xFF55FFFF; // Aqua
            } else {
                int seconds = ticksLeft / 20;
                text = String.format("%02d:%02d", seconds / 60, seconds % 60);

                if (seconds < 30) {
                    boolean blink = (this.client.world != null && this.client.world.getTime() % 10 < 5);
                    color = blink ? 0xFFFF5555 : 0xFFFFFF55;
                } else {
                    color = 0xFF55FF55; // Grün
                }
            }

            int textWidth = this.client.textRenderer.getWidth(text);
            int textX = (screenWidth - textWidth) / 2;
            int textY = y - 6;

            // --- VANILLA OUTLINE RENDERING ---
            // Wir zeichnen den Text 4x in Schwarz versetzt, dann 1x farbig in der Mitte.
            // Das entspricht exakt der Logik von Bar.drawExperienceLevel
            int outlineColor = 0xFF000000;

            // 4x Schwarz (Outline)
            context.drawText(this.client.textRenderer, text, textX + 1, textY, outlineColor, false);
            context.drawText(this.client.textRenderer, text, textX - 1, textY, outlineColor, false);
            context.drawText(this.client.textRenderer, text, textX, textY + 1, outlineColor, false);
            context.drawText(this.client.textRenderer, text, textX, textY - 1, outlineColor, false);

            // 1x Farbe (Vordergrund)
            context.drawText(this.client.textRenderer, text, textX, textY, color | 0xFF000000, false);
        }
    }

}