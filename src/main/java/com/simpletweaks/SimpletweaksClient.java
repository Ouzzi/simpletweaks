package com.simpletweaks;

import com.simpletweaks.block.ModBlocks;
import com.simpletweaks.block.entity.SpawnTeleporterBlockEntity;
import com.simpletweaks.client.AutoWalkHandler;
import com.simpletweaks.client.SpawnElytraClient;
import com.simpletweaks.client.gui.SpawnElytraTimerOverlay;
import com.simpletweaks.entity.ModEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class SimpletweaksClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        AutoWalkHandler.register();
        SpawnElytraClient.register();
        HudRenderCallback.EVENT.register(new SpawnElytraTimerOverlay());
        EntityRendererRegistry.register(ModEntities.BRICK_PROJECTILE, FlyingItemEntityRenderer::new);


    }
}