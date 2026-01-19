package com.simpletweaks.block.entity;

import com.simpletweaks.block.custom.FlypadBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.*;

public class FlypadBlockEntity extends BlockEntity {
    // Wir speichern die Spieler, die wir im letzten Tick fliegen ließen, um es zu widerrufen, wenn sie den Bereich verlassen.
    private final Set<UUID> flyingPlayers = new HashSet<>();
    private UUID ownerUuid;

    public FlypadBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLYPAD_BE, pos, state);
    }

    public void setOwner(UUID uuid) { this.ownerUuid = uuid; markDirty(); }
    public boolean isOwner(PlayerEntity player) { return ownerUuid != null && ownerUuid.equals(player.getUuid()); }

    public static void tick(World world, BlockPos pos, BlockState state, FlypadBlockEntity be) {
        if (world.isClient()) return;
        
        // Alle 5 Ticks Logik (Performance), aber Glowing jeden Tick oder länger
        if (world.getTime() % 5 != 0) return;

        int tier = 1;
        if (state.getBlock() instanceof FlypadBlock flypad) {
            tier = flypad.getTier();
        }

        Box range = getBoxForTier(pos, tier);
        List<ServerPlayerEntity> currentPlayers = world.getEntitiesByClass(ServerPlayerEntity.class, range, p -> true);
        Set<UUID> currentUUIDs = new HashSet<>();

        for (ServerPlayerEntity player : currentPlayers) {
            currentUUIDs.add(player.getUuid());

            // 1. Creative Flight erlauben
            if (!player.getAbilities().allowFlying) {
                player.getAbilities().allowFlying = true;
                player.sendAbilitiesUpdate();
            }
            
            // 2. Glowing Effect (Spectral Arrow)
            // 20 Ticks = 1 Sekunde. Wir refreshen es alle 5 Ticks, also Dauer 10-20 Ticks reicht.
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 10, 0, true, false, false));
        }

        // Cleanup: Spieler, die nicht mehr im Radius sind, verlieren Flight
        // (außer sie sind im Creative/Spectator Mode)
        Iterator<UUID> it = be.flyingPlayers.iterator();
        while (it.hasNext()) {
            UUID id = it.next();
            if (!currentUUIDs.contains(id)) {
                // Spieler hat Bereich verlassen
                ServerPlayerEntity p = world.getServer().getPlayerManager().getPlayer(id);
                if (p != null) {
                    if (!p.isCreative() && !p.isSpectator()) {
                        p.getAbilities().allowFlying = false;
                        p.getAbilities().flying = false; // Absturz verhindern? Minecraft regelt das meist weich, aber sicherheitshalber aus.
                        p.sendAbilitiesUpdate();
                    }
                }
                it.remove();
            }
        }

        be.flyingPlayers.addAll(currentUUIDs);
    }

    private static Box getBoxForTier(BlockPos pos, int tier) {
        double xzRadius;
        double height;
        switch (tier) {
            case 2 -> { xzRadius = 7.5; height = 31; }  // Reinforced
            case 3 -> { xzRadius = 15.5; height = 63; } // Netherite
            case 4 -> { xzRadius = 31.5; height = 127; } // Stellar
            default -> { xzRadius = 2.5; height = 15; } // Tier 1
        }
        return new Box(pos).expand(xzRadius, 0, xzRadius).stretch(0, height, 0);
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