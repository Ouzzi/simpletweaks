package com.simpletweaks.mixin;

import net.minecraft.block.vault.VaultSharedData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;
import java.util.UUID;

@Mixin(VaultSharedData.class)
public interface VaultSharedDataAccessor {
    @Accessor("connectedPlayers")
    Set<UUID> getConnectedPlayersSet();

    // NEU: Damit der Client das Update (Partikel weg) mitbekommt
    @Accessor("dirty")
    void setDirty(boolean dirty);
}