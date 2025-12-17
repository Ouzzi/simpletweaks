package com.simpletweaks.mixin.client;

import com.simpletweaks.client.gui.PickupNotifierHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Shadow private ClientWorld world;

    // Speichert das letzte Item, um Duplikate zu verhindern
    @Unique private int simpletweaks$lastEntityId = -1;
    @Unique private long simpletweaks$lastPickupTime = 0;

    @Inject(method = "onItemPickupAnimation", at = @At("HEAD"))
    private void onPickupAnimation(ItemPickupAnimationS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || this.world == null) return;

        // 1. Prüfen, ob wir der Sammler sind
        Entity collector = this.world.getEntityById(packet.getCollectorEntityId());
        if (collector != client.player) return;

        // 2. Duplikat-Schutz
        // Wenn dasselbe Entity (gleiche ID) innerhalb von 5 Ticks (250ms) nochmal kommt -> Ignorieren
        long currentTime = System.currentTimeMillis();
        if (packet.getEntityId() == simpletweaks$lastEntityId && (currentTime - simpletweaks$lastPickupTime) < 250) {
            return;
        }

        // Merken für das nächste Mal
        simpletweaks$lastEntityId = packet.getEntityId();
        simpletweaks$lastPickupTime = currentTime;

        // 3. Entity holen
        Entity pickedUpEntity = this.world.getEntityById(packet.getEntityId());
        if (pickedUpEntity == null) return;

        // 4. Verarbeiten
        if (pickedUpEntity instanceof ItemEntity itemEntity) {
            // Priority: Packet Amount -> Stack Count
            int amount = packet.getStackAmount();
            if (amount <= 0) {
                amount = itemEntity.getStack().getCount();
            }
            // Wenn immer noch 0 (selten), nimm 1
            if (amount <= 0) amount = 1;

            PickupNotifierHud.addNotification(itemEntity.getStack(), amount);
        }
        else if (pickedUpEntity instanceof ExperienceOrbEntity xpOrb) {
            // XP hat keine "Packet Amount" im Animation Packet, wir nehmen den Wert vom Orb
            int xpAmount = xpOrb.getValue();
            if (xpAmount > 0) {
                PickupNotifierHud.addXpNotification(xpAmount);
            }
        }
    }
}