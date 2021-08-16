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
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.vulkan.VK10;

public class FboRenderObject extends GuiRenderObject {

    private FboRenderObject(@NotNull Material material, float z, @NotNull Vector3f colour, @NotNull Matrix4f viewMatrix, @NotNull Matrix4f projectionMatrix) {
        super(material, z, colour, viewMatrix, projectionMatrix);
    }

    public static FboRenderObject create(FrameBufferObject frameBufferObject, float z, Rosella rosella, Matrix4f viewMatrix, Matrix4f projectionMatrix, ShaderProgram shaderProgram) {
        if (frameBufferObject.isSwapchainBased) {
            throw new RuntimeException("Cannot display main fbo to another fbo!");
        }
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
                        .entry("texSampler", createFboTextureObject(
                                VK10.VK_FORMAT_R8G8B8A8_SRGB,
                                frameBufferObject,
                                rosella
                        ))
                        .build()
        );
        return new FboRenderObject(fboMaterial, z, new Vector3f(), viewMatrix, projectionMatrix);
    }

    public static Texture createFboTextureObject(int vkImgFormat, FrameBufferObject frameBufferObject, Rosella rosella) {
        TextureManager textureManager = rosella.common.textureManager;

        int textureId = textureManager.generateTextureId();
        textureManager.createTextureRaw(
                rosella.renderer,
                textureId,
                rosella.renderer.swapchain.getSwapChainExtent().width(),
                rosella.renderer.swapchain.getSwapChainExtent().height(), //FIXME: this might break on resize?
                vkImgFormat,
                frameBufferObject.images.get(0) //FIXME: hardcoded to 0. ew
        );
        textureManager.setTextureSampler(
                textureId,
                "texSampler",
                new SamplerCreateInfo(TextureFilter.NEAREST, WrapMode.REPEAT)
        );
        return textureManager.getTexture(textureId);
    }
}
