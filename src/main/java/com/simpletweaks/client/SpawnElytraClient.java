package com.simpletweaks.client;

import com.simpletweaks.item.ModItems;
import com.simpletweaks.network.SpawnElytraNetworking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

public class SpawnElytraClient {
    private static boolean spaceWasPressed = false;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            ItemStack chest = client.player.getEquippedStack(EquipmentSlot.CHEST);
            if (!chest.isOf(ModItems.SPAWN_ELYTRA)) return;

            boolean spacePressed = client.options.jumpKey.isPressed();

            // Nur senden, wenn Space *neu* gedrückt wurde (kein Dauerfeuer) und wir fliegen
            if (spacePressed && !spaceWasPressed && client.player.isGliding()) {
                ClientPlayNetworking.send(new SpawnElytraNetworking.BoostPayload());
            }

            spaceWasPressed = spacePressed;
        });
    }
}