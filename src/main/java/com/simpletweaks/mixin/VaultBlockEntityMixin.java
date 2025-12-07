package com.simpletweaks.mixin;

import com.mojang.serialization.Codec;
import com.simpletweaks.util.IVaultCooldown;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.VaultBlockEntity;
import net.minecraft.block.vault.VaultServerData;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;
import java.util.UUID;

@Mixin(VaultBlockEntity.class)
public abstract class VaultBlockEntityMixin extends BlockEntity {

    @Shadow public abstract VaultServerData getServerData();

    @Unique
    private static final Codec<Map<UUID, Long>> LOOT_TIMES_CODEC = Codec.unboundedMap(Uuids.CODEC, Codec.LONG);

    public VaultBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    // writeData und readData bleiben hier (sind korrekt!)
    @Inject(method = "writeData", at = @At("TAIL"))
    private void writeCustomData(WriteView view, CallbackInfo ci) {
        VaultServerData data = this.getServerData();

        if (data instanceof IVaultCooldown cooldownData) {
            Map<UUID, Long> times = cooldownData.getLootTimesMap();

            if (times != null && !times.isEmpty()) {
                view.put("SimpleBuildingLootTimes", LOOT_TIMES_CODEC, times);
            }
        }
    }

    @Inject(method = "readData", at = @At("TAIL"))
    private void readCustomData(ReadView view, CallbackInfo ci) {
        VaultServerData data = this.getServerData();

        if (data instanceof IVaultCooldown cooldownData) {
            // FIX: Hier stand .get, es muss aber .read sein!
            view.read("SimpleBuildingLootTimes", LOOT_TIMES_CODEC).ifPresent(loadedMap -> {
                cooldownData.setLootTimesMap(loadedMap);
            });
        }
    }
}