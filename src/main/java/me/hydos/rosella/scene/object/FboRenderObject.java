package me.hydos.rosella.scene.object;

import me.hydos.rosella.Rosella;
import me.hydos.rosella.render.Topology;
import me.hydos.rosella.render.fbo.FrameBufferObject;
import me.hydos.rosella.render.material.Material;
import me.hydos.rosella.render.model.GuiRenderObject;
import me.hydos.rosella.render.pipeline.Pipeline;
import me.hydos.rosella.render.pipeline.state.StateInfo;
import me.hydos.rosella.render.shader.ShaderProgram;
import me.hydos.rosella.render.texture.*;
import me.hydos.rosella.render.vertex.VertexFormats;
import me.hydos.rosella.util.VkUtils;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK10.VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL;

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
        textureManager.createTexture(
                rosella.renderer,
                textureId,
                rosella.renderer.swapchain.getSwapChainExtent().width(),
                rosella.renderer.swapchain.getSwapChainExtent().height(), //FIXME: this might break on resize?
                VK_FORMAT_B8G8R8A8_UNORM,
                false
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
        textureManager.createTexture(
                rosella.renderer,
                textureId,
                rosella.renderer.swapchain.getSwapChainExtent().width(),
                rosella.renderer.swapchain.getSwapChainExtent().height(), //FIXME: this might break on resize?
                VK_FORMAT_D32_SFLOAT,
                true
        );
        textureManager.setTextureSampler(
                textureId,
                "texSampler",
                new SamplerCreateInfo(TextureFilter.NEAREST, WrapMode.REPEAT)
        );
        return textureManager.getTexture(textureId);
    }
}
