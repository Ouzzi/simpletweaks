package com.simpletweaks.block.entity;

import com.simpletweaks.block.custom.LaunchpadBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
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
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class LaunchpadBlockEntity extends BlockEntity {
    private int charges = 0;
    private int chargeTimer = 0;

    private UUID ownerUuid;

    public LaunchpadBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LAUNCHPAD_BE, pos, state);
    }

    // --- Owner Logic ---
    public void setOwner(UUID uuid) {
        this.ownerUuid = uuid;
        markDirty();
    }

    public boolean isOwner(PlayerEntity player) {
        return ownerUuid != null && ownerUuid.equals(player.getUuid());
    }

    // --- Charges Logic ---
    public int getCharges() { return charges; }

    public void addCharge() {
        if (this.charges < 16) {
            this.charges++;
            this.chargeTimer = 0;
            markDirty();
            if (world != null) world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    private List<PlayerEntity> cachedPlayers = List.of(); // Cache Liste
    public static void tick(World world, BlockPos pos, BlockState state, LaunchpadBlockEntity be) {
        boolean isClient = world.isClient();

        if (world.getTime() % 10 == 0) {
            Box detectionBox = new Box(pos).offset(0, 0.1, 0).expand(0.0, 0.5, 0.0);
            be.cachedPlayers = world.getEntitiesByClass(PlayerEntity.class, detectionBox, p -> true);
        }

        // Bereich prüfen
        Box detectionBox = new Box(pos).offset(0, 0.1, 0).expand(0.0, 0.5, 0.0);
        List<PlayerEntity> players = world.getEntitiesByClass(PlayerEntity.class, detectionBox, p -> true);

        // --- 1. IDLE PARTIKEL (Wenn niemand draufsteht & geladen) ---
        if (be.cachedPlayers.isEmpty() && be.charges > 0 && isClient) {
            // Alle paar Ticks ein kleiner Windstoß
            if (world.getRandom().nextInt(30) == 0) {
                world.addParticleClient(ParticleTypes.SMALL_GUST,
                        pos.getX() + 0.5 + (world.getRandom().nextDouble() - 0.5) * 0.4,
                        pos.getY() + 0.2,
                        pos.getZ() + 0.5 + (world.getRandom().nextDouble() - 0.5) * 0.4,
                        0, 0.02, 0);
            }
        }

        if (!be.cachedPlayers.isEmpty()) {
            PlayerEntity player = be.cachedPlayers.get(0);
            if (!player.isAlive()) return;

            if (be.charges > 0) {
                be.chargeTimer++;

                if (be.chargeTimer < 60) {
                    if (isClient) {
                        // Frequenz hängt von Ladungen ab
                        int frequency = Math.max(1, 10 - (be.charges / 2));

                        if (be.chargeTimer % frequency == 0) {
                            // Partikel-Spirale
                            double speed = 0.2 + (be.charges * 0.02);
                            double angle = (be.chargeTimer * speed);
                            double radius = 0.6;

                            double px = pos.getX() + 0.5 + Math.cos(angle) * radius;
                            double pz = pos.getZ() + 0.5 + Math.sin(angle) * radius;

                            world.addParticleClient(ParticleTypes.GUST,
                                    px, pos.getY() + 0.2 + (be.chargeTimer * 0.01), pz,
                                    0, 0.05, 0);
                        }
                    } else {
                        // Sound und Chat Feedback
                        if (be.chargeTimer % 20 == 0) {
                            // Pitch wird höher je voller es ist
                            float pitch = 0.8f + (be.charges / 40f) + (be.chargeTimer / 60f);
                            world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HAT.value(), SoundCategory.BLOCKS, 0.5f, pitch);

                            // Info an Spieler
                            player.sendMessage(Text.literal("Power: " + be.charges + " | Launch in " + (3 - (be.chargeTimer / 20))).formatted(Formatting.AQUA), true);
                        }
                    }
                }

                if (be.chargeTimer >= 60) {
                    if (!isClient) {
                        double strength = 1.5 + (be.charges * 0.4);

                        launchPlayer((ServerPlayerEntity) player, strength);

                        be.charges = 0;
                        be.chargeTimer = 0;
                        be.markDirty();
                        be.updateListeners();
                    }
                }
            } else {
                if (be.chargeTimer == 0 && !isClient && world.getTime() % 40 == 0) {
                    player.sendMessage(Text.literal("Right-click with Wind Charges.").formatted(Formatting.RED), true);
                }
            }
        } else {
            if (be.chargeTimer > 0) {
                be.chargeTimer = 0;
            }
        }
    }

    private static void launchPlayer(ServerPlayerEntity player, double strength) {
        player.setVelocity(player.getVelocity().add(0, strength, 0));

        player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player));

        ServerWorld serverWorld = (ServerWorld) player.getEntityWorld();

        int particleCount = (int) (strength * 5);

        serverWorld.spawnParticles(ParticleTypes.EXPLOSION_EMITTER,
                player.getX(), player.getY(), player.getZ(),
                particleCount, 0, 0, 0, 0);

        serverWorld.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_BREEZE_WIND_BURST.value(), SoundCategory.PLAYERS, 2.0f, 1.0f);
    }

    private void updateListeners() {
        if (world != null && !world.isClient()) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putInt("Charges", charges);
        if (ownerUuid != null) {view.put("Owner", Uuids.INT_STREAM_CODEC, ownerUuid);}
    }

    // --- FIX: readData mit Codec ---
    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        this.charges = view.getInt("Charges", 0);
        view.read("Owner", Uuids.INT_STREAM_CODEC).ifPresent(uuid -> this.ownerUuid = uuid);
    }

    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }
}