package com.simpletweaks.block.entity;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.block.ModBlocks;
import com.simpletweaks.block.custom.SpawnTeleporterBlock;
import com.simpletweaks.config.SimpletweaksConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class SpawnTeleporterBlockEntity extends BlockEntity {
    private UUID ownerUuid;

    // Runtime data (nicht gespeichert)
    private final Map<UUID, Integer> timeStanding = new HashMap<>();
    private final Map<UUID, Vec3d> lastPositions = new HashMap<>();

    public SpawnTeleporterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPAWN_TELEPORTER_BE, pos, state);
    }

    public void setOwner(UUID uuid) {
        this.ownerUuid = uuid;
        markDirty();
        if (world != null && !world.isClient()) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    // --- NEU: Getter für den Mixin ---
    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public boolean isOwner(PlayerEntity player) {
        return ownerUuid != null && ownerUuid.equals(player.getUuid());
    }

    // --- DATEN SPEICHERN (Server) ---
    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        if (ownerUuid != null) {
            view.put("Owner", Uuids.INT_STREAM_CODEC, ownerUuid);
        }
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        view.read("Owner", Uuids.INT_STREAM_CODEC).ifPresent(uuid -> this.ownerUuid = uuid);
    }

    // --- SYNC MIT CLIENT (Neu hinzugefügt!) ---

    // 1. Paket senden, wenn sich der Block ändert (z.B. beim Platzieren)
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    // 2. Daten bereitstellen, wenn der Chunk geladen wird
    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    // ------------------------------------------

    public static void tick(World world, BlockPos pos, BlockState state, SpawnTeleporterBlockEntity be) {
        if (world.isClient()) return;

        Box box = new Box(pos).offset(0, 0.5, 0).expand(0.1, 1.5, 0.1);
        List<ServerPlayerEntity> players = world.getEntitiesByClass(ServerPlayerEntity.class, box, p -> true);

        be.timeStanding.keySet().removeIf(uuid -> players.stream().noneMatch(p -> p.getUuid().equals(uuid)));
        be.lastPositions.keySet().removeIf(uuid -> players.stream().noneMatch(p -> p.getUuid().equals(uuid)));

        for (ServerPlayerEntity player : players) {
            UUID id = player.getUuid();
            Vec3d currentPos = player.getEntityPos();
            Vec3d lastPos = be.lastPositions.get(id);

            boolean moved = lastPos != null && lastPos.squaredDistanceTo(currentPos) > 0.0001;

            if (moved) {
                be.timeStanding.put(id, 0);
                player.sendMessage(Text.literal("Teleport cancelled (moved)").formatted(Formatting.RED), true);
            } else {
                int ticks = be.timeStanding.getOrDefault(id, 0) + 1;
                be.timeStanding.put(id, ticks);

                // FEATURE 3: Partikel beim Warten
                if (world instanceof ServerWorld serverWorld) {
                    serverWorld.spawnParticles(ParticleTypes.PORTAL,
                            player.getX(), player.getY() + 1.0, player.getZ(),
                            2, 0.3, 0.5, 0.3, 0.1);
                }

                // --- Riser Sound (Pitch erhöht sich) ---
                if (ticks % 5 == 0 && ticks < 100) {
                    float pitch = 0.1f + (ticks / 100f) * 1.2f;
                    // 0.05 bis 3 volunme:
                    float volume = 0.05f + (ticks / 100f) * 0.95f;
                    if (ticks % 10 == 0) world.playSound(null, pos, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.BLOCKS, 0.3f*volume, pitch);
                    world.playSound(null, pos, SoundEvents.ENTITY_ZOMBIE_STEP, SoundCategory.BLOCKS, 0.20f*volume, pitch);
                    world.playSound(null, pos, SoundEvents.ENTITY_ENDERMITE_STEP, SoundCategory.BLOCKS, 0.10f*volume, pitch);
                    world.playSound(null, pos, SoundEvents.ENTITY_SILVERFISH_STEP, SoundCategory.BLOCKS, 0.02f*volume, pitch);
                }

                if (ticks % 20 == 0 && ticks < 100) {
                    player.sendMessage(Text.literal("Teleporting in " + (5 - (ticks / 20)) + "...").formatted(Formatting.BLUE), true);
                }

                if (ticks >= 100) {
                    be.timeStanding.put(id, 0);
                    teleportToSpawn(world, player, state);
                }
            }
            be.lastPositions.put(id, currentPos);
        }
    }

    private static void teleportToSpawn(World world, ServerPlayerEntity player, BlockState state) {
        if (!(world instanceof ServerWorld serverWorld)) return;

        // Tier ermitteln
        int tier = 1;
        if (state.getBlock() instanceof SpawnTeleporterBlock teleBlock) {
            tier = teleBlock.getTier();
        }

        // Ziel ermitteln
        ServerWorld overworld = serverWorld.getServer().getOverworld();
        BlockPos targetPos = getTargetForTier(tier, overworld);

        // Sound am Startort
        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1f, 1f);

        // --- Teleport ---
        // Y + 2 Blöcke für Slow Falling Effekt
        player.teleport(overworld, targetPos.getX() + 0.5, targetPos.getY() + 2.0, targetPos.getZ() + 0.5, Set.of(), 0f, 0f, false);

        // --- Post-Teleport Effekte ---
        // Particles
        overworld.spawnParticles(ParticleTypes.END_ROD, targetPos.getX() + 0.5, targetPos.getY()+ 2, targetPos.getZ() + 0.5, 50, 0.2, 0.1, 0.2, 0.05);
        overworld.spawnParticles(ParticleTypes.SOUL, targetPos.getX() + 0.5, targetPos.getY() + 1, targetPos.getZ() + 0.5, 40, 0.5, 1, 0.5, 0.1);
        overworld.spawnParticles(ParticleTypes.SCULK_SOUL, targetPos.getX() + 0.5, targetPos.getY() + 2, targetPos.getZ() + 0.5, 30, 0.2, 0.1, 0.2, 0.05);
        overworld.spawnParticles(ParticleTypes.PORTAL, targetPos.getX() + 0.5, targetPos.getY() + 2, targetPos.getZ() + 0.5, 20, 0.2, 0.1, 0.2, 0.02);
        overworld.spawnParticles(ParticleTypes.SCULK_CHARGE_POP, targetPos.getX() + 0.5, targetPos.getY() + 2, targetPos.getZ() + 0.5, 15, 0.2, 0.1, 0.2, 0.05);
        overworld.spawnParticles(ParticleTypes.EXPLOSION, targetPos.getX() + 0.5, targetPos.getY(), targetPos.getZ() + 2, 10, 0.1, 0.1, 0.2, 0.2);

        // Status Effects
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 20, 0, false, false, false)); // 1s
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 60, 0, false, false, false)); // 3s
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 40, 0, false, false, false)); // 2s (sanft runter)

        // Sound am Zielort
        overworld.playSound(null, targetPos, SoundEvents.BLOCK_BEACON_ACTIVATE, SoundCategory.PLAYERS, 1.0f, 1.5f);
        overworld.playSound(null, targetPos, SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, SoundCategory.PLAYERS, 1.5f, 0.5f);
        player.sendMessage(Text.literal("Welcome to Spawn " + tier + "!").formatted(Formatting.GOLD, Formatting.BOLD), true);
    }

    private static BlockPos getTargetForTier(int tier, ServerWorld world) {
        SimpletweaksConfig.Spawn config = Simpletweaks.getConfig().spawn;
        BlockPos customPos = null;

        switch (tier) {
            case 2 -> { if (config.spawn2Y > -999) customPos = new BlockPos(config.spawn2X, config.spawn2Y, config.spawn2Z); }
            case 3 -> { if (config.spawn3Y > -999) customPos = new BlockPos(config.spawn3X, config.spawn3Y, config.spawn3Z); }
            case 4 -> { if (config.spawn4Y > -999) customPos = new BlockPos(config.spawn4X, config.spawn4Y, config.spawn4Z); }
            default -> { if (config.spawn1Y > -999) customPos = new BlockPos(config.spawn1X, config.spawn1Y, config.spawn1Z); }
        }

        // Fallback: World Spawn
        if (customPos == null) {
            return world.getSpawnPoint().getPos();
        }
        return customPos;
    }
}