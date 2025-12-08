package com.simpletweaks;

import com.simpletweaks.client.AutoWalkHandler;
import com.simpletweaks.client.SpawnElytraClient;
import com.simpletweaks.client.gui.SpawnElytraTimerOverlay;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class SimpletweaksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AutoWalkHandler.register();
        SpawnElytraClient.register();
        HudRenderCallback.EVENT.register(new SpawnElytraTimerOverlay());
    }
}