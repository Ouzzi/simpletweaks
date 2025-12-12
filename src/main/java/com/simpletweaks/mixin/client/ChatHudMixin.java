package com.simpletweaks.mixin.client;

import com.simpletweaks.Simpletweaks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {

    @Shadow @Final private MinecraftClient client;
    @Shadow @Final private List<ChatHudLine> messages;
    @Shadow @Final private List<ChatHudLine.Visible> visibleMessages;
    @Shadow private int scrolledLines;

    @Shadow public abstract double getChatScale();
    @Shadow public abstract int getVisibleLineCount();
    @Shadow protected abstract boolean isChatHidden();

    // 1. Verschiebung: Wir injecten VOR dem Aufruf der internen render-Methode.
    // Das ist sicher, da dieser Aufruf NACH pushMatrix() passiert.
    @Inject(
            method = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;IIIZZ)V",
            at = @At(
                    value = "INVOKE",
                    // Ziel: Die private render Methode (Backend, int, int, boolean)
                    target = "Lnet/minecraft/client/gui/hud/ChatHud;render(Lnet/minecraft/client/gui/hud/ChatHud$Backend;IIZ)V"
            )
    )
    private void shiftChatBeforeInternalRender(DrawContext context, TextRenderer textRenderer, int currentTick, int mouseX, int mouseY, boolean interactable, boolean bl, CallbackInfo ci) {
        if (!this.isChatHidden() && Simpletweaks.getConfig().visuals.enableChatHeads) {
            // Verschiebe den Kontext um 12 Pixel nach rechts
            context.getMatrices().translate(12.0f, 0.0f);
        }
    }

    // 2. Köpfe rendern (Am Ende der Methode)
    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/font/TextRenderer;IIIZZ)V", at = @At("TAIL"))
    private void renderChatHeads(DrawContext context, TextRenderer textRenderer, int currentTick, int mouseX, int mouseY, boolean interactable, boolean bl, CallbackInfo ci) {
        if (this.isChatHidden() || !Simpletweaks.getConfig().visuals.enableChatHeads) return;
        if (this.visibleMessages.isEmpty()) return;

        float scale = (float) this.getChatScale();

        context.getMatrices().pushMatrix();

        // WICHTIG: Die Verschiebung auch hier anwenden, da TAIL nach popMatrix() liegt
        context.getMatrices().translate(12.0f, 0.0f);

        // Standard Chat-Transformationen nachbauen
        context.getMatrices().scale(scale, scale);
        context.getMatrices().translate(4.0f, 0.0f);

        int windowHeight = context.getScaledWindowHeight();
        int startY = MathHelper.floor((float)(windowHeight - 40) / scale);

        double lineSpacing = this.client.options.getChatLineSpacing().getValue();
        int lineHeight = (int)((double)9 * (lineSpacing + 1.0));
        int textOffset = (int)Math.round(8.0 * (lineSpacing + 1.0) - 4.0 * lineSpacing);

        int visibleCount = this.getVisibleLineCount();
        int lineCount = this.visibleMessages.size();
        int startLine = Math.min(lineCount - this.scrolledLines, visibleCount) - 1;

        for (int i = startLine; i >= 0; --i) {
            int listIndex = i + this.scrolledLines;
            if (listIndex >= this.visibleMessages.size()) continue;

            ChatHudLine.Visible visibleLine = this.visibleMessages.get(listIndex);

            float opacity = 1.0f;
            if (!interactable) {
                int age = currentTick - visibleLine.addedTime();
                double d = (double)age / 200.0;
                d = 1.0 - d;
                d *= 10.0;
                d = MathHelper.clamp(d, 0.0, 1.0);
                d *= d;
                opacity = (float)d;
            }

            if (opacity > 0.005f) {
                int lineY = startY - i * lineHeight;
                int drawY = lineY - textOffset;
                simpletweaks$drawHeadForLine(context, visibleLine, drawY, opacity);
            }
        }

        context.getMatrices().popMatrix();
    }

    @Unique
    private void simpletweaks$drawHeadForLine(DrawContext context, ChatHudLine.Visible visibleLine, int y, float opacity) {
        ChatHudLine rawLine = null;
        for (ChatHudLine line : this.messages) {
            if (line.creationTick() == visibleLine.addedTime()) {
                rawLine = line;
                break;
            }
        }
        if (rawLine == null) return;

        String senderName = simpletweaks$getSenderName(rawLine.content());
        if (senderName == null) return;

        PlayerListEntry playerEntry = this.client.getNetworkHandler().getPlayerListEntry(senderName);
        if (playerEntry == null) return;

        Identifier skin = playerEntry.getSkinTextures().body().texturePath();

        int headSize = 8;
        // Positionierung: Links vom Text (x=0)
        int x = -headSize - 2;

        int alphaInt = (int)(opacity * 255.0f);
        if (alphaInt < 4) return;
        int color = alphaInt << 24 | 0xFFFFFF;

        // Köpfe zeichnen (RenderPipelines importieren nicht vergessen!)
        context.drawTexture(RenderPipelines.GUI_TEXTURED, skin, x, y, 8.0F, 8.0F, headSize, headSize, 8, 8, 64, 64, color);
        context.drawTexture(RenderPipelines.GUI_TEXTURED, skin, x, y, 40.0F, 8.0F, headSize, headSize, 8, 8, 64, 64, color);
    }

    @Unique
    private String simpletweaks$getSenderName(Text text) {
        if (text.getContent() instanceof TranslatableTextContent translatable) {
            String key = translatable.getKey();
            if (key.startsWith("chat.type.")) {
                Object[] args = translatable.getArgs();
                if (args.length > 0 && args[0] instanceof Text senderText) {
                    String name = senderText.getString();
                    name = name.replaceAll("[<>]", "");
                    return name.trim();
                }
            }
        }
        return null;
    }
}