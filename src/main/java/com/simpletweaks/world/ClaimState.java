package com.simpletweaks.world;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.command.permission.PermissionLevel;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.*;
import java.util.stream.Collectors;

public class ClaimState extends PersistentState {

    // --- CODEC DEFINITION (MUSS OBEN STEHEN!) ---
    private static final Codec<UUID> UUID_CODEC = Codec.STRING.xmap(UUID::fromString, UUID::toString);

    public static final Codec<ClaimData> DATA_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUID_CODEC.fieldOf("Owner").forGetter(d -> d.owner),
            UUID_CODEC.listOf().optionalFieldOf("Whitelist", List.of()).forGetter(d -> new ArrayList<>(d.whitelist))
    ).apply(instance, (owner, whitelist) -> {
        ClaimData data = new ClaimData(owner);
        data.whitelist.addAll(whitelist);
        return data;
    }));

    private static Codec<ClaimState> getCodec() {
        return Codec.unboundedMap(Codec.LONG, DATA_CODEC).xmap(
                ClaimState::new,
                state -> state.claims
        );
    }

    // --- TYPE DEFINITION (DARF ERST HIER STEHEN) ---
    private static final PersistentStateType<ClaimState> TYPE = new PersistentStateType<>(
            "simpletweaks_claims",
            ClaimState::new,
            getCodec(),
            DataFixTypes.LEVEL
    );

    // --- DATEN ---
    private final Map<Long, ClaimData> claims = new HashMap<>();

    public ClaimState() {}

    public ClaimState(Map<Long, ClaimData> loadedClaims) {
        this.claims.putAll(loadedClaims);
    }

    public static ClaimState get(ServerWorld world) {
        // Nutze die sichere Factory-Methode
        return world.getPersistentStateManager().getOrCreate(TYPE);
    }

    // --- LOGIK ---

    public boolean isClaimed(ChunkPos pos) {
        return claims.containsKey(pos.toLong());
    }

    public boolean claim(ChunkPos pos, UUID owner) {
        if (isClaimed(pos)) return false;
        claims.put(pos.toLong(), new ClaimData(owner));
        markDirty();
        return true;
    }

    public boolean unclaim(ChunkPos pos, UUID requestor, boolean isAdmin) {
        long key = pos.toLong();
        if (!claims.containsKey(key)) return false;

        // Erlaube wenn Owner ODER Admin
        if (isAdmin || claims.get(key).owner.equals(requestor)) {
            claims.remove(key);
            markDirty();
            return true;
        }
        return false;
    }

    public boolean canInteract(ChunkPos pos, ServerPlayerEntity player) {
        long key = pos.toLong();
        if (!claims.containsKey(key)) return true;

        ClaimData data = claims.get(key);
        if (data.owner.equals(player.getUuid())) return true;

        // Admin-Check (OP Level 2+)
        boolean isOp = false;
        if (player.getEntityWorld().getServer() != null) {
            PermissionLevel level = player.getEntityWorld().getServer()
                    .getPermissionLevel(player.getPlayerConfigEntry())
                    .getLevel();
            // OP Level 2 (GAMEMASTERS) oder höher darf alles
            isOp = level.ordinal() >= PermissionLevel.GAMEMASTERS.ordinal();
        }

        return data.whitelist.contains(player.getUuid()) || isOp;
    }

    public boolean addFriend(ChunkPos pos, UUID owner, UUID friend) {
        long key = pos.toLong();
        if (claims.containsKey(key) && claims.get(key).owner.equals(owner)) {
            claims.get(key).whitelist.add(friend);
            markDirty();
            return true;
        }
        return false;
    }

    public boolean removeFriend(ChunkPos pos, UUID owner, UUID friend) {
        long key = pos.toLong();
        if (claims.containsKey(key) && claims.get(key).owner.equals(owner)) {
            claims.get(key).whitelist.remove(friend);
            markDirty();
            return true;
        }
        return false;
    }

    public UUID getOwner(ChunkPos pos) {
        long key = pos.toLong();
        return claims.containsKey(key) ? claims.get(key).owner : null;
    }

    // --- NEU: Getter für Whitelist (für das Item) ---
    public Collection<UUID> getWhitelist(ChunkPos pos) {
        long key = pos.toLong();
        if (claims.containsKey(key)) {
            return claims.get(key).whitelist;
        }
        return Collections.emptyList();
    }

    // --- ADMIN HELPERS ---

    // Gibt alle Claims eines Spielers zurück
    public List<ChunkPos> getClaimsByPlayer(UUID playerUuid) {
        return claims.entrySet().stream()
                .filter(entry -> entry.getValue().owner.equals(playerUuid))
                .map(entry -> new ChunkPos(entry.getKey()))
                .collect(Collectors.toList());
    }

    public static class ClaimData {
        UUID owner;
        Set<UUID> whitelist = new HashSet<>();

        public ClaimData(UUID owner) {
            this.owner = owner;
        }
    }
}