package com.simpletweaks;

import com.simpletweaks.client.SpawnElytraClient;
import com.simpletweaks.client.gui.SpawnElytraTimerOverlay;
import com.simpletweaks.client.render.LaserRenderer;
import com.simpletweaks.network.LaserManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;

public class SimpletweaksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        SpawnElytraClient.register();
        HudRenderCallback.EVENT.register(new SpawnElytraTimerOverlay());


        // Client Ticks (zusammengefasst)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!client.isPaused()) {
                LaserManager.tick();
            }
        });

        LaserManager.registerClient();
        WorldRenderEvents.END_MAIN.register(LaserRenderer::render);
    }
}