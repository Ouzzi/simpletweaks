package com.simpletweaks;

import com.simpletweaks.client.AutoWalkHandler;
import com.simpletweaks.client.SpawnElytraClient;
import com.simpletweaks.client.gui.ElytraPitchHud;
import com.simpletweaks.client.gui.PickupNotifierHud;
import com.simpletweaks.client.gui.SpawnElytraTimerOverlay;
import com.simpletweaks.client.gui.SpeedLinesOverlay;
import com.simpletweaks.client.gui.tooltip.MapTooltipComponent;
import com.simpletweaks.client.gui.tooltip.MapTooltipData;
import com.simpletweaks.client.render.LaserRenderer;
import com.simpletweaks.entity.ModEntities;
import com.simpletweaks.event.HoeHarvestHandler;
import com.simpletweaks.network.LaserManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class SimpletweaksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AutoWalkHandler.register();
        SpawnElytraClient.register();
        EntityRendererRegistry.register(ModEntities.BRICK_PROJECTILE, FlyingItemEntityRenderer::new);
        HoeHarvestHandler.register();
        HudRenderCallback.EVENT.register(new SpawnElytraTimerOverlay());
        HudRenderCallback.EVENT.register(new ElytraPitchHud());
        HudRenderCallback.EVENT.register(new SpeedLinesOverlay());

        TooltipComponentCallback.EVENT.register(data -> {
            if (Simpletweaks.getConfig().visuals.enableMapTooltips && data instanceof MapTooltipData mapData) {
                return new MapTooltipComponent(mapData);
            }
            return null;
        });

        // Client Ticks (zusammengefasst)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!client.isPaused()) {
                PickupNotifierHud.tick();
                LaserManager.tick();
            }
        });

        LaserManager.registerClient();
        WorldRenderEvents.END_MAIN.register(LaserRenderer::render);
    }
}