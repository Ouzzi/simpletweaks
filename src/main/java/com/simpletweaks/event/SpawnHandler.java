package com.simpletweaks.event;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.component.ModDataComponentTypes;
import com.simpletweaks.config.SimpletweaksConfig;
import com.simpletweaks.item.ModItems;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public class SpawnHandler {

    public static void register() {
        // Beim ersten Join
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            tickSpawnLogic(handler.getPlayer());
        });

        // Beim Respawn
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            tickSpawnLogic(newPlayer);
        });

        // Server Tick für Timer-Logik, Cleanup und Re-Entry
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                tickSpawnLogic(player);
            }
        });
    }

    private static void tickSpawnLogic(ServerPlayerEntity player) {
        SimpletweaksConfig.Spawn config = Simpletweaks.getConfig().spawn;
        if (!config.giveElytraOnSpawn) return;

        // --- 1. CLEANUP (Elytra darf nur im Brust-Slot existieren) ---

        // A. Mauszeiger (Cursor Stack) prüfen
        ItemStack cursorStack = player.currentScreenHandler.getCursorStack();
        if (cursorStack.isOf(ModItems.SPAWN_ELYTRA)) {
            player.currentScreenHandler.setCursorStack(ItemStack.EMPTY); // Löschen
        }

        // B. Inventar prüfen (Alles außer Brust-Slot)
        for (int i = 0; i < player.getInventory().size(); i++) {
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isOf(ModItems.SPAWN_ELYTRA)) {
                // Slot 38 ist der Brustplatten-Slot im Spieler-Inventar
                // Wir prüfen, ob der aktuelle Slot NICHT der Brust-Slot ist.
                boolean isChestSlot = (i == 38);

                if (!isChestSlot) {
                    player.getInventory().removeStack(i); // Löschen
                }
            }
        }

        // --- 2. RADIUS LOGIK ---

        BlockPos worldSpawn = config.useWorldSpawnAsCenter
                ? player.getEntityWorld().getSpawnPoint().getPos()
                : new BlockPos(config.customSpawnElytraX, 0, config.customSpawnElytraZ);

        int radius = config.spawnElytraRadius;
        double distX = Math.abs(player.getX() - worldSpawn.getX());
        double distZ = Math.abs(player.getZ() - worldSpawn.getZ());
        boolean insideSpawn = distX <= radius && distZ <= radius;

        ItemStack chestStack = player.getEquippedStack(EquipmentSlot.CHEST);
        boolean isWearingSpawnElytra = chestStack.isOf(ModItems.SPAWN_ELYTRA);

        if (insideSpawn) {
            // Im Spawn-Bereich
            if (isWearingSpawnElytra) {
                // Aufladen (Timer & Boost resetten)
                initElytraData(chestStack, config);
            } else if (chestStack.isEmpty()) {
                // Nur geben, wenn Slot LEER ist.
                // Wenn der Spieler eine normale Brustplatte trägt, tun wir nichts.
                ItemStack elytra = new ItemStack(ModItems.SPAWN_ELYTRA);
                initElytraData(elytra, config);
                player.equipStack(EquipmentSlot.CHEST, elytra);
            }
        }
        else if (isWearingSpawnElytra) {
            // Außerhalb Spawn-Bereich -> Timer Logik
            Integer ticksLeft = chestStack.get(ModDataComponentTypes.FLIGHT_TIME);
            if (ticksLeft == null) ticksLeft = config.flightTimeSeconds * 20;

            // Timer läuft nur, wenn man gleitet (Fliegt)
            if (player.isGliding()) {
                ticksLeft--;
            }

            chestStack.set(ModDataComponentTypes.FLIGHT_TIME, ticksLeft);

            boolean timeUp = ticksLeft <= 0;
            // Entfernen wenn gelandet (und nicht mehr am gleiten) oder Zeit abgelaufen
            boolean landed = player.isOnGround() && !player.isGliding();

            if (timeUp || landed) {
                player.equipStack(EquipmentSlot.CHEST, ItemStack.EMPTY);
                player.sendMessage(Text.literal("Spawn Elytra expired.").formatted(Formatting.YELLOW), true);
            } else if (ticksLeft == 200) { // 10 Sekunden Warnung
                player.sendMessage(Text.literal("Elytra expires in 10 seconds!").formatted(Formatting.RED), true);
            }
        }
    }

    private static void initElytraData(ItemStack stack, SimpletweaksConfig.Spawn config) {
        stack.set(ModDataComponentTypes.FLIGHT_TIME, config.flightTimeSeconds * 20);
        stack.set(ModDataComponentTypes.BOOST_LEVEL, 1.0f);
    }
}