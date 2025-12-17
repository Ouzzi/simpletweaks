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

    // 1. WELT SPAWN FUZZING (Entfernt den Zufallsradius)
    // Wir fangen getWorldSpawnPos ab, bevor es den Radius addiert.
    @Inject(method = "getWorldSpawnPos", at = @At("HEAD"), cancellable = true)
    private void onGetWorldSpawnPos(ServerWorld world, BlockPos basePos, CallbackInfoReturnable<BlockPos> cir) {
        if (Simpletweaks.getConfig().spawn.forceExactSpawn) {
            // Gibt direkt die Basis-Position (unsere Config-Position) zurück
            cir.setReturnValue(basePos);
        }
    }

    // 2. BETT SPAWN & WELT SPAWN ZENTRIERUNG
    @Inject(method = "getRespawnTarget", at = @At("RETURN"), cancellable = true)
    private void onGetRespawnTarget(boolean alive, TeleportTarget.PostDimensionTransition postDimensionTransition, CallbackInfoReturnable<TeleportTarget> cir) {
        if (!Simpletweaks.getConfig().spawn.forceExactSpawn) return;

        TeleportTarget original = cir.getReturnValue();
        if (original == null) return;

        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;
        ServerWorld world = original.world();
        BlockPos pos = BlockPos.ofFloored(original.position());
        BlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof BedBlock) {
            Vec3d exactPos = new Vec3d(pos.getX() + 0.5d, pos.getY() + 0.5625d, pos.getZ() + 0.5d);
            cir.setReturnValue(new TeleportTarget(world, exactPos, Vec3d.ZERO, original.yaw(), original.pitch(), postDimensionTransition));
            return;
        }

        if (player.getRespawn() == null) {
            BlockPos worldSpawn = world.getSpawnPoint().getPos();

            Vec3d exactWorldSpawn = new Vec3d(worldSpawn.getX() + 0.5d, worldSpawn.getY(), worldSpawn.getZ() + 0.5d);

            cir.setReturnValue(new TeleportTarget(world, exactWorldSpawn, Vec3d.ZERO, original.yaw(), original.pitch(), postDimensionTransition));
        }
    }
}