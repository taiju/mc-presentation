package info.taiju.minecraft.presentation.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import info.taiju.minecraft.presentation.Presentation;
import info.taiju.minecraft.presentation.PresentationData;
import info.taiju.minecraft.presentation.PresentationManager;
import info.taiju.minecraft.presentation.SlideData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

/**
 * Render presentations in the world
 */
@EventBusSubscriber(modid = Presentation.MODID, value = Dist.CLIENT)
public class PresentationRenderer {

    @SubscribeEvent
    public static void onRenderAfterParticles(RenderLevelStageEvent.AfterParticles event) {
        PresentationManager manager = PresentationManager.getInstance();

        for (PresentationData presentation : manager.getAllPresentations().values()) {
            if (!presentation.isPlaced()) {
                continue;
            }

            SlideData currentSlide = presentation.getCurrentSlide();
            if (currentSlide == null) {
                continue;
            }

            ResourceLocation texture = currentSlide.getTextureLocation();
            if (texture == null) {
                texture = ImageLoader.getInstance().loadTexture(
                        presentation.getId(),
                        currentSlide.getFilename());
                if (texture != null) {
                    currentSlide.setTextureLocation(texture);
                } else {
                    continue;
                }
            }

            renderPresentation(event, presentation, texture);
        }
    }

    private static void renderPresentation(RenderLevelStageEvent.AfterParticles event,
            PresentationData presentation,
            ResourceLocation texture) {
        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        poseStack.pushPose();

        var camera = event.getCamera();
        var cameraPos = camera.getPosition();

        poseStack.translate(
                presentation.getX() - cameraPos.x,
                presentation.getY() - cameraPos.y,
                presentation.getZ() - cameraPos.z);

        poseStack.mulPose(Axis.YP.rotationDegrees(-presentation.getRotation()));

        int lightmap = 0xF000F0;
        Matrix4f matrix = poseStack.last().pose();

        RenderType renderType = RenderType.text(texture);
        VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);

        float w = (float) presentation.getWidth();
        float h = (float) presentation.getHeight();

        float u0 = 0.0f;
        float u1 = 1.0f;
        float v0 = 0.0f;
        float v1 = 1.0f;

        vertexConsumer.addVertex(matrix, 0, 0, 0)
                .setColor(255, 255, 255, 255)
                .setUv(u0, v1)
                .setLight(lightmap);

        vertexConsumer.addVertex(matrix, w, 0, 0)
                .setColor(255, 255, 255, 255)
                .setUv(u1, v1)
                .setLight(lightmap);

        vertexConsumer.addVertex(matrix, w, h, 0)
                .setColor(255, 255, 255, 255)
                .setUv(u1, v0)
                .setLight(lightmap);

        vertexConsumer.addVertex(matrix, 0, h, 0)
                .setColor(255, 255, 255, 255)
                .setUv(u0, v0)
                .setLight(lightmap);

        vertexConsumer.addVertex(matrix, w, 0, 0)
                .setColor(0, 0, 0, 255)
                .setUv(u0, v1)
                .setLight(lightmap);

        vertexConsumer.addVertex(matrix, 0, 0, 0)
                .setColor(0, 0, 0, 255)
                .setUv(u1, v1)
                .setLight(lightmap);

        vertexConsumer.addVertex(matrix, 0, h, 0)
                .setColor(0, 0, 0, 255)
                .setUv(u1, v0)
                .setLight(lightmap);

        vertexConsumer.addVertex(matrix, w, h, 0)
                .setColor(0, 0, 0, 255)
                .setUv(u0, v0)
                .setLight(lightmap);

        bufferSource.endBatch();

        poseStack.popPose();
    }
}
