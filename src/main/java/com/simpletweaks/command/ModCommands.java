package com.simpletweaks.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.simpletweaks.Simpletweaks;
import com.simpletweaks.config.SimpletweaksConfig;
import com.simpletweaks.world.ClaimState;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.vehicle.*;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ModCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            // --- CLAIM COMMANDS ---
            dispatcher.register(CommandManager.literal("claim")
                    // Info über aktuellen Chunk
                    .executes(context -> {
                        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                        ChunkPos pos = player.getChunkPos();

                        // KORREKTUR: getEntityWorld() verwenden (gibt ServerWorld zurück)
                        ClaimState state = ClaimState.get(player.getEntityWorld());

                        UUID owner = state.getOwner(pos);
                        if (owner == null) {
                            context.getSource().sendFeedback(() -> Text.literal("This chunk is wild.").formatted(Formatting.GRAY), false);
                        } else {
                            // Namen auflösen (Online Spieler oder Fallback UUID)
                            String ownerName = resolveName(context.getSource().getServer(), owner);
                            context.getSource().sendFeedback(() -> Text.literal("Chunk owned by: " + ownerName).formatted(Formatting.GOLD), false);
                        }
                        return 1;
                    })
                    // Trust Player
                    .then(CommandManager.literal("trust")
                            .then(CommandManager.argument("player", StringArgumentType.word())
                                    .executes(context -> {
                                        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                        String targetName = StringArgumentType.getString(context, "player");

                                        // Nur Online-Spieler suchen
                                        Optional<GameProfile> profileOpt = resolveProfile(context.getSource().getServer(), targetName);

                                        if (profileOpt.isPresent()) {
                                            GameProfile profile = profileOpt.get();
                                            ChunkPos pos = player.getChunkPos();
                                            ClaimState state = ClaimState.get(player.getEntityWorld());

                                            // getId() statt id()
                                            if (state.addFriend(pos, player.getUuid(), profile.id())) {
                                                context.getSource().sendFeedback(() -> Text.literal("Added " + targetName + " to this chunk.").formatted(Formatting.GREEN), false);
                                            } else {
                                                context.getSource().sendError(Text.literal("You don't own this chunk!"));
                                            }
                                        } else {
                                            context.getSource().sendError(Text.literal("Player not found (must be online)."));
                                        }

                                        return 1;
                                    })
                            )
                    )
                    // Untrust Player
                    .then(CommandManager.literal("untrust")
                            .then(CommandManager.argument("player", StringArgumentType.word())
                                    .executes(context -> {
                                        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                        String targetName = StringArgumentType.getString(context, "player");

                                        Optional<GameProfile> profileOpt = resolveProfile(context.getSource().getServer(), targetName);

                                        if (profileOpt.isPresent()) {
                                            GameProfile profile = profileOpt.get();
                                            ChunkPos pos = player.getChunkPos();
                                            ClaimState state = ClaimState.get(player.getEntityWorld());

                                            if (state.removeFriend(pos, player.getUuid(), profile.id())) {
                                                context.getSource().sendFeedback(() -> Text.literal("Removed " + targetName + " from this chunk.").formatted(Formatting.RED), false);
                                            } else {
                                                context.getSource().sendError(Text.literal("You don't own this chunk or player was not trusted."));
                                            }
                                        } else {
                                            // Fallback: Wenn Spieler offline ist, können wir ihn hier schwer entfernen ohne Cache.
                                            // Optional: Man könnte eine Logik bauen, die nur den Namen im State speichert, aber UUID ist sicherer.
                                            context.getSource().sendError(Text.literal("Player not found (must be online to remove via command)."));
                                        }

                                        return 1;
                                    })
                            )
                    )
                    .then(CommandManager.literal("admin")
                            .requires(source -> checkPermission(source, 4))
                            .then(CommandManager.literal("unclaim")
                                    .executes(context -> {
                                        ServerPlayerEntity player = context.getSource().getPlayerOrThrow();
                                        ChunkPos pos = player.getChunkPos();
                                        ClaimState state = ClaimState.get(player.getEntityWorld());

                                        if (state.isClaimed(pos)) {
                                            state.unclaim(pos, player.getUuid(), true);
                                            context.getSource().sendFeedback(() -> Text.literal("Chunk force-unclaimed by admin.").formatted(Formatting.RED, Formatting.BOLD), true);
                                        } else {
                                            context.getSource().sendError(Text.literal("This chunk is not claimed."));
                                        }
                                        return 1;
                                    })
                            )
                            .then(CommandManager.literal("listall")
                                    .executes(context -> {
                                        ServerCommandSource source = context.getSource();
                                        ClaimState state = ClaimState.get(source.getWorld());
                                        Map<ChunkPos, UUID> allClaims = state.getAllClaims();

                                        if (allClaims.isEmpty()) {
                                            source.sendFeedback(() -> Text.literal("No chunks claimed yet.").formatted(Formatting.YELLOW), false);
                                            return 1;
                                        }

                                        source.sendFeedback(() -> Text.literal("--- All Claimed Chunks (" + allClaims.size() + ") ---").formatted(Formatting.GOLD, Formatting.BOLD), false);

                                        // Auflisten (max 50, um Chat Spam zu verhindern)
                                        int count = 0;
                                        for (Map.Entry<ChunkPos, UUID> entry : allClaims.entrySet()) {
                                            if (count >= 50) {
                                                source.sendFeedback(() -> Text.literal("... and more.").formatted(Formatting.GRAY), false);
                                                break;
                                            }
                                            ChunkPos p = entry.getKey();
                                            String name = resolveName(source.getServer(), entry.getValue());

                                            source.sendFeedback(() -> Text.literal(" - [" + p.x + ", " + p.z + "] : " + name)
                                                    .formatted(Formatting.YELLOW)
                                                    .styled(style -> style.withColor(Formatting.YELLOW)), false);
                                            count++;
                                        }
                                        return 1;
                                    })
                            )
                            // 3. List (Alle Claims eines Spielers)
                            .then(CommandManager.literal("list")
                                    .then(CommandManager.argument("target", StringArgumentType.word())
                                            .executes(context -> {
                                                String targetName = StringArgumentType.getString(context, "target");
                                                Optional<GameProfile> profileOpt = resolveProfile(context.getSource().getServer(), targetName);

                                                if (profileOpt.isPresent()) {
                                                    UUID targetId = profileOpt.get().id();
                                                    ClaimState state = ClaimState.get(context.getSource().getWorld());
                                                    List<ChunkPos> claims = state.getClaimsByPlayer(targetId);

                                                    context.getSource().sendFeedback(() -> Text.literal("Claims for " + targetName + ": " + claims.size()).formatted(Formatting.GOLD), false);

                                                    for (int i = 0; i < Math.min(claims.size(), 10); i++) {
                                                        ChunkPos p = claims.get(i);
                                                        context.getSource().sendFeedback(() -> Text.literal("- [" + p.x + ", " + p.z + "]").formatted(Formatting.YELLOW), false);
                                                    }
                                                    if (claims.size() > 10) {
                                                        context.getSource().sendFeedback(() -> Text.literal("... and " + (claims.size() - 10) + " more.").formatted(Formatting.GRAY), false);
                                                    }

                                                } else {
                                                    context.getSource().sendError(Text.literal("Player not found online."));
                                                }
                                                return 1;
                                            })
                                    )
                            )
                    )
            );

            // --- KILL BOATS ---
            dispatcher.register(CommandManager.literal("killboats")
                    .requires(source -> checkPermission(source, 2))
                    .executes(context -> executeKillBoats(context, "standard"))
                    .then(CommandManager.argument("mode", StringArgumentType.word())
                            .suggests((context, builder) -> CommandSource.suggestMatching(new String[]{"standard", "empty", "all"}, builder))
                            .executes(context -> executeKillBoats(context, StringArgumentType.getString(context, "mode")))
                    ));

            // --- KILL CARTS ---
            dispatcher.register(CommandManager.literal("killcarts")
                    .requires(source -> checkPermission(source, 2))
                    .executes(context -> executeKillCarts(context, "standard"))
                    .then(CommandManager.argument("mode", StringArgumentType.word())
                            .suggests((context, builder) -> CommandSource.suggestMatching(new String[]{"standard", "empty", "all"}, builder))
                            .executes(context -> executeKillCarts(context, StringArgumentType.getString(context, "mode")))
                    ));

            // --- CONFIG COMMANDS (simpletweaks) ---
            dispatcher.register(CommandManager.literal("simpletweaks")
                    // Level 4 für Admin-Befehle (Config Änderungen)
                    .requires(source -> checkPermission(source, 4))

                    // 1. Balancing
                    .then(CommandManager.literal("balancing")
                            .then(CommandManager.literal("rocketStackSize")
                                    .then(CommandManager.argument("size", IntegerArgumentType.integer(1, 64))
                                            .executes(ctx -> {
                                                int val = IntegerArgumentType.getInteger(ctx, "size");
                                                Simpletweaks.getConfig().balancing.rocketStackSize = val;
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Rocket Stack Size set to: " + val), true);
                                                return 1;
                                            })))
                    )
                    // 4. Dimensions
                    .then(CommandManager.literal("dimension")
                            .then(CommandManager.literal("nether")
                                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                Simpletweaks.getConfig().dimensions.allowNether = val;
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Nether enabled: " + val), true);
                                                return 1;
                                            })))
                            .then(CommandManager.literal("end")
                                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                Simpletweaks.getConfig().dimensions.allowEnd = val;
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("End enabled: " + val), true);
                                                return 1;
                                            }))))

                    // 5. Spawn (Elytra + Teleporter)
                    .then(CommandManager.literal("spawn")
                            // NEU: Teleporter Count
                            .then(CommandManager.literal("teleporterCount")
                                    .then(CommandManager.argument("count", IntegerArgumentType.integer(0, 64))
                                            .executes(ctx -> {
                                                int val = IntegerArgumentType.getInteger(ctx, "count");
                                                Simpletweaks.getConfig().spawn.spawnTeleporterCount = val;
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Spawn Teleporter Count set to: " + val), true);
                                                return 1;
                                            })))
                            .then(CommandManager.literal("elytra")
                                    // Toggle An/Aus
                                    .then(CommandManager.literal("toggle")
                                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                                    .executes(ctx -> {
                                                        boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                        Simpletweaks.getConfig().spawn.giveElytraOnSpawn = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("Spawn Elytra enabled: " + val), true);
                                                        return 1;
                                                    })))
                                    // Radius setzen
                                    .then(CommandManager.literal("radius")
                                            .then(CommandManager.argument("blocks", IntegerArgumentType.integer(1))
                                                    .executes(ctx -> {
                                                        int val = IntegerArgumentType.getInteger(ctx, "blocks");
                                                        Simpletweaks.getConfig().spawn.spawnElytraRadius = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("Elytra Radius set to: " + val), true);
                                                        return 1;
                                                    })))
                                    // Flugzeit
                                    .then(CommandManager.literal("flightTime")
                                            .then(CommandManager.argument("seconds", IntegerArgumentType.integer(1))
                                                    .executes(ctx -> {
                                                        int val = IntegerArgumentType.getInteger(ctx, "seconds");
                                                        Simpletweaks.getConfig().spawn.flightTimeSeconds = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("Flight Time set to: " + val + " seconds"), true);
                                                        return 1;
                                                    })))
                                    // Boost Anzahl
                                    .then(CommandManager.literal("maxBoosts")
                                            .then(CommandManager.argument("amount", IntegerArgumentType.integer(1))
                                                    .executes(ctx -> {
                                                        int val = IntegerArgumentType.getInteger(ctx, "amount");
                                                        Simpletweaks.getConfig().spawn.maxBoosts = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("Max Boosts set to: " + val), true);
                                                        return 1;
                                                    })))
                                    // Boost Stärke
                                    .then(CommandManager.literal("boostStrength")
                                            .then(CommandManager.argument("strength", FloatArgumentType.floatArg(0.1f))
                                                    .executes(ctx -> {
                                                        float val = FloatArgumentType.getFloat(ctx, "strength");
                                                        Simpletweaks.getConfig().spawn.boostStrength = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("Boost Strength set to: " + val), true);
                                                        return 1;
                                                    })))
                                    // Mittelpunkt konfigurieren
                                    .then(CommandManager.literal("center")
                                            .then(CommandManager.literal("worldspawn").executes(ctx -> {
                                                Simpletweaks.getConfig().spawn.useWorldSpawnAsCenter = true; saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Elytra Spawn Center set to World Spawn"), true); return 1;
                                            }))
                                            .then(CommandManager.literal("set")
                                                    .then(CommandManager.argument("x", IntegerArgumentType.integer())
                                                            .then(CommandManager.argument("z", IntegerArgumentType.integer())
                                                                    .executes(ctx -> {
                                                                        int x = IntegerArgumentType.getInteger(ctx, "x");
                                                                        int z = IntegerArgumentType.getInteger(ctx, "z");
                                                                        Simpletweaks.getConfig().spawn.useWorldSpawnAsCenter = false;
                                                                        Simpletweaks.getConfig().spawn.customSpawnElytraX = x;
                                                                        Simpletweaks.getConfig().spawn.customSpawnElytraZ = z;
                                                                        saveConfig();
                                                                        ctx.getSource().sendFeedback(() -> Text.literal("Elytra Center set to X:" + x + " Z:" + z), true);
                                                                        return 1;
                                                                    }))))
                                            // auf aktuelle Spielerposition setzen
                                            .then(CommandManager.literal("here").executes(ctx -> {
                                                BlockPos pos = ctx.getSource().getEntityOrThrow().getBlockPos();
                                                Simpletweaks.getConfig().spawn.useWorldSpawnAsCenter = false;
                                                Simpletweaks.getConfig().spawn.customSpawnElytraX = pos.getX();
                                                Simpletweaks.getConfig().spawn.customSpawnElytraZ = pos.getZ();
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Elytra Center set to your position (X:" + pos.getX() + " Z:" + pos.getZ() + ")"), true);
                                                return 1;
                                            }))
                                    )
                            )
                    )

                    // 6. World Spawn Settings
                    .then(CommandManager.literal("worldspawn")
                            .then(CommandManager.literal("setspawn1")
                                    .executes(ctx -> setTeleporterSpawn(ctx, 1)))
                            .then(CommandManager.literal("setspawn2")
                                    .executes(ctx -> setTeleporterSpawn(ctx, 2)))
                            .then(CommandManager.literal("setspawn3")
                                    .executes(ctx -> setTeleporterSpawn(ctx, 3)))
                            .then(CommandManager.literal("setspawn4")
                                    .executes(ctx -> setTeleporterSpawn(ctx, 4)))

                            .then(CommandManager.literal("forceExact")
                                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                Simpletweaks.getConfig().spawn.forceExactSpawn = val;
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Force Exact Spawn enabled: " + val), true);
                                                return 1;
                                            })))
                            .then(CommandManager.literal("set")
                                    .then(CommandManager.argument("x", IntegerArgumentType.integer())
                                            .then(CommandManager.argument("y", IntegerArgumentType.integer(-1, 320))
                                                    .then(CommandManager.argument("z", IntegerArgumentType.integer())
                                                            .executes(ctx -> {
                                                                int x = IntegerArgumentType.getInteger(ctx, "x");
                                                                int y = IntegerArgumentType.getInteger(ctx, "y");
                                                                int z = IntegerArgumentType.getInteger(ctx, "z");
                                                                Simpletweaks.getConfig().spawn.xCoordSpawnPoint = x;
                                                                Simpletweaks.getConfig().spawn.yCoordSpawnPoint = y;
                                                                Simpletweaks.getConfig().spawn.zCoordSpawnPoint = z;
                                                                saveConfig();
                                                                ctx.getSource().sendFeedback(() -> Text.literal("Custom World Spawn set to X:" + x + " Y:" + y + " Z:" + z), true);
                                                                return 1;
                                                            })))))
                            .then(CommandManager.literal("here").executes(ctx -> {
                                BlockPos pos = ctx.getSource().getEntityOrThrow().getBlockPos();
                                Simpletweaks.getConfig().spawn.xCoordSpawnPoint = pos.getX();
                                Simpletweaks.getConfig().spawn.yCoordSpawnPoint = pos.getY();
                                Simpletweaks.getConfig().spawn.zCoordSpawnPoint = pos.getZ();
                                saveConfig();
                                ctx.getSource().sendFeedback(() -> Text.literal("Custom World Spawn set to your position (X:" + pos.getX() + " Y:" + pos.getY() + " Z:" + pos.getZ() + ")"), true);
                                return 1;
                            }))
                    )

                    // 7. Command Settings
                    .then(CommandManager.literal("commands")
                            .then(CommandManager.literal("enableKillBoats")
                                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                Simpletweaks.getConfig().commands.enableKillBoatsCommand = val;
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("/killboats enabled: " + val), true);
                                                return 1;
                                            })))
                            .then(CommandManager.literal("enableKillCarts")
                                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                Simpletweaks.getConfig().commands.enableKillCartsCommand = val;
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("/killcarts enabled: " + val), true);
                                                return 1;
                                            })))
                    )

            );
        });
    }

    // --- HELPER ---

    private static String resolveName(MinecraftServer server, UUID uuid) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
        if (player != null) return player.getName().getString();
        return uuid.toString();
    }

    private static Optional<GameProfile> resolveProfile(MinecraftServer server, String name) {
        ServerPlayerEntity player = server.getPlayerManager().getPlayer(name);
        if (player != null) return Optional.of(player.getGameProfile());
        return Optional.empty();
    }

    // --- EXECUTION LOGIC ---

    private static int executeKillBoats(CommandContext<ServerCommandSource> context, String mode) {
        if (!Simpletweaks.getConfig().commands.enableKillBoatsCommand) {
            context.getSource().sendError(Text.literal("Command is disabled in config."));
            return 0;
        }

        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity player)) {
            context.getSource().sendError(Text.literal("Only players can use this command."));
            return 0;
        }

        Box box = player.getBoundingBox().expand(100);
        List<AbstractBoatEntity> boats = player.getEntityWorld().getEntitiesByClass(AbstractBoatEntity.class, box, boat -> !boat.hasPassengers());

        int count = 0;
        for (AbstractBoatEntity boat : boats) {
            boolean isStorage = boat instanceof AbstractChestBoatEntity;

            boolean shouldRemove = false;
            switch (mode) {
                case "standard":
                    if (!isStorage) shouldRemove = true;
                    break;
                case "empty":
                    if (!isStorage) {
                        shouldRemove = true;
                    } else {
                        if (((Inventory) boat).isEmpty()) shouldRemove = true;
                    }
                    break;
                case "all":
                    shouldRemove = true;
                    break;
                default:
                    if (!isStorage) shouldRemove = true;
                    break;
            }

            if (shouldRemove) {
                boat.discard();
                count++;
            }
        }

        final int finalCount = count;
        context.getSource().sendFeedback(() -> Text.translatable("commands.simpletweaks.killboats.success", finalCount)
                .append(Text.literal(" (" + mode + ")")), true);
        return count;
    }

    private static int executeKillCarts(CommandContext<ServerCommandSource> context, String mode) {
        if (!Simpletweaks.getConfig().commands.enableKillCartsCommand) {
            context.getSource().sendError(Text.literal("Command is disabled in config."));
            return 0;
        }

        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity player)) {
            context.getSource().sendError(Text.literal("Only players can use this command."));
            return 0;
        }

        Box box = player.getBoundingBox().expand(100);
        List<AbstractMinecartEntity> carts = player.getEntityWorld().getEntitiesByClass(AbstractMinecartEntity.class, box, cart -> !cart.hasPassengers());

        int count = 0;
        for (AbstractMinecartEntity cart : carts) {
            boolean isStandard = cart instanceof MinecartEntity;
            boolean isStorage = cart instanceof StorageMinecartEntity;

            boolean shouldRemove = false;
            switch (mode) {
                case "standard":
                    if (isStandard) shouldRemove = true;
                    break;
                case "empty":
                    if (isStandard) {
                        shouldRemove = true;
                    } else if (isStorage) {
                        if (((StorageMinecartEntity) cart).isEmpty()) shouldRemove = true;
                    }
                    break;
                case "all":
                    shouldRemove = true;
                    break;
                default:
                    if (isStandard) shouldRemove = true;
                    break;
            }

            if (shouldRemove) {
                cart.discard();
                count++;
            }
        }

        final int finalCount = count;
        context.getSource().sendFeedback(() -> Text.literal("Removed " + finalCount + " minecarts (" + mode + ")."), true);
        return count;
    }

    private static boolean checkRemove(String mode, boolean isStorage, Inventory inv) {
        switch (mode) {
            case "standard": return !isStorage;
            case "empty": return !isStorage || (inv != null && inv.isEmpty());
            case "all": return true;
            default: return !isStorage;
        }
    }

    private static void saveConfig() {
        AutoConfig.getConfigHolder(SimpletweaksConfig.class).save();
    }

    private static boolean checkPermission(ServerCommandSource source, int level) {
        if (source.getEntity() instanceof ServerPlayerEntity player) {
            return source.getServer().getPlayerManager().isOperator(player.getPlayerConfigEntry());
        }
        return true;
    }

    private static int setTeleporterSpawn(CommandContext<ServerCommandSource> ctx, int tier) {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        if (player == null) return 0;
        BlockPos pos = player.getBlockPos();
        SimpletweaksConfig config = Simpletweaks.getConfig();

        switch (tier) {
            case 1 -> { config.spawn.spawn1X = pos.getX(); config.spawn.spawn1Y = pos.getY(); config.spawn.spawn1Z = pos.getZ(); }
            case 2 -> { config.spawn.spawn2X = pos.getX(); config.spawn.spawn2Y = pos.getY(); config.spawn.spawn2Z = pos.getZ(); }
            case 3 -> { config.spawn.spawn3X = pos.getX(); config.spawn.spawn3Y = pos.getY(); config.spawn.spawn3Z = pos.getZ(); }
            case 4 -> { config.spawn.spawn4X = pos.getX(); config.spawn.spawn4Y = pos.getY(); config.spawn.spawn4Z = pos.getZ(); }
        }
        Simpletweaks.saveConfig();
        ctx.getSource().sendFeedback(() -> Text.literal("Teleporter Spawn " + tier + " set to " + pos.toShortString()).formatted(Formatting.GREEN), true);
        return 1;
    }
}