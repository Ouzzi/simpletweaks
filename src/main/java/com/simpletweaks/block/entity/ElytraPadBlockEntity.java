package com.simpletweaks.block.entity;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.block.custom.ElytraPadBlock;
import com.simpletweaks.component.ModDataComponentTypes;
import com.simpletweaks.config.SimpletweaksConfig;
import com.simpletweaks.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.UUID;

public class ElytraPadBlockEntity extends BlockEntity {
    private UUID ownerUuid;

    public ElytraPadBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ELYTRA_PAD_BE, pos, state);
    }

    public void setOwner(UUID uuid) { this.ownerUuid = uuid; markDirty(); }
    public boolean isOwner(PlayerEntity player) { return ownerUuid != null && ownerUuid.equals(player.getUuid()); }

    public static void tick(World world, BlockPos pos, BlockState state, ElytraPadBlockEntity be) {
        if (world.isClient() || world.getTime() % 10 != 0) return; // Server only, alle 0.5 Sek

        int tier = 1; // Default
        if (state.getBlock() instanceof ElytraPadBlock padBlock) {
            tier = padBlock.getTier();
        }
        Box range = getBoxForTier(pos, tier);

        List<ServerPlayerEntity> players = world.getEntitiesByClass(ServerPlayerEntity.class, range, p -> true);

        SimpletweaksConfig.Spawn config = Simpletweaks.getConfig().spawn;

        for (ServerPlayerEntity player : players) {
            ItemStack chestStack = player.getEquippedStack(EquipmentSlot.CHEST);
            
            // 1. Elytra geben, wenn Slot leer
            if (chestStack.isEmpty()) {
                ItemStack elytra = new ItemStack(ModItems.SPAWN_ELYTRA);
                initElytraData(elytra, config);
                // Standardmäßig NICHT sicher (Falle/Kinetic enabled), außer es kommt vom Spawn.
                elytra.set(ModDataComponentTypes.IS_SAFE_ELYTRA, false);
                // Wir sind auf dem Pad, also Valid
                elytra.set(ModDataComponentTypes.LAST_PAD_TICK, world.getTime());

                player.equipStack(EquipmentSlot.CHEST, elytra);
                player.sendMessage(net.minecraft.text.Text.literal("Elytra equipped by Pad!").formatted(net.minecraft.util.Formatting.GREEN), true);
            } 
            // 2. Elytra aufladen, wenn vorhanden
            else if (chestStack.isOf(ModItems.SPAWN_ELYTRA)) {
                // Sagen, dass wir hier sicher sind (verhindert despawn im SpawnHandler)
                chestStack.set(ModDataComponentTypes.LAST_PAD_TICK, world.getTime());

                // Flugzeit IMMER resetten im Radius (damit man nicht abstürzt)
                chestStack.set(ModDataComponentTypes.FLIGHT_TIME, config.flightTimeSeconds * 20);

                // FIX: Boosts nur im 3x3 Bereich (direkt über Pad) aufladen
                if (isInBoostColumn(player, pos)) {
                    chestStack.set(ModDataComponentTypes.BOOST_LEVEL, 1.0f);
                }

                // Safe State NICHT überschreiben! Wenn sie vom Spawn kommt (True), bleibt sie True.

                player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 20, 0, true, false, false));
            }
        }
    }

    private static boolean isInBoostColumn(ServerPlayerEntity player, BlockPos pos) {
        // 3x3 Säule zentriert auf Pad, etwas höher
        Box boostBox = new Box(pos).expand(1.5, 0, 1.5).stretch(0, 4.0, 0);
        return boostBox.intersects(player.getBoundingBox());
    }

    private static void initElytraData(ItemStack stack, SimpletweaksConfig.Spawn config) {
        stack.set(ModDataComponentTypes.FLIGHT_TIME, config.flightTimeSeconds * 20);
        stack.set(ModDataComponentTypes.BOOST_LEVEL, 1.0f);
    }

    private static Box getBoxForTier(BlockPos pos, int tier) {
        // Tier 1: 5x5x15 -> Radius (Breite) ~2.5 -> Box +/- 2.5
        // Die Angabe "5x5" interpretiere ich als Gesamtbreite.
        // Höhe geht vom Pad nach oben.
        
        double xzRadius;
        double height;
        double offsetY = 2.0;

        switch (tier) {
            case 2 -> { xzRadius = 7.5; height = 31; }  // 15x15x31
            case 3 -> { xzRadius = 15.5; height = 63; } // 31x31x63
            case 4 -> { xzRadius = 31.5; height = 127; } // 63x63x127
            default -> { xzRadius = 2.5; height = 15; } // Tier 1: 5x5x15
        }
        
        // Box zentriert auf Blockmitte (x+0.5), y startet am Block
        return new Box(pos).expand(xzRadius, 0-offsetY, xzRadius).stretch(0, height-offsetY, 0);
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        if (ownerUuid != null) {view.put("Owner", Uuids.INT_STREAM_CODEC, ownerUuid);}
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        view.read("Owner", Uuids.INT_STREAM_CODEC).ifPresent(uuid -> this.ownerUuid = uuid);
    }
}