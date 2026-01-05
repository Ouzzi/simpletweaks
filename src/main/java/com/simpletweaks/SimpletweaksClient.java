package com.simpletweaks;

import com.simpletweaks.client.SpawnElytraClient;
import com.simpletweaks.client.gui.SpawnElytraTimerOverlay;
import com.simpletweaks.client.render.LaserRenderer;
import com.simpletweaks.item.custom.LaserPointerItem;
import com.simpletweaks.network.LaserManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.joml.Vector3f;

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

    // In SimpletweaksClient.java oder einer neuen Klasse 'LaserHandler'
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;

            // Nur alle 2 Ticks (10x pro Sekunde) prüfen reicht völlig aus
            if (client.player.age % 2 != 0) return;

            if (client.player.isUsingItem() && client.player.getActiveItem().getItem() instanceof LaserPointerItem) {
                double range = Simpletweaks.getConfig().visuals.laserPointer.range;
                HitResult hit = client.player.raycast(range, 0f, false); // 0f tickDelta reicht hier

                if (hit.getType() != HitResult.Type.MISS) {
                    Vec3d pos = hit.getPos();
                    Vector3f posF = new Vector3f((float) pos.x, (float) pos.y, (float) pos.z);

                    // Sende Paket nur, wenn wir wirklich aimen
                    ClientPlayNetworking.send(new LaserManager.LaserPayload(client.player.getUuid(), posF, true));
                }
            }
        });
    }
}