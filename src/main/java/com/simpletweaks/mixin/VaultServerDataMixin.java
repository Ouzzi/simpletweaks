package com.simpletweaks.mixin;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.util.IVaultCooldown;
import net.minecraft.block.vault.VaultServerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mixin(VaultServerData.class)
public class VaultServerDataMixin implements IVaultCooldown {

    @Unique
    private Map<UUID, Long> lastLootTimes = new HashMap<>();

    @Override
    public boolean hasLootedRecently(UUID playerUuid, long worldTime) {
        if (!lastLootTimes.containsKey(playerUuid)) {
            Simpletweaks.LOGGER.info("Vault Check: Spieler " + playerUuid + " war noch nie hier. Erlaubt.");
            return false;
        }

        long lastLoot = lastLootTimes.get(playerUuid);
        long diff = worldTime - lastLoot;

        // Config laden mit Fallback
        long configDays = 50; // Standard 100 Tage
        try {
            if (Simpletweaks.getConfig() != null) {
                configDays = Simpletweaks.getConfig().vaults.vaultCooldownDays;
                Simpletweaks.LOGGER.info("Geladener Vault Cooldown aus Config: " + configDays + " Tage");
            }
        } catch (Exception e) {
            Simpletweaks.LOGGER.error("Fehler beim Laden der Vault Config, nutze Standard 100 Tage", e);
        }

        long cooldownTicks = configDays * 24000L;

        // Debug Ausgabe in die Konsole
        Simpletweaks.LOGGER.info("Vault Check für " + playerUuid + ":");
        Simpletweaks.LOGGER.info(" - Letzter Loot: " + lastLoot);
        Simpletweaks.LOGGER.info(" - Jetzige Zeit: " + worldTime);
        Simpletweaks.LOGGER.info(" - Differenz: " + diff + " Ticks");
        Simpletweaks.LOGGER.info(" - Cooldown Zeit: " + cooldownTicks + " Ticks (" + configDays + " Tage)");

        // Wenn Zeit zurückgedreht wurde (diff negativ), erlauben wir es sicherheitshalber
        if (diff < 0) return false;

        boolean isOnCooldown = diff < cooldownTicks;
        Simpletweaks.LOGGER.info(" - Noch im Cooldown? " + isOnCooldown);

        return isOnCooldown;
    }

    /*

    @Override
    public boolean hasLootedRecently(UUID playerUuid, long worldTime) {
        if (!lastLootTimes.containsKey(playerUuid)) return false;
        long lastLoot = lastLootTimes.get(playerUuid);

        // TEST: Nur 10 Sekunden Cooldown (200 Ticks)
        return (worldTime - lastLoot) < 200L;
    }
     */

    @Override
    public void markLooted(UUID playerUuid, long worldTime) {
        lastLootTimes.put(playerUuid, worldTime);
        Simpletweaks.LOGGER.info("Vault: Spieler " + playerUuid + " hat gelootet bei Zeit " + worldTime);
    }

    @Override
    public Map<UUID, Long> getLootTimesMap() {
        return lastLootTimes;
    }

    @Override
    public void setLootTimesMap(Map<UUID, Long> map) {
        this.lastLootTimes = new HashMap<>(map);
    }
}