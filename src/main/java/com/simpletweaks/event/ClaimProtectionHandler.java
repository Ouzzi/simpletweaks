package com.simpletweaks.event;

import com.simpletweaks.world.ClaimState;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ChunkPos;

public class ClaimProtectionHandler {

    public static void register() {
        // 1. Block Abbauen verhindern
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, entity) -> {
            // KORREKTUR: world.isClient ist oft ein Feld, keine Methode.
            if (world.isClient() || !(player instanceof ServerPlayerEntity serverPlayer)) return true;
            return checkPermission(serverPlayer, new ChunkPos(pos), true);
        });

        // 2. Block Interaktion (Kisten öffnen, Knöpfe, Bauen etc.) verhindern
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (world.isClient() || !(player instanceof ServerPlayerEntity serverPlayer)) return ActionResult.PASS;

            // Wenn keine Erlaubnis -> FAIL (Verhindert Interaktion)
            if (!checkPermission(serverPlayer, new ChunkPos(hitResult.getBlockPos()), false)) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        // 3. Entity Interaktion (Villager handeln, Rahmen drehen)
        UseEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient() || !(player instanceof ServerPlayerEntity serverPlayer)) return ActionResult.PASS;
            if (!checkPermission(serverPlayer, new ChunkPos(entity.getBlockPos()), false)) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });

        // 4. Schlagen / PvP / Tiere töten
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (world.isClient() || !(player instanceof ServerPlayerEntity serverPlayer)) return ActionResult.PASS;

            // Ausnahme: Hostile Mobs (Monster) darf man immer schlagen
            if (!entity.getType().getSpawnGroup().isPeaceful()) {
                return ActionResult.PASS;
            }

            if (!checkPermission(serverPlayer, new ChunkPos(entity.getBlockPos()), true)) {
                return ActionResult.FAIL;
            }
            return ActionResult.PASS;
        });
    }

    private static boolean checkPermission(ServerPlayerEntity player, ChunkPos pos, boolean sendFeedback) {
        // KORREKTUR: getEntityWorld() statt getServerWorld()
        // Deine ServerPlayerEntity.java definiert getEntityWorld() als public Methode.
        ServerWorld world = player.getEntityWorld();
        ClaimState state = ClaimState.get(world);

        if (state.canInteract(pos, player)) {
            return true;
        }

        if (sendFeedback) {
            player.sendMessage(Text.literal("This chunk is claimed!").formatted(Formatting.RED), true);
        }
        return false;
    }
}