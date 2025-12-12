package com.simpletweaks.mixin;

import com.simpletweaks.Simpletweaks;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class ExactSpawnMixin {

    // 1. WELT SPAWN: Verhindert den Zufallsradius beim Spawnen (Fuzzing)
    @Inject(method = "getWorldSpawnPos", at = @At("HEAD"), cancellable = true)
    private void onGetWorldSpawnPos(ServerWorld world, BlockPos basePos, CallbackInfoReturnable<BlockPos> cir) {
        if (Simpletweaks.getConfig().spawn.forceExactSpawn) {
            // Gibt direkt die Basis-Position zurück, ohne SpawnLocating.locateSpawnPos aufzurufen (was den Radius addiert)
            cir.setReturnValue(basePos);
        }
    }

    // 2. BETT SPAWN & GENERELLER RESPAWN: Position korrigieren
    // Wir nutzen getRespawnTarget statt findRespawnPosition, da RespawnPos private/unsichtbar ist.
    @Inject(method = "getRespawnTarget", at = @At("RETURN"), cancellable = true)
    private void onGetRespawnTarget(boolean alive, TeleportTarget.PostDimensionTransition postDimensionTransition, CallbackInfoReturnable<TeleportTarget> cir) {
        if (!Simpletweaks.getConfig().spawn.forceExactSpawn) return;

        TeleportTarget original = cir.getReturnValue();
        if (original == null) return; // Kein valider Respawn gefunden

        ServerWorld world = original.world();
        Vec3d pos = original.position();
        BlockPos blockPos = BlockPos.ofFloored(pos);
        BlockState state = world.getBlockState(blockPos);

        // Prüfen, ob wir auf einem Bett spawnen
        if (state.getBlock() instanceof BedBlock) {
            // Exakt auf das Kissen setzen (Mitte des Blocks + Höhe des Bettes)
            // BedBlock ist 0.5625 Blöcke hoch
            Vec3d exactPos = new Vec3d(
                    blockPos.getX() + 0.5d,
                    blockPos.getY() + 0.5625d,
                    blockPos.getZ() + 0.5d
            );

            // Neues Target erstellen mit exakter Position
            TeleportTarget newTarget = new TeleportTarget(
                    world,
                    exactPos,
                    Vec3d.ZERO, // Keine Bewegung
                    original.yaw(),
                    original.pitch(),
                    postDimensionTransition
            );

            cir.setReturnValue(newTarget);
        }
    }
}