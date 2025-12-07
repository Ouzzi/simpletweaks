package com.simpletweaks;

import com.simpletweaks.client.AutoWalkHandler;
import net.fabricmc.api.ClientModInitializer;

public class SimpletweaksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        // Auto Walk Keybind und Logik registrieren
        AutoWalkHandler.register();
    }
}