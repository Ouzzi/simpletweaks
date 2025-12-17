package com.simpletweaks.client.gui;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.config.SimpletweaksConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PickupNotifierHud {

    private static final List<Notification> notifications = new ArrayList<>();
    private static final MinecraftClient client = MinecraftClient.getInstance();

    public static void addNotification(ItemStack stack, int count) {
        if (stack.isEmpty() || count <= 0) return;

        ItemStack displayStack = stack.copy();

        // Versuchen zu mergen
        for (Notification notification : notifications) {
            if (ItemStack.areItemsEqual(notification.stack, displayStack) && notification.age < 60) {
                notification.count += count;
                notification.age = 0; // Reset timer
                notification.popScale = 1.3f; // Kleiner Pop
                return;
            }
        }

        // Neu hinzufügen
        notifications.add(new Notification(displayStack, count));
    }

    public static void addXpNotification(int amount) {
        if (!Simpletweaks.getConfig().visuals.pickupNotifier.pickupNotifierShowXp || amount <= 0) return;

        for (Notification notification : notifications) {
            if (notification.isXp && notification.age < 60) {
                notification.count += amount;
                notification.age = 0;
                notification.popScale = 1.3f;
                return;
            }
        }

        ItemStack xpIcon = new ItemStack(Items.EXPERIENCE_BOTTLE);
        Notification xpNotif = new Notification(xpIcon, amount);
        xpNotif.isXp = true;
        notifications.add(xpNotif);
    }

    public static void render(DrawContext context, RenderTickCounter tickCounter) {
        var config = Simpletweaks.getConfig().visuals;
        if (!config.pickupNotifier.enablePickupNotifier || notifications.isEmpty() || client.options.hudHidden) return;

        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();
        TextRenderer textRenderer = client.textRenderer;

        // --- POSITION & SEITE ---
        boolean isRight = (config.pickupNotifier.pickupNotifierSide == SimpletweaksConfig.PickupSide.RIGHT);
        int startX = isRight ? (screenWidth - config.pickupNotifier.pickupNotifierOffsetX) : config.pickupNotifier.pickupNotifierOffsetX;
        int startY = screenHeight - config.pickupNotifier.pickupNotifierOffsetY;

        float scale = config.pickupNotifier.pickupNotifierScale;
        int maxAge = config.pickupNotifier.pickupNotifierDuration;

        context.getMatrices().pushMatrix();

        // Globale Skalierung
        if (scale != 1.0f) {
            context.getMatrices().scale(scale, scale);
            startX = (int) (startX / scale);
            startY = (int) (startY / scale);
        }

        int yOffset = 0;

        for (int i = 0; i < notifications.size(); i++) {
            Notification n = notifications.get(i);

            // Lebensdauer
            float life = (float) n.age + tickCounter.getTickProgress(true);

            // Ein-/Ausblenden
            float opacity = 1.0f;
            if (life < 5) opacity = MathHelper.clamp(life / 5.0f, 0.0f, 1.0f);
            else if (life > maxAge - 20) opacity = MathHelper.clamp((maxAge - life) / 20.0f, 0.0f, 1.0f);

            if (opacity <= 0) continue;

            int alpha = (int) (255 * opacity);

            // Textfarben
            int textColor = (alpha << 24) | 0xFFFFFF;
            int countColor = n.isXp ? ((alpha << 24) | 0x80FF20) : ((alpha << 24) | 0xAAAAAA);

            // Name + Rarity
            Text nameText;
            if (n.isXp) {
                nameText = Text.literal("Experience").formatted(Formatting.GREEN);
            } else {
                nameText = n.stack.getName();
                if (config.pickupNotifier.pickupUseRarityColor) {
                    Rarity rarity = n.stack.getRarity();
                    if (rarity != Rarity.COMMON) {
                        nameText = nameText.copy().formatted(rarity.getFormatting());
                    }
                }
            }

            String countText = "+" + n.count;
            if (n.isXp) countText += " XP";

            // Breiten
            int padding = 4;
            int iconWidth = 16;
            int nameWidth = textRenderer.getWidth(nameText);
            int countWidth = textRenderer.getWidth(countText);

            boolean showIcon = config.pickupNotifier.pickupShowItem;
            boolean showName = config.pickupNotifier.pickupShowName;
            boolean showCount = config.pickupNotifier.pickupShowCount;

            int contentWidth = 0;
            if (showIcon) contentWidth += iconWidth + padding;
            if (showName) contentWidth += nameWidth + padding;
            if (showCount) contentWidth += countWidth + padding;
            if (contentWidth > 0) contentWidth -= padding; // Letztes Padding weg

            int boxWidth = contentWidth + (padding * 2);
            int boxHeight = 20;

            // X-Koordinate berechnen
            int renderX;
            if (isRight) {
                renderX = startX - boxWidth;
            } else {
                renderX = startX;
            }
            int renderY = startY - yOffset - boxHeight;

            // Pop Animation
            float currentScale = 1.0f;
            if (n.popScale > 1.0f) {
                currentScale = n.popScale;
                n.popScale = MathHelper.lerp(0.2f, n.popScale, 1.0f);
            }

            context.getMatrices().pushMatrix();

            // Scale um Mitte
            if (currentScale > 1.0f) {
                float centerX = renderX + boxWidth / 2.0f;
                float centerY = renderY + boxHeight / 2.0f;
                context.getMatrices().translate(centerX, centerY);
                context.getMatrices().scale(currentScale, currentScale);
                context.getMatrices().translate(-centerX, -centerY);
            }

            // HINTERGRUND
            if (config.pickupNotifier.pickupVanillaStyle) {
                drawVanillaBox(context, renderX, renderY, boxWidth, boxHeight, alpha, config.pickupNotifier.pickupBackgroundOpacity);
            } else {
                int bgAlphaValue = (int) ((Math.max(0, alpha - 180)) * config.pickupNotifier.pickupBackgroundOpacity);
                int bgColor = (bgAlphaValue << 24) | 0x000000;
                context.fill(renderX, renderY, renderX + boxWidth, renderY + boxHeight, bgColor);
            }

            // INHALT
            int currentX = renderX + padding;
            int centerY = renderY + (boxHeight / 2);

            SimpletweaksConfig.PickupLayout layout = config.pickupNotifier.pickupNotifierLayout;

            // Zeichnen & X verschieben
            if (layout == SimpletweaksConfig.PickupLayout.ICON_NAME_COUNT) {
                if (showIcon) currentX = drawIcon(context, n.stack, currentX, centerY, padding);
                if (showName) currentX = drawString(context, textRenderer, nameText, currentX, centerY, textColor, padding);
                if (showCount) drawString(context, textRenderer, countText, currentX, centerY, countColor, padding);
            }
            else if (layout == SimpletweaksConfig.PickupLayout.COUNT_ICON_NAME) {
                if (showCount) currentX = drawString(context, textRenderer, countText, currentX, centerY, countColor, padding);
                if (showIcon) currentX = drawIcon(context, n.stack, currentX, centerY, padding);
                if (showName) drawString(context, textRenderer, nameText, currentX, centerY, textColor, padding);
            }
            else if (layout == SimpletweaksConfig.PickupLayout.NAME_ICON_COUNT) {
                if (showName) currentX = drawString(context, textRenderer, nameText, currentX, centerY, textColor, padding);
                if (showIcon) currentX = drawIcon(context, n.stack, currentX, centerY, padding);
                if (showCount) drawString(context, textRenderer, countText, currentX, centerY, countColor, padding);
            }
            else if (layout == SimpletweaksConfig.PickupLayout.ICON_COUNT_NAME) {
                if (showIcon) currentX = drawIcon(context, n.stack, currentX, centerY, padding);
                if (showCount) currentX = drawString(context, textRenderer, countText, currentX, centerY, countColor, padding);
                if (showName) drawString(context, textRenderer, nameText, currentX, centerY, textColor, padding);
            }

            context.getMatrices().popMatrix();

            // Slide Animation für Liste
            float slideProgress = MathHelper.clamp(life / 3.0f, 0.0f, 1.0f);
            yOffset += (int) ((boxHeight + 2) * slideProgress);
        }

        context.getMatrices().popMatrix();
    }

    private static int drawIcon(DrawContext context, ItemStack stack, int x, int centerY, int padding) {
        context.drawItem(stack, x, centerY - 8);
        // Auch Item Count im Icon (Overlay) zeichnen, falls gewünscht?
        // Nein, das ist meist zu klein. Wir haben ja den Text.
        return x + 16 + padding;
    }

    private static int drawString(DrawContext context, TextRenderer textRenderer, Text text, int x, int centerY, int color, int padding) {
        context.drawText(textRenderer, text, x, centerY - 4, color, true);
        return x + textRenderer.getWidth(text) + padding;
    }

    private static int drawString(DrawContext context, TextRenderer textRenderer, String text, int x, int centerY, int color, int padding) {
        context.drawText(textRenderer, text, x, centerY - 4, color, true);
        return x + textRenderer.getWidth(text) + padding;
    }

    private static void drawVanillaBox(DrawContext context, int x, int y, int w, int h, int globalAlpha, float bgOpacityFactor) {
        int bg = 0xF0100010;
        int borderStart = 0x505000FF;
        int borderEnd = 0x5028007F;

        int alpha = globalAlpha;
        int bgAlpha = (int) (0xF0 * bgOpacityFactor * (globalAlpha / 255.0f));

        if (alpha != 255 || bgOpacityFactor != 1.0f) {
            bg = (bgAlpha << 24) | (bg & 0x00FFFFFF);
            borderStart = (alpha << 24) | (borderStart & 0x00FFFFFF);
            borderEnd = (alpha << 24) | (borderEnd & 0x00FFFFFF);
        }

        // Vanilla Box Logic
        context.fill(x + 1, y + 1, x + w - 1, y + h - 1, bg);
        context.fillGradient(x + 1, y, x + w - 1, y + 1, borderStart, borderStart);
        context.fillGradient(x + 1, y + h - 1, x + w - 1, y + h, borderEnd, borderEnd);
        context.fillGradient(x, y + 1, x + 1, y + h - 1, borderStart, borderEnd);
        context.fillGradient(x + w - 1, y + 1, x + w, y + h - 1, borderStart, borderEnd);
    }

    public static void tick() {
        if (notifications.isEmpty()) return;
        int maxAge = Simpletweaks.getConfig().visuals.pickupNotifier.pickupNotifierDuration;
        Iterator<Notification> iterator = notifications.iterator();
        while (iterator.hasNext()) {
            Notification n = iterator.next();
            n.age++;
            if (n.age > maxAge) iterator.remove();
        }
    }

    private static class Notification {
        ItemStack stack;
        int count;
        int age;
        boolean isXp;
        float popScale = 1.0f;

        public Notification(ItemStack stack, int count) {
            this.stack = stack.copy();
            this.count = count;
            this.age = 0;
            this.isXp = false;
        }
    }
}