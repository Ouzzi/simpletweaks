package com.simpletweaks;

import com.simpletweaks.client.AutoWalkHandler;
import com.simpletweaks.client.SpawnElytraClient;
import com.simpletweaks.client.gui.ElytraPitchHud;
import com.simpletweaks.client.gui.PickupNotifierHud;
import com.simpletweaks.client.gui.SpawnElytraTimerOverlay;
import com.simpletweaks.client.gui.SpeedLinesOverlay;
import com.simpletweaks.client.gui.tooltip.MapTooltipComponent;
import com.simpletweaks.client.gui.tooltip.MapTooltipData;
import com.simpletweaks.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class SimpletweaksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AutoWalkHandler.register();
        SpawnElytraClient.register();
        HudRenderCallback.EVENT.register(new SpawnElytraTimerOverlay());
        EntityRendererRegistry.register(ModEntities.BRICK_PROJECTILE, FlyingItemEntityRenderer::new);

        HudRenderCallback.EVENT.register(new ElytraPitchHud());

        HudRenderCallback.EVENT.register(new SpeedLinesOverlay());

        TooltipComponentCallback.EVENT.register(data -> {
            if (Simpletweaks.getConfig().visuals.enableMapTooltips && data instanceof MapTooltipData mapData) {
                return new MapTooltipComponent(mapData);
            }
            return null;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!client.isPaused()) {
                PickupNotifierHud.tick();
            }
        });
    }
}