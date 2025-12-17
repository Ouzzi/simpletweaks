package com.simpletweaks.client.render;

import com.simpletweaks.Simpletweaks;
import com.simpletweaks.item.custom.LaserPointerItem;
import com.simpletweaks.network.LaserManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.BufferAllocator;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class LaserRenderer {

    private static int tickCounter = 0;

    public static void render(WorldRenderContext context) {
        if (!Simpletweaks.getConfig().visuals.laserPointer.enable) return;

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity me = client.player;
        if (me == null) return;

        float tickDelta = client.getRenderTickCounter().getTickProgress(true);

        if (me.isUsingItem() && me.getActiveItem().getItem() instanceof LaserPointerItem) {
            if (tickCounter++ % 2 == 0) {
                double range = Simpletweaks.getConfig().visuals.laserPointer.range;
                HitResult hit = me.raycast(range, tickDelta, false);

                if (hit.getType() != HitResult.Type.MISS) {
                    Vec3d pos = hit.getPos();
                    Vector3f posF = new Vector3f((float) pos.x, (float) pos.y, (float) pos.z);

                    ClientPlayNetworking.send(new LaserManager.LaserPayload(me.getUuid(), posF, true));
                    renderDot(context, pos, Simpletweaks.getConfig().visuals.laserPointer.color);
                }
            }
        }

        LaserManager.ACTIVE_LASERS.forEach((uuid, data) -> {
            if (!uuid.equals(me.getUuid())) {
                Vec3d pos = new Vec3d(data.pos().x, data.pos().y, data.pos().z);
                renderDot(context, pos, Simpletweaks.getConfig().visuals.laserPointer.color);
            }
        });
    }

    private static void renderDot(WorldRenderContext context, Vec3d targetPos, int color) {
        MinecraftClient client = MinecraftClient.getInstance();
        MatrixStack matrices = context.matrices();
        Vec3d cameraPos = client.gameRenderer.getCamera().getCameraPos();

        float scale = Simpletweaks.getConfig().visuals.laserPointer.scale;

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = 255;

        matrices.push();
        matrices.translate(targetPos.x - cameraPos.x, targetPos.y - cameraPos.y, targetPos.z - cameraPos.z);
        matrices.scale(scale, scale, scale);

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        try (BufferAllocator allocator = new BufferAllocator(256)) {
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(allocator);

            // FIX: 'getDebugLineStrip(1.0)' nutzen.
            // Das existiert garantiert, da es für F3+B Hitboxen genutzt wird.
            // Parameter 1.0 ist die Linienbreite.
            VertexConsumer buffer = immediate.getBuffer(RenderLayer.getDebugLineStrip(1.0));

            float s = 0.5f;

            // X-Achse
            drawLine(buffer, matrix, -s, 0, 0, s, 0, 0, r, g, b, a);
            // Y-Achse
            drawLine(buffer, matrix, 0, -s, 0, 0, s, 0, r, g, b, a);
            // Z-Achse
            drawLine(buffer, matrix, 0, 0, -s, 0, 0, s, r, g, b, a);

            immediate.draw();
        }

        matrices.pop();
    }

    private static void drawLine(VertexConsumer buffer, Matrix4f matrix, float x1, float y1, float z1, float x2, float y2, float z2, int r, int g, int b, int a) {
        // Für 'getDebugLineStrip' brauchen wir keine Normals, nur Position und Color
        buffer.vertex(matrix, x1, y1, z1).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, z2).color(r, g, b, a);
    }
}