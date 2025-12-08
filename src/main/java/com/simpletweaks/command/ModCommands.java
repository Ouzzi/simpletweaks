package com.simpletweaks.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.simpletweaks.Simpletweaks;
import com.simpletweaks.config.SimpletweaksConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;

public class ModCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {

            dispatcher.register(CommandManager.literal("killboats")
                    .requires(source -> source.hasPermissionLevel(2)) // Nur OPs / Cheats
                    .executes(context -> {
                        // Config Check
                        if (!Simpletweaks.getConfig().commands.enableKillBoatsCommand) {
                            context.getSource().sendError(Text.literal("Command is disabled in config."));
                            return 0;
                        }

                        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity player)) {
                            context.getSource().sendError(Text.literal("Only players can use this command."));
                            return 0;
                        }

                        // Suche Boote im Radius von 100 Blöcken
                        Box box = player.getBoundingBox().expand(100);
                        List<BoatEntity> boats = player.getEntityWorld().getEntitiesByClass(BoatEntity.class, box, boat -> !boat.hasPassengers());

                        int count = 0;
                        for (BoatEntity boat : boats) {
                            boat.discard();
                            count++;
                        }

                        // Variable muss für Lambda final sein
                        final int finalCount = count;
                        context.getSource().sendFeedback(() -> Text.translatable("commands.simpletweaks.killboats.success", finalCount), true);
                        return count;
                    }));

            // --- KILL CARTS ---
            // Entfernt leere Loren im Radius von 100 Blöcken
            dispatcher.register(CommandManager.literal("killcarts")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        if (!Simpletweaks.getConfig().commands.enableKillCartsCommand) {
                            context.getSource().sendError(Text.literal("Command is disabled in config."));
                            return 0;
                        }

                        if (!(context.getSource().getEntity() instanceof ServerPlayerEntity player)) {
                            context.getSource().sendError(Text.literal("Only players can use this command."));
                            return 0;
                        }

                        Box box = player.getBoundingBox().expand(100);
                        // AbstractMinecartEntity erfasst alle Loren-Typen (TNT, Kiste, Hopper, etc.)
                        List<AbstractMinecartEntity> carts = player.getEntityWorld().getEntitiesByClass(AbstractMinecartEntity.class, box, cart -> !cart.hasPassengers());

                        int count = 0;
                        for (AbstractMinecartEntity cart : carts) {
                            cart.discard();
                            count++;
                        }

                        final int finalCount = count;
                        context.getSource().sendFeedback(() -> Text.literal("Removed " + finalCount + " empty minecarts."), true);
                        return count;
                    }));

            // --- CONFIG COMMANDS (simpletweaks) ---
            dispatcher.register(CommandManager.literal("simpletweaks")
                    .requires(source -> source.hasPermissionLevel(4)) // Admin Only (Level 4)

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

                    // 2. Spawn Elytra (/simpletweaks spawn elytra ...)
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

                    // 7. Tweaks
                    .then(CommandManager.literal("tweaks")
                            .then(CommandManager.literal("autowalk")
                                    .then(CommandManager.argument("enabled", BoolArgumentType.bool())
                                            .executes(ctx -> {
                                                boolean val = BoolArgumentType.getBool(ctx, "enabled");
                                                Simpletweaks.getConfig().tweaks.enableAutowalk = val;
                                                saveConfig();
                                                ctx.getSource().sendFeedback(() -> Text.literal("Auto-Walk enabled: " + val), true);
                                                return 1;
                                            })))
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
                                    .then(CommandManager.literal("yeetStrength")
                                            .then(CommandManager.argument("value", FloatArgumentType.floatArg(0.01f))
                                                    .executes(ctx -> {
                                                        float val = FloatArgumentType.getFloat(ctx, "value");
                                                        Simpletweaks.getConfig().tweaks.yeetStrength = val;
                                                        saveConfig();
                                                        ctx.getSource().sendFeedback(() -> Text.literal("Yeet Strength set to: " + val), true);
                                                        return 1;
                                                    })))
                            )
                    )
            );
        });
    }

    // Hilfsmethode zum Speichern der Config
    private static void saveConfig() {
        AutoConfig.getConfigHolder(SimpletweaksConfig.class).save();
    }
}