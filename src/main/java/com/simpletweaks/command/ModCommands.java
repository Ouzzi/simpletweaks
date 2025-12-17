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

                    // 2. Vaults
                    .then(CommandManager.literal("vaults")
                            .then(CommandManager.literal("cooldown")
                                    .then(CommandManager.argument("days", IntegerArgumentType.integer(0))
                                            .executes(ctx -> {
                                                int val = IntegerArgumentType.getInteger(ctx, "days");
                                                Simpletweaks.getConfig().balancing.vaultCooldownDays = val;
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

                    // 8. Tweaks (Alle Features)
                    .then(CommandManager.literal("tweaks")
                            // AutoWalk
                            .then(CommandManager.literal("autowalk")
                                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                Simpletweaks.getConfig().qOL.enableAutowalk = val;
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
                                                        Simpletweaks.getConfig().fun.enableYeet = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("Yeet enabled: " + val), true);
                                                        return 1;
                                                    })))
                                    .then(CommandManager.literal("strength")
                                            .then(CommandManager.argument("value", FloatArgumentType.floatArg(0.1f))
                                                    .executes(ctx -> {
                                                        float val = FloatArgumentType.getFloat(ctx, "value");
                                                        Simpletweaks.getConfig().fun.yeetStrength = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("Yeet Strength set to: " + val), true);
                                                        return 1;
                                                    })))
                            )
                            // Farmland Protect
                            .then(CommandManager.literal("farmlandProtect")
                                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                Simpletweaks.getConfig().qOL.preventFarmlandTrampleWithFeatherFalling = val;
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Farmland Feather Falling Protection: " + val), true);
                                                return 1;
                                            })))
                            // Ladder Speed
                            .then(CommandManager.literal("ladderSpeed")
                                    .then(CommandManager.argument("speed", DoubleArgumentType.doubleArg(0.0))
                                            .executes(ctx -> {
                                                double val = DoubleArgumentType.getDouble(ctx, "speed");
                                                Simpletweaks.getConfig().qOL.ladderClimbingSpeed = val;
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Ladder Climbing Speed set to: " + val), true);
                                                return 1;
                                            })))
                            // Sharpness Cut
                            .then(CommandManager.literal("sharpnessCut")
                                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                Simpletweaks.getConfig().qOL.sharpnessCutsGrass = val;
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Sharpness Cuts Grass: " + val), true);
                                                return 1;
                                            })))
                            // Mute Suffixes
                            .then(CommandManager.literal("muteSuffixes")
                                    .then(CommandManager.literal("list")
                                            .executes(ctx -> {
                                                List<String> list = Simpletweaks.getConfig().qOL.nametagMuteSuffixes;
                                                ctx.getSource().sendFeedback(() -> Text.literal("Mute Suffixes: " + list.toString()).formatted(Formatting.YELLOW), false);
                                                return list.size();
                                            }))
                                    .then(CommandManager.literal("add")
                                            .then(CommandManager.argument("suffix", StringArgumentType.string())
                                                    .executes(ctx -> {
                                                        String suffix = StringArgumentType.getString(ctx, "suffix");
                                                        List<String> list = Simpletweaks.getConfig().qOL.nametagMuteSuffixes;
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
                                                    .suggests((context, builder) -> CommandSource.suggestMatching(Simpletweaks.getConfig().qOL.nametagMuteSuffixes, builder))
                                                    .executes(ctx -> {
                                                        String suffix = StringArgumentType.getString(ctx, "suffix");
                                                        List<String> list = Simpletweaks.getConfig().qOL.nametagMuteSuffixes;
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
                                                Simpletweaks.getConfig().qOL.nametagMuteSuffixes.clear();
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Cleared all mute suffixes.").formatted(Formatting.RED), true);
                                                return 1;
                                            }))
                                    .then(CommandManager.literal("hoeHarvest")
                                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                                    .executes(ctx -> {
                                                        boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                        Simpletweaks.getConfig().qOL.enableHoeHarvest = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("Hoe Harvest & Replant: " + val), true);
                                                        return 1;
                                                    })))
                            )
                            .then(CommandManager.literal("babySuffixes")
                                    .then(CommandManager.literal("list")
                                            .executes(ctx -> {
                                                List<String> list = Simpletweaks.getConfig().qOL.nametagBabySuffixes;
                                                ctx.getSource().sendFeedback(() -> Text.literal("Baby Suffixes: " + list.toString()).formatted(Formatting.YELLOW), false);
                                                return list.size();
                                            }))
                                    .then(CommandManager.literal("add")
                                            .then(CommandManager.argument("suffix", StringArgumentType.string())
                                                    .executes(ctx -> {
                                                        String suffix = StringArgumentType.getString(ctx, "suffix");
                                                        List<String> list = Simpletweaks.getConfig().qOL.nametagBabySuffixes;
                                                        if (!list.contains(suffix)) {
                                                            list.add(suffix);
                                                            saveConfig();
                                                            ctx.getSource().sendFeedback(() -> Text.literal("Added baby suffix: " + suffix).formatted(Formatting.GREEN), true);
                                                        } else {
                                                            ctx.getSource().sendError(Text.literal("Suffix already exists."));
                                                        }
                                                        return 1;
                                                    })))
                                    .then(CommandManager.literal("remove")
                                            .then(CommandManager.literal("remove")
                                                    .then(CommandManager.argument("suffix", StringArgumentType.string())
                                                            .suggests((context, builder) -> CommandSource.suggestMatching(Simpletweaks.getConfig().qOL.nametagBabySuffixes, builder))
                                                            .executes(ctx -> {
                                                                String suffix = StringArgumentType.getString(ctx, "suffix");
                                                                List<String> list = Simpletweaks.getConfig().qOL.nametagBabySuffixes;
                                                                if (list.remove(suffix)) {
                                                                    saveConfig();
                                                                    ctx.getSource().sendFeedback(() -> Text.literal("Removed baby suffix: " + suffix).formatted(Formatting.GREEN), true);
                                                                } else {
                                                                    ctx.getSource().sendError(Text.literal("Suffix not found."));
                                                                }
                                                                return 1;
                                                            })))
                                    )
                            )
                            // NEU: Player Locator
                            .then(CommandManager.literal("locator")
                                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                Simpletweaks.getConfig().visuals.enablePlayerLocator = val;
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Player Locator enabled: " + val), true);
                                                return 1;
                                            })))
                            // NEU: XP Clumps
                            .then(CommandManager.literal("xpClumps")
                                    .then(CommandManager.literal("enable")
                                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                                    .executes(ctx -> {
                                                        boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                        Simpletweaks.getConfig().optimization.enableXpClumps = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("XP Clumps enabled: " + val), true);
                                                        return 1;
                                                    })))
                                    .then(CommandManager.literal("scale")
                                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                                    .executes(ctx -> {
                                                        boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                        Simpletweaks.getConfig().optimization.scaleXpOrbs = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("XP Orb Scaling enabled: " + val), true);
                                                        return 1;
                                                    })))
                            )
                            // NEU: Status Effect Bars
                            .then(CommandManager.literal("statusBars")
                                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                Simpletweaks.getConfig().visuals.enableStatusEffectBars = val;
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Status Effect Bars enabled: " + val), true);
                                                return 1;
                                            })))
                            // NEU: Chat Heads
                            .then(CommandManager.literal("chatHeads")
                                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                Simpletweaks.getConfig().visuals.enableChatHeads = val;
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Chat Heads enabled: " + val), true);
                                                return 1;
                                            })))
                            // NEU: Throwable Bricks
                            .then(CommandManager.literal("bricks")
                                    .then(CommandManager.literal("enable")
                                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                                    .executes(ctx -> {
                                                        boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                        Simpletweaks.getConfig().fun.enableThrowableBricks = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("Throwable Bricks enabled: " + val), true);
                                                        return 1;
                                                    })))
                                    .then(CommandManager.literal("breakGlass")
                                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                                    .executes(ctx -> {
                                                        boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                        Simpletweaks.getConfig().fun.throwableBricksBreakBlocks = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("Bricks Break Glass enabled: " + val), true);
                                                        return 1;
                                                    })))
                                    .then(CommandManager.literal("damage")
                                            .then(CommandManager.argument("value", FloatArgumentType.floatArg(0.0f))
                                                    .executes(ctx -> {
                                                        float val = FloatArgumentType.getFloat(ctx, "value");
                                                        Simpletweaks.getConfig().fun.brickDamage = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("Brick Damage set to: " + val), true);
                                                        return 1;
                                                    })))
                                    .then(CommandManager.literal("snowballDamage")
                                            .then(CommandManager.argument("value", FloatArgumentType.floatArg(0.0f))
                                                    .executes(ctx -> {
                                                        float val = FloatArgumentType.getFloat(ctx, "value");
                                                        Simpletweaks.getConfig().fun.brickSnowballDamage = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("Brick Snowball Damage set to: " + val), true);
                                                        return 1;
                                                    })))
                            )
                            // NEU: Elytra Pitch Helper
                            .then(CommandManager.literal("elytraHelper")
                                    .then(CommandManager.literal("enable")
                                            .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                                    .executes(ctx -> {
                                                        boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                        Simpletweaks.getConfig().visuals.enableElytraPitchHelper = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("Elytra Pitch Helper enabled: " + val), true);
                                                        return 1;
                                                    })))
                                    .then(CommandManager.literal("angles")
                                            .then(CommandManager.argument("up", FloatArgumentType.floatArg())
                                                    .then(CommandManager.argument("down", FloatArgumentType.floatArg())
                                                            .executes(ctx -> {
                                                                float up = FloatArgumentType.getFloat(ctx, "up");
                                                                float down = FloatArgumentType.getFloat(ctx, "down");
                                                                Simpletweaks.getConfig().visuals.elytraTargetAngleUp = up;
                                                                Simpletweaks.getConfig().visuals.elytraTargetAngleDown = down;
                                                                saveConfig();
                                                                ctx.getSource().sendFeedback(() -> Text.literal("Elytra Helper Targets set to Up:" + up + " Down:" + down), true);
                                                                return 1;
                                                            }))))
                                    .then(CommandManager.literal("tolerance")
                                            .then(CommandManager.argument("value", FloatArgumentType.floatArg(0.0f))
                                                    .executes(ctx -> {
                                                        float val = FloatArgumentType.getFloat(ctx, "value");
                                                        Simpletweaks.getConfig().visuals.elytraPitchTolerance = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("Elytra Helper Tolerance set to: " + val), true);
                                                        return 1;
                                                    })))
                                    .then(CommandManager.literal("sensitivity")
                                            .then(CommandManager.argument("value", FloatArgumentType.floatArg(0.0f))
                                                    .executes(ctx -> {
                                                        float val = FloatArgumentType.getFloat(ctx, "value");
                                                        Simpletweaks.getConfig().visuals.elytraSensitivity = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("Elytra Helper Sensitivity set to: " + val), true);
                                                        return 1;
                                                    })))
                            )
                    )

                    // 9. Visual Settings
                    .then(CommandManager.literal("visuals")

                            // --- SPEED LINES ---
                            .then(CommandManager.literal("speedLines")
                                    .then(CommandManager.literal("enable")
                                            .then(CommandManager.argument("val", BoolArgumentType.bool())
                                                    .executes(ctx -> {
                                                        boolean val = BoolArgumentType.getBool(ctx, "val");
                                                        Simpletweaks.getConfig().visuals.speedLines.enableSpeedLines = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("SpeedLines enabled: " + val), true);
                                                        return 1;
                                                    })))
                                    .then(CommandManager.literal("color")
                                            .then(CommandManager.argument("hex", IntegerArgumentType.integer())
                                                    .executes(ctx -> {
                                                        int val = IntegerArgumentType.getInteger(ctx, "hex");
                                                        Simpletweaks.getConfig().visuals.speedLines.speedLinesColor = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("SpeedLines color set to: " + Integer.toHexString(val)), true);
                                                        return 1;
                                                    })))
                                    .then(CommandManager.literal("alpha")
                                            .then(CommandManager.argument("val", FloatArgumentType.floatArg(0, 1))
                                                    .executes(ctx -> {
                                                        float val = FloatArgumentType.getFloat(ctx, "val");
                                                        Simpletweaks.getConfig().visuals.speedLines.speedLinesAlpha = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("SpeedLines alpha set to: " + val), true);
                                                        return 1;
                                                    })))
                                    .then(CommandManager.literal("amount")
                                            .then(CommandManager.argument("val", FloatArgumentType.floatArg(0))
                                                    .executes(ctx -> {
                                                        float val = FloatArgumentType.getFloat(ctx, "val");
                                                        Simpletweaks.getConfig().visuals.speedLines.speedLinesAmount = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("SpeedLines amount set to: " + val), true);
                                                        return 1;
                                                    })))
                                    .then(CommandManager.literal("threshold")
                                            .then(CommandManager.argument("val", FloatArgumentType.floatArg(0))
                                                    .executes(ctx -> {
                                                        float val = FloatArgumentType.getFloat(ctx, "val");
                                                        Simpletweaks.getConfig().visuals.speedLines.speedThreshold = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("SpeedLines threshold set to: " + val), true);
                                                        return 1;
                                                    })))
                            )

                            // --- PICKUP NOTIFIER ---
                            .then(CommandManager.literal("pickupNotifier")
                                    .then(CommandManager.literal("enable")
                                            .then(CommandManager.argument("val", BoolArgumentType.bool())
                                                    .executes(ctx -> {
                                                        boolean val = BoolArgumentType.getBool(ctx, "val");
                                                        Simpletweaks.getConfig().visuals.pickupNotifier.enablePickupNotifier = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("PickupNotifier enabled: " + val), true);
                                                        return 1;
                                                    })))
                                    .then(CommandManager.literal("offset")
                                            .then(CommandManager.argument("x", IntegerArgumentType.integer())
                                                    .then(CommandManager.argument("y", IntegerArgumentType.integer())
                                                            .executes(ctx -> {
                                                                int x = IntegerArgumentType.getInteger(ctx, "x");
                                                                int y = IntegerArgumentType.getInteger(ctx, "y");
                                                                Simpletweaks.getConfig().visuals.pickupNotifier.pickupNotifierOffsetX = x;
                                                                Simpletweaks.getConfig().visuals.pickupNotifier.pickupNotifierOffsetY = y;
                                                                saveConfig();
                                                                ctx.getSource().sendFeedback(() -> Text.literal("PickupNotifier Offset set to X:" + x + " Y:" + y), true);
                                                                return 1;
                                                            }))))
                                    .then(CommandManager.literal("scale")
                                            .then(CommandManager.argument("val", FloatArgumentType.floatArg(0))
                                                    .executes(ctx -> {
                                                        float val = FloatArgumentType.getFloat(ctx, "val");
                                                        Simpletweaks.getConfig().visuals.pickupNotifier.pickupNotifierScale = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("PickupNotifier scale set to: " + val), true);
                                                        return 1;
                                                    })))
                                    .then(CommandManager.literal("duration")
                                            .then(CommandManager.argument("ticks", IntegerArgumentType.integer(0))
                                                    .executes(ctx -> {
                                                        int val = IntegerArgumentType.getInteger(ctx, "ticks");
                                                        Simpletweaks.getConfig().visuals.pickupNotifier.pickupNotifierDuration = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("PickupNotifier duration set to: " + val), true);
                                                        return 1;
                                                    })))
                                    .then(CommandManager.literal("showXp")
                                            .then(CommandManager.argument("val", BoolArgumentType.bool())
                                                    .executes(ctx -> {
                                                        boolean val = BoolArgumentType.getBool(ctx, "val");
                                                        Simpletweaks.getConfig().visuals.pickupNotifier.pickupNotifierShowXp = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("PickupNotifier Show XP: " + val), true);
                                                        return 1;
                                                    })))
                                    .then(CommandManager.literal("vanillaStyle")
                                            .then(CommandManager.argument("val", BoolArgumentType.bool())
                                                    .executes(ctx -> {
                                                        boolean val = BoolArgumentType.getBool(ctx, "val");
                                                        Simpletweaks.getConfig().visuals.pickupNotifier.pickupVanillaStyle = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("PickupNotifier Vanilla Style: " + val), true);
                                                        return 1;
                                                    })))
                                    .then(CommandManager.literal("opacity")
                                            .then(CommandManager.argument("val", FloatArgumentType.floatArg(0, 1))
                                                    .executes(ctx -> {
                                                        float val = FloatArgumentType.getFloat(ctx, "val");
                                                        Simpletweaks.getConfig().visuals.pickupNotifier.pickupBackgroundOpacity = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("PickupNotifier Bg Opacity: " + val), true);
                                                        return 1;
                                                    })))
                                    // ENUMS (Side & Layout)
                                    .then(CommandManager.literal("side")
                                            .then(CommandManager.argument("side", StringArgumentType.word())
                                                    .suggests((context, builder) -> CommandSource.suggestMatching(new String[]{"left", "right"}, builder))
                                                    .executes(ctx -> {
                                                        String s = StringArgumentType.getString(ctx, "side");
                                                        try {
                                                            SimpletweaksConfig.PickupSide side = SimpletweaksConfig.PickupSide.valueOf(s.toUpperCase());
                                                            Simpletweaks.getConfig().visuals.pickupNotifier.pickupNotifierSide = side;
                                                            saveConfig();
                                                            ctx.getSource().sendFeedback(() -> Text.literal("PickupNotifier Side set to: " + side), true);
                                                        } catch (IllegalArgumentException e) {
                                                            ctx.getSource().sendError(Text.literal("Invalid side. Use 'left' or 'right'."));
                                                        }
                                                        return 1;
                                                    })))
                                    .then(CommandManager.literal("layout")
                                            .then(CommandManager.argument("layout", StringArgumentType.word())
                                                    .suggests((context, builder) -> CommandSource.suggestMatching(new String[]{"icon_name_count", "count_icon_name", "name_icon_count", "icon_count_name"}, builder))
                                                    .executes(ctx -> {
                                                        String s = StringArgumentType.getString(ctx, "layout");
                                                        try {
                                                            SimpletweaksConfig.PickupLayout layout = SimpletweaksConfig.PickupLayout.valueOf(s.toUpperCase());
                                                            Simpletweaks.getConfig().visuals.pickupNotifier.pickupNotifierLayout = layout;
                                                            saveConfig();
                                                            ctx.getSource().sendFeedback(() -> Text.literal("PickupNotifier Layout set to: " + layout), true);
                                                        } catch (IllegalArgumentException e) {
                                                            ctx.getSource().sendError(Text.literal("Invalid layout."));
                                                        }
                                                        return 1;
                                                    })))
                                    // Toggles für Elemente
                                    .then(CommandManager.literal("elements")
                                            .then(CommandManager.literal("item").then(CommandManager.argument("val", BoolArgumentType.bool()).executes(ctx -> {
                                                Simpletweaks.getConfig().visuals.pickupNotifier.pickupShowItem = BoolArgumentType.getBool(ctx, "val"); saveConfig(); return 1;
                                            })))
                                            .then(CommandManager.literal("name").then(CommandManager.argument("val", BoolArgumentType.bool()).executes(ctx -> {
                                                Simpletweaks.getConfig().visuals.pickupNotifier.pickupShowName = BoolArgumentType.getBool(ctx, "val"); saveConfig(); return 1;
                                            })))
                                            .then(CommandManager.literal("count").then(CommandManager.argument("val", BoolArgumentType.bool()).executes(ctx -> {
                                                Simpletweaks.getConfig().visuals.pickupNotifier.pickupShowCount = BoolArgumentType.getBool(ctx, "val"); saveConfig(); return 1;
                                            })))
                                    )
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