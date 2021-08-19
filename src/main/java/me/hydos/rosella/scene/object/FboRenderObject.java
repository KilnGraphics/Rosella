package me.hydos.rosella.scene.object;

import me.hydos.rosella.Rosella;
import me.hydos.rosella.render.Topology;
import me.hydos.rosella.render.fbo.FrameBufferObject;
import me.hydos.rosella.render.material.Material;
import me.hydos.rosella.render.model.GuiRenderObject;
import me.hydos.rosella.render.pipeline.Pipeline;
import me.hydos.rosella.render.pipeline.state.StateInfo;
import me.hydos.rosella.render.renderer.Renderer;
import me.hydos.rosella.render.shader.ShaderProgram;
import me.hydos.rosella.render.texture.*;
import me.hydos.rosella.render.vertex.VertexFormats;
import me.hydos.rosella.util.VkUtils;
import me.hydos.rosella.vkobjects.VkCommon;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.util.vma.Vma;

import static org.lwjgl.vulkan.VK10.*;

public class FboRenderObject extends GuiRenderObject {

    public final Texture colourTexture;
    public final Texture depthTexture;

    private FboRenderObject(@NotNull Material material, float z, @NotNull Vector3f colour, @NotNull Matrix4f viewMatrix, @NotNull Matrix4f projectionMatrix, Texture colourTexture, Texture depthTexture) {
        super(material, z, colour, viewMatrix, projectionMatrix);
        this.colourTexture = colourTexture;
        this.depthTexture = depthTexture;
    }

    public static FboRenderObject create(FrameBufferObject frameBufferObject, float z, Rosella rosella, Matrix4f viewMatrix, Matrix4f projectionMatrix, ShaderProgram shaderProgram) {
        if (frameBufferObject.isSwapchainBased) {
            throw new RuntimeException("Cannot display main fbo to another fbo!");
        }
        Texture colourTexture = createColourTexture(rosella);
        Texture depthTexture = createDepthTexture(rosella);
        Material fboMaterial = new Material(
                rosella.common.pipelineManager.registerPipeline(
                        new Pipeline(
                                rosella.renderer.mainRenderPass,
                                shaderProgram,
                                Topology.TRIANGLES,
                                VertexFormats.POSITION_COLOR3f_UV0,
                                StateInfo.DEFAULT_GUI
                        )
                ),
                ImmutableTextureMap.builder()
                        .entry("texSampler", colourTexture)
                        .build()
        );

        // Explicitly transitioning the depth image
        VkUtils.transitionImageLayout(
                rosella.renderer,
                rosella.common.device,
                depthTexture.getTextureImage().pointer(),
                VK_FORMAT_D32_SFLOAT,
                VK_IMAGE_LAYOUT_UNDEFINED,
                VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL
        );

        return new FboRenderObject(fboMaterial, z, new Vector3f(), viewMatrix, projectionMatrix, colourTexture, depthTexture);
    }

    public static Texture createColourTexture(Rosella rosella) {
        TextureManager textureManager = rosella.common.textureManager;

        int textureId = textureManager.generateTextureId();
        createTexture(
                rosella.common,
                rosella.renderer,
                textureId,
                rosella.renderer.swapchain.getSwapChainExtent().width(),
                rosella.renderer.swapchain.getSwapChainExtent().height(), //FIXME: this might break on resize?
                VK_FORMAT_B8G8R8A8_UNORM,
                false,
                VK_IMAGE_USAGE_COLOR_ATTACHMENT_BIT
        );
        textureManager.setTextureSampler(
                textureId,
                "texSampler",
                new SamplerCreateInfo(TextureFilter.NEAREST, WrapMode.REPEAT)
        );
        return textureManager.getTexture(textureId);
    }

    public static Texture createDepthTexture(Rosella rosella) {
        TextureManager textureManager = rosella.common.textureManager;

        int textureId = textureManager.generateTextureId();
        createTexture(
                rosella.common,
                rosella.renderer,
                textureId,
                rosella.renderer.swapchain.getSwapChainExtent().width(),
                rosella.renderer.swapchain.getSwapChainExtent().height(), //FIXME: this might break on resize?
                VK_FORMAT_D32_SFLOAT,
                true,
                VK_IMAGE_USAGE_DEPTH_STENCIL_ATTACHMENT_BIT
        );
        textureManager.setTextureSampler(
                textureId,
                "texSampler",
                new SamplerCreateInfo(TextureFilter.NEAREST, WrapMode.REPEAT)
        );
        return textureManager.getTexture(textureId);
    }

    public static void createTexture(VkCommon common, Renderer renderer, int textureId, int width, int height, int imgFormat, boolean createDepthTexture, int extraImageUsage) {
        Texture currentTexture = common.textureManager.getTexture(textureId);
        if (currentTexture != null) {
            if (currentTexture.getImageFormat() != imgFormat || currentTexture.getWidth() != width || currentTexture.getHeight() != height) {
                currentTexture.getTextureImage().free(common.device, common.memory);
            } else {
                // we can use the old texture if it satisfies the requirements
                return;
            }
        }
        TextureImage textureImage;
        if (!createDepthTexture) {
            textureImage = createTextureImage(renderer, common, width, height, imgFormat, extraImageUsage, VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL);
            textureImage.setView(VkUtils.createTextureImageView(common.device, imgFormat, textureImage.pointer()));
        } else {
            textureImage = createTextureImage(renderer, common, width, height, imgFormat, extraImageUsage, VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL);
            textureImage.setView(VkUtils.createDepthTextureImageView(common.device, imgFormat, textureImage.pointer()));
        }
        common.textureManager.createTextureRaw(textureId, new Texture(imgFormat, width, height, textureImage, null));
    }

    public static TextureImage createTextureImage(Renderer renderer, VkCommon common, int width, int height, int imgFormat, int extraUsage, int layout) {
        TextureImage image = VkUtils.createImage(
                common.memory,
                width,
                height,
                imgFormat,
                VK_IMAGE_TILING_OPTIMAL,
                VK_IMAGE_USAGE_SAMPLED_BIT | extraUsage,
                VK_MEMORY_PROPERTY_DEVICE_LOCAL_BIT,
                Vma.VMA_MEMORY_USAGE_UNKNOWN // FIXME
        );

        VkUtils.transitionImageLayout(
                renderer,
                common.device,
                image.pointer(),
                imgFormat,
                VK_IMAGE_LAYOUT_UNDEFINED,
                layout
        );

        return image;
    }
}
