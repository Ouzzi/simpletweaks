package com.simpletweaks.event;

import com.simpletweaks.Simpletweaks;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ProfileComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;

public class PlayerHeadDropHandler {

    public static void register() {
        ServerLivingEntityEvents.AFTER_DEATH.register(PlayerHeadDropHandler::onDeath);
    }

    private static void onDeath(LivingEntity entity, DamageSource source) {
        // Config Check
        if (!Simpletweaks.getConfig().pvp.playerHeadDrops) {
            return;
        }

        // Ist das Opfer ein Spieler?
        if (entity instanceof ServerPlayerEntity victim) {
            // Ist der Mörder ein Spieler?
            if (source.getAttacker() instanceof PlayerEntity) {
                dropHead(victim);
            }
        }
    }

    private static void dropHead(ServerPlayerEntity victim) {
        ItemStack headStack = new ItemStack(Items.PLAYER_HEAD);

        // Das Profil (Skin) des Opfers auf den Kopf setzen
        // FIX: ProfileComponent ist abstrakt. Nutze die Factory-Methode 'ofStatic'.
        headStack.set(DataComponentTypes.PROFILE, ProfileComponent.ofStatic(victim.getGameProfile()));

        // Item droppen (dropItem ist die sicherere Methode für Spieler)
        // true = zufällige Richtung/Geschwindigkeit (wie beim normalen Droppen)
        victim.dropItem(headStack, true);
    }
}