package com.simpletweaks.client.gui;

import com.simpletweaks.component.ModDataComponentTypes;
import com.simpletweaks.item.ModItems;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Formatting;

public class SpawnElytraTimerOverlay implements HudRenderCallback {
    @Override
    public void onHudRender(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        // Prüfen, ob die Spawn Elytra getragen wird
        ItemStack chest = client.player.getEquippedStack(EquipmentSlot.CHEST);
        if (!chest.isOf(ModItems.SPAWN_ELYTRA)) return;

        // Daten holen (Kann null sein, wenn im Spawn-Bereich oder frisch gespawnt)
        Integer ticksLeft = chest.get(ModDataComponentTypes.FLIGHT_TIME);

        String text;
        int color; // Wir nutzen direkt Int-Farben für Sicherheit

        if (ticksLeft == null) {
            // Wenn keine Zeit gesetzt ist (z.B. im sicheren Spawn-Bereich)
            text = "Elytra: Active";
            color = 0x55FFFF; // Aqua
        } else {
            // Zeit berechnen
            int seconds = ticksLeft / 20;
            text = String.format("%02d:%02d", seconds / 60, seconds % 60);

            // Farb-Logik:
            // > 30s: Grün
            // < 30s: Rot/Gelb blinkend
            if (seconds < 30) {
                // Blink-Effekt basierend auf der Weltzeit
                boolean blink = (client.world != null && client.world.getTime() % 10 < 5);
                color = blink ? 0xFF5555 : 0xFFFF55; // Rot : Gelb
            } else {
                color = 0x55FF55; // Grün
            }
        }

        // --- RENDERING ---
        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();

        // Positionierung:
        // Wir zentrieren den Text relativ zur Hungerleiste (rechte Seite)
        // Die Hungerleiste beginnt bei Mitte + 10 und ist 81px breit.
        int textWidth = client.textRenderer.getWidth(text);
        int x = (screenWidth / 2) + 10 + (81 - textWidth) / 2;

        // y: Über der Hunger-Leiste. Standard GUI ist unten ca 40-50px hoch.
        int y = screenHeight - 49;

        // Text mit Schatten zeichnen
        context.drawTextWithShadow(client.textRenderer, text, x, y, color);
    }
}