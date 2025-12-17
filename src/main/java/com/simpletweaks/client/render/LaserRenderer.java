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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
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
                    Direction side = hit instanceof BlockHitResult bHit ? bHit.getSide() : Direction.UP;

                    Vector3f posF = new Vector3f((float) pos.x, (float) pos.y, (float) pos.z);
                    ClientPlayNetworking.send(new LaserManager.LaserPayload(me.getUuid(), posF, true));

                    renderPixelCircle(context, pos, side, Simpletweaks.getConfig().visuals.laserPointer.color);
                }
            }
        }

        LaserManager.ACTIVE_LASERS.forEach((uuid, data) -> {
            if (!uuid.equals(me.getUuid())) {
                renderPixelCircle(context, new Vec3d(data.pos().x, data.pos().y, data.pos().z), Direction.UP, Simpletweaks.getConfig().visuals.laserPointer.color);
            }
        });
    }

    private static void renderPixelCircle(WorldRenderContext context, Vec3d pos, Direction side, int color) {
        MinecraftClient client = MinecraftClient.getInstance();
        MatrixStack matrices = context.matrices();
        Vec3d cameraPos = client.gameRenderer.getCamera().getCameraPos();

        double distance = cameraPos.distanceTo(pos);

        // --- DYNAMISCHE SKALIERUNG (Subjektiv) ---
        float distanceScale = (float) (distance * 0.12f);
        float finalScale = Math.max(Simpletweaks.getConfig().visuals.laserPointer.scale, distanceScale);

        // --- DYNAMISCHER OFFSET GEGEN FLACKERN ---
        // Je weiter weg, desto mehr Abstand zur Wand (verhindert Z-Fighting in der Ferne)
        double wallOffset = 0.01 + (distance * 0.002);

        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = 255;

        matrices.push();
        matrices.translate(pos.x - cameraPos.x, pos.y - cameraPos.y, pos.z - cameraPos.z);

        // Den Punkt von der Wand weg schieben
        matrices.translate(side.getOffsetX() * wallOffset, side.getOffsetY() * wallOffset, side.getOffsetZ() * wallOffset);

        rotateToFace(matrices, side);
        matrices.scale(finalScale, finalScale, finalScale);
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        try (BufferAllocator allocator = new BufferAllocator(512)) {
            VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(allocator);

            // FIX: debugQuads() benötigt NUR Position und Farbe. Keine Normale, keine LineWidth.
            VertexConsumer buffer = immediate.getBuffer(RenderLayers.debugQuads());

            // Minecraft Pixel Style (abgerundet durch 3 überlagerte Quads)
            drawSimpleQuad(buffer, matrix, -0.5f, -0.3f, 0.5f, 0.3f, r, g, b, a);
            drawSimpleQuad(buffer, matrix, -0.3f, -0.5f, 0.3f, 0.5f, r, g, b, a);
            drawSimpleQuad(buffer, matrix, -0.4f, -0.4f, 0.4f, 0.4f, r, g, b, a);

            immediate.draw();
        }

        matrices.pop();
    }

    private static void drawSimpleQuad(VertexConsumer buffer, Matrix4f matrix, float x1, float y1, float x2, float y2, int r, int g, int b, int a) {
        // Dieser Layer (debugQuads) akzeptiert NUR vertex() und color()!
        buffer.vertex(matrix, x1, y1, 0).color(r, g, b, a);
        buffer.vertex(matrix, x2, y1, 0).color(r, g, b, a);
        buffer.vertex(matrix, x2, y2, 0).color(r, g, b, a);
        buffer.vertex(matrix, x1, y2, 0).color(r, g, b, a);
    }

    private static void rotateToFace(MatrixStack matrices, Direction side) {
        switch (side) {
            case DOWN -> matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(90));
            case UP -> matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90));
            case SOUTH -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
            case WEST -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
            case EAST -> matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90));
            default -> {}
        }
    }
}