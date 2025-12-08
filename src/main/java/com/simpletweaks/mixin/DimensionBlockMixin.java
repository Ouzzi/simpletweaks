package com.simpletweaks.mixin;

import com.simpletweaks.Simpletweaks;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerEntity.class)
public abstract class DimensionBlockMixin {

    @Inject(method = "teleportTo", at = @At("HEAD"), cancellable = true)
    private void blockDimensionChange(TeleportTarget teleportTarget, CallbackInfoReturnable<ServerPlayerEntity> cir) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this;

        // In 1.21 the destination world is wrapped inside the TeleportTarget object
        ServerWorld destination = teleportTarget.world();
        RegistryKey<World> destKey = destination.getRegistryKey();

        // Nether Check
        if (destKey == World.NETHER && !Simpletweaks.getConfig().dimensions.allowNether) {
            player.sendMessage(Text.literal("The Nether is currently disabled.").formatted(Formatting.RED), true);
            cir.setReturnValue(null); // Teleport abbrechen
        }

        // End Check
        if (destKey == World.END && !Simpletweaks.getConfig().dimensions.allowEnd) {
            player.sendMessage(Text.literal("The End is currently disabled.").formatted(Formatting.RED), true);
            cir.setReturnValue(null); // Teleport abbrechen
        }
    }
}