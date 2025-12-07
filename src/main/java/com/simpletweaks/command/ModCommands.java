package com.simpletweaks.command;

import com.simpletweaks.Simpletweaks;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
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
        });
    }
}