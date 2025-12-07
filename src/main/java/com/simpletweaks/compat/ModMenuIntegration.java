package com.simpletweaks.compat;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.config.SimpletweaksConfig;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.autoconfig.AutoConfig;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // Erzeugt das GUI automatisch basierend auf deiner Config-Klasse
        return parent -> AutoConfig.getConfigScreen(SimpletweaksConfig.class, parent).get();
    }
}