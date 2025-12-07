package com.simpletweaks.mixin;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.util.IVaultCooldown;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.block.vault.VaultConfig;
import net.minecraft.block.vault.VaultServerData;
import net.minecraft.block.vault.VaultSharedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mixin(VaultBlockEntity.Server.class)
public class VaultBlockEntityServerMixin {

    // --- TICK LOGIK ---
    @Inject(method = "tick", at = @At("HEAD"))
    private static void tickCooldowns(ServerWorld world, BlockPos pos, BlockState state, VaultConfig config, VaultServerData serverData, VaultSharedData sharedData, CallbackInfo ci) {
        // Nur jede Sekunde (20 Ticks) prüfen
        if (world.getTime() % 20 != 0) return;

        if (serverData instanceof IVaultCooldown cooldownData) {
            long now = world.getTime();

            // Wir brauchen Zugriff auf die Sets
            Set<UUID> rewardedPlayers = ((VaultServerDataAccessor) serverData).getRewardedPlayersSet();
            Set<UUID> connectedPlayers = ((VaultSharedDataAccessor) sharedData).getConnectedPlayersSet();

            // Kopie erstellen, um ConcurrentModificationException zu vermeiden (da wir im Loop löschen wollen)
            Set<UUID> playersToCheck = new HashSet<>(rewardedPlayers);
            boolean changed = false;

            for (UUID uuid : playersToCheck) {
                // Hat der Spieler seinen Cooldown abgesessen?
                // Hinweis: hasLootedRecently liefert 'false' zurück, wenn der Cooldown VORBEI ist.
                if (!cooldownData.hasLootedRecently(uuid, now)) {

                    // JA! Cooldown vorbei. Spieler entfernen.
                    rewardedPlayers.remove(uuid);
                    connectedPlayers.remove(uuid);
                    Simpletweaks.LOGGER.info("Vault Tick: Cooldown abgelaufen für " + uuid);
                    changed = true;
                }
            }

            // Wenn wir jemanden gelöscht haben, müssen wir speichern und syncen
            if (changed) {
                ((VaultServerDataAccessor) serverData).setDirty(true);
                ((VaultSharedDataAccessor) sharedData).setDirty(true);

                // FIX: Statischen markDirty Aufruf nutzen!
                // Da markDirty protected sein könnte, nutzen wir world.markDirty oder einen Accessor/Invoker falls nötig.
                // Aber VaultBlockEntity.markDirty(world, pos, state) ist oft protected.
                // Einfacher: world.markDirty(pos) reicht meistens!
                world.markDirty(pos);

                // Status Update an alle senden
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            }
        }
    }

    // --- ALTE LOGIK (Als Sicherheit beim Klicken) ---
    @Inject(method = "tryUnlock", at = @At("HEAD"))
    private static void checkCooldownClick(ServerWorld world, BlockPos pos, BlockState state, VaultConfig config, VaultServerData serverData, VaultSharedData sharedData, PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        // Diese Methode fängt Fälle ab, wo der Tick vielleicht noch nicht lief,
        // der Spieler aber schon klickt.
        if (serverData instanceof IVaultCooldown cooldownData) {
            UUID uuid = player.getUuid();
            Set<UUID> rewardedPlayers = ((VaultServerDataAccessor) serverData).getRewardedPlayersSet();

            if (rewardedPlayers.contains(uuid) && !cooldownData.hasLootedRecently(uuid, world.getTime())) {
                // Sofortiger Reset beim Klick
                rewardedPlayers.remove(uuid);
                Set<UUID> connectedPlayers = ((VaultSharedDataAccessor) sharedData).getConnectedPlayersSet();
                connectedPlayers.remove(uuid);

                ((VaultServerDataAccessor) serverData).setDirty(true);
                ((VaultSharedDataAccessor) sharedData).setDirty(true);
                world.updateListeners(pos, state, state, 3);

                Simpletweaks.LOGGER.info("Vault Klick-Reset für " + player.getName().getString());
            }
        }
    }

    @Inject(method = "tryUnlock", at = @At("TAIL"))
    private static void saveCooldown(ServerWorld world, BlockPos pos, BlockState state, VaultConfig config, VaultServerData serverData, VaultSharedData sharedData, PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        Set<UUID> rewardedPlayers = ((VaultServerDataAccessor) serverData).getRewardedPlayersSet();

        // Wenn Loot erfolgreich war -> Zeit speichern
        if (rewardedPlayers.contains(player.getUuid())) {
            if (serverData instanceof IVaultCooldown cooldownData) {
                cooldownData.markLooted(player.getUuid(), world.getTime());
                ((VaultServerDataAccessor) serverData).setDirty(true);

                // Debug
                Simpletweaks.LOGGER.info("Vault: Zeit gespeichert für " + player.getName().getString());
            }
        }
    }
}