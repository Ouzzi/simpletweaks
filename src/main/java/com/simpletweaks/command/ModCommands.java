package com.simpletweaks.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.simpletweaks.Simpletweaks;
import com.simpletweaks.config.SimpletweaksConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.vehicle.*;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;

public class ModCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            // --- KILL BOATS ---
            dispatcher.register(CommandManager.literal("killboats")
                    // FIX: Use getPermissions().test(2) instead of hasPermission(2)
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
                    // Level 4 für Admin-Befehle
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

                    // 2. Vaults
                    .then(CommandManager.literal("vaults")
                            .then(CommandManager.literal("cooldown")
                                    .then(CommandManager.argument("days", IntegerArgumentType.integer(0))
                                            .executes(ctx -> {
                                                int val = IntegerArgumentType.getInteger(ctx, "days");
                                                Simpletweaks.getConfig().vaults.vaultCooldownDays = val;
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Vault Cooldown set to: " + val + " days"), true);
                                                return 1;
                                            })))
                    )

                    // 3. PvP
                    .then(CommandManager.literal("pvp")
                            .then(CommandManager.literal("headDrops")
                                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                Simpletweaks.getConfig().pvp.playerHeadDrops = val;
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Player Head Drops enabled: " + val), true);
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

                    // 5. Spawn Elytra
                    .then(CommandManager.literal("spawn")
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
                            .then(CommandManager.literal("forceExact")
                                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                Simpletweaks.getConfig().worldSpawn.forceExactSpawn = val;
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
                                        Simpletweaks.getConfig().worldSpawn.xCoordSpawnPoint = x;
                                        Simpletweaks.getConfig().worldSpawn.yCoordSpawnPoint = y;
                                        Simpletweaks.getConfig().worldSpawn.zCoordSpawnPoint = z;
                                        saveConfig();
                                        ctx.getSource().sendFeedback(() -> Text.literal("Custom World Spawn set to X:" + x + " Y:" + y + " Z:" + z), true);
                                        return 1;
                                    })))))
                            .then(CommandManager.literal("here").executes(ctx -> {
                                BlockPos pos = ctx.getSource().getEntityOrThrow().getBlockPos();
                                Simpletweaks.getConfig().worldSpawn.xCoordSpawnPoint = pos.getX();
                                Simpletweaks.getConfig().worldSpawn.yCoordSpawnPoint = pos.getY();
                                Simpletweaks.getConfig().worldSpawn.zCoordSpawnPoint = pos.getZ();
                                saveConfig();
                                ctx.getSource().sendFeedback(() -> Text.literal("Custom World Spawn set to your position (X:" + pos.getX() + " Y:" + pos.getY() + " Z:" + pos.getZ() + ")"), true);
                                return 1;
                            }))
                    )

                    // 7. Command Settings (NEU)
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

                    // 8. Tweaks (Erweitert)
                    .then(CommandManager.literal("tweaks")
                            // AutoWalk
                            .then(CommandManager.literal("autowalk")
                                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                Simpletweaks.getConfig().tweaks.enableAutowalk = val;
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Auto-Walk enabled: " + val), true);
                                                return 1;
                                            })))
                            // Yeet
                            .then(CommandManager.literal("yeet")
                                    .then(CommandManager.literal("toggle")
                                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                                    .executes(ctx -> {
                                                        boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                        Simpletweaks.getConfig().tweaks.enableYeet = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("Yeet enabled: " + val), true);
                                                        return 1;
                                                    })))
                                    .then(CommandManager.literal("strength")
                                            .then(CommandManager.argument("value", FloatArgumentType.floatArg(0.1f))
                                                    .executes(ctx -> {
                                                        float val = FloatArgumentType.getFloat(ctx, "value");
                                                        Simpletweaks.getConfig().tweaks.yeetStrength = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("Yeet Strength set to: " + val), true);
                                                        return 1;
                                                    })))
                            )
                            // Farmland Protect (NEU)
                            .then(CommandManager.literal("farmlandProtect")
                                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                Simpletweaks.getConfig().tweaks.preventFarmlandTrampleWithFeatherFalling = val;
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Farmland Feather Falling Protection: " + val), true);
                                                return 1;
                                            })))
                            // Ladder Speed (NEU)
                            .then(CommandManager.literal("ladderSpeed")
                                    .then(CommandManager.argument("speed", DoubleArgumentType.doubleArg(0.0))
                                            .executes(ctx -> {
                                                double val = DoubleArgumentType.getDouble(ctx, "speed");
                                                Simpletweaks.getConfig().tweaks.ladderClimbingSpeed = val;
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Ladder Climbing Speed set to: " + val), true);
                                                return 1;
                                            })))
                            // Sharpness Cut (NEU)
                            .then(CommandManager.literal("sharpnessCut")
                                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                Simpletweaks.getConfig().tweaks.sharpnessCutsGrass = val;
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Sharpness Cuts Grass: " + val), true);
                                                return 1;
                                            })))
                            // Mute Suffixes (NEU: LISTE)
                            .then(CommandManager.literal("muteSuffixes")
                                    .then(CommandManager.literal("list")
                                            .executes(ctx -> {
                                                List<String> list = Simpletweaks.getConfig().tweaks.nametagMuteSuffixes;
                                                ctx.getSource().sendFeedback(() -> Text.literal("Mute Suffixes: " + list.toString()).formatted(Formatting.YELLOW), false);
                                                return list.size();
                                            }))
                                    .then(CommandManager.literal("add")
                                            .then(CommandManager.argument("suffix", StringArgumentType.string())
                                                    .executes(ctx -> {
                                                        String suffix = StringArgumentType.getString(ctx, "suffix");
                                                        List<String> list = Simpletweaks.getConfig().tweaks.nametagMuteSuffixes;
                                                        if (!list.contains(suffix)) {
                                                            list.add(suffix);
                                                            saveConfig();
                                                            ctx.getSource().sendFeedback(() -> Text.literal("Added suffix: " + suffix).formatted(Formatting.GREEN), true);
                                                        } else {
                                                            ctx.getSource().sendError(Text.literal("Suffix already exists."));
                                                        }
                                                        return 1;
                                                    })))
                                    .then(CommandManager.literal("remove")
                                            .then(CommandManager.argument("suffix", StringArgumentType.string())
                                                    .suggests((context, builder) -> CommandSource.suggestMatching(Simpletweaks.getConfig().tweaks.nametagMuteSuffixes, builder))
                                                    .executes(ctx -> {
                                                        String suffix = StringArgumentType.getString(ctx, "suffix");
                                                        List<String> list = Simpletweaks.getConfig().tweaks.nametagMuteSuffixes;
                                                        if (list.remove(suffix)) {
                                                            saveConfig();
                                                            ctx.getSource().sendFeedback(() -> Text.literal("Removed suffix: " + suffix).formatted(Formatting.GREEN), true);
                                                        } else {
                                                            ctx.getSource().sendError(Text.literal("Suffix not found."));
                                                        }
                                                        return 1;
                                                    })))
                                    .then(CommandManager.literal("clear")
                                            .executes(ctx -> {
                                                Simpletweaks.getConfig().tweaks.nametagMuteSuffixes.clear();
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Cleared all mute suffixes.").formatted(Formatting.RED), true);
                                                return 1;
                                            }))
                            )
                    )
            );
        });
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

            // Filter Logik
            boolean shouldRemove = false;
            switch (mode) {
                case "standard":
                    // Nur das normale Minecart
                    if (isStandard) shouldRemove = true;
                    break;
                case "empty":
                    // Normales Minecart ODER leere Storage Carts
                    if (isStandard) {
                        shouldRemove = true;
                    } else if (isStorage) {
                        if (((StorageMinecartEntity) cart).isEmpty()) shouldRemove = true;
                    }
                    // TNT, Furnace, Spawner, CommandBlock werden hier NICHT gelöscht
                    break;
                case "all":
                    // Alles weg (TNT, Spawner, gefüllte Kisten...)
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

    private static void saveConfig() {
        AutoConfig.getConfigHolder(SimpletweaksConfig.class).save();
    }

    private static boolean checkPermission(ServerCommandSource source, int level) {
        if (source.getEntity() instanceof ServerPlayerEntity player) {
            return source.getServer().getPlayerManager().isOperator(player.getPlayerConfigEntry());
        }
        return true;
    }
}

// todo: killcart soll einen parameter haben der sagt ob er auch chart variationen löschen soll, also tnt kiste hopper etc. das selbe bei boats. default nur leere standart boote/charts, all  für alles und eine zwichenstufe für leere kisten/hopper etc.