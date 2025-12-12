package com.simpletweaks.event;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.block.ModBlocks;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class FirstJoinHandler {
    private static final String FIRST_JOIN_KEY = "simpletweaks.first_join";

    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();

            // Prüfen, ob Spieler schon mal da war
            if (!player.getCommandTags().contains(FIRST_JOIN_KEY)) {

                // Config abrufen
                int amount = Simpletweaks.getConfig().spawn.spawnTeleporterCount;

                // Nur Items geben, wenn die Anzahl größer als 0 ist
                if (amount > 0) {
                    giveStarterItems(player, amount);
                }

                // Tag setzen, damit es nicht nochmal passiert
                player.addCommandTag(FIRST_JOIN_KEY);
            }
        });
    }

    private static void giveStarterItems(ServerPlayerEntity player, int amount) {
        // Item mit der konfigurierten Anzahl erstellen
        ItemStack teleporter = new ItemStack(ModBlocks.SPAWN_TELEPORTER, amount);

        teleporter.set(DataComponentTypes.CUSTOM_NAME, Text.literal("Home Teleporter").formatted(Formatting.AQUA));

        if (!player.getInventory().insertStack(teleporter)) {
            player.dropItem(teleporter, false);
        }

        // Nachricht anpassen je nach Menge (Singular/Plural optional, hier einfach halten)
        player.sendMessage(Text.literal("You received " + amount + " Spawn Teleporter(s)!").formatted(Formatting.GREEN), false);
    }
}