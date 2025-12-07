package com.simpletweaks.util;

import java.util.Map;
import java.util.UUID;

public interface IVaultCooldown {
    boolean hasLootedRecently(UUID playerUuid, long worldTime);
    void markLooted(UUID playerUuid, long worldTime);

    // NEU: Getter und Setter für die Speicherung
    Map<UUID, Long> getLootTimesMap();
    void setLootTimesMap(Map<UUID, Long> map);
}