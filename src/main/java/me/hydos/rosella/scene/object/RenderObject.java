package me.hydos.rosella.scene.object;

import me.hydos.rosella.Rosella;
import me.hydos.rosella.device.VulkanDevice;
import me.hydos.rosella.memory.ManagedBuffer;
import me.hydos.rosella.memory.Memory;
import me.hydos.rosella.render.Topology;
import me.hydos.rosella.render.fbo.FrameBufferObject;
import me.hydos.rosella.render.info.InstanceInfo;
import me.hydos.rosella.render.info.RenderInfo;
import me.hydos.rosella.render.material.Material;
import me.hydos.rosella.render.pipeline.Pipeline;
import me.hydos.rosella.render.pipeline.state.StateInfo;
import me.hydos.rosella.render.texture.ImmutableTextureMap;
import me.hydos.rosella.render.texture.Texture;
import me.hydos.rosella.render.vertex.VertexFormats;
import me.hydos.rosella.ubo.BasicUbo;
import me.hydos.rosella.ubo.UboDataProvider;
import me.hydos.rosella.util.VkUtils;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.nio.ByteBuffer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.lwjgl.vulkan.VK10.*;

public abstract class RenderObject implements Renderable {

    protected Material material;
    public Future<RenderInfo> renderInfo;
    public InstanceInfo instanceInfo;

    public Matrix4f modelMatrix;
    public final Matrix4f viewMatrix;
    public final Matrix4f projectionMatrix;
    public FrameBufferObject fbo;
    protected ByteBuffer indices;
    protected ByteBuffer vertexBuffer;
    private final UboDataProvider<RenderObject> uboDataProvider;

    protected RenderObject(Material material, Matrix4f projectionMatrix, Matrix4f viewMatrix, Matrix4f modelMatrix, UboDataProvider<RenderObject> dataProvider, @Nullable FrameBufferObject fbo) {
        this.material = material;
        this.projectionMatrix = projectionMatrix;
        this.viewMatrix = viewMatrix;
        this.modelMatrix = modelMatrix;
        this.uboDataProvider = dataProvider;
        this.fbo = fbo;
    }

    @Override
    public void onAddedToScene(Rosella rosella) {
        if(fbo != null) {
            if (this.fbo.colourTexture == null) {
                this.fbo.createColourTexture(rosella);
                this.fbo.createDepthTexture(rosella);
            }
            // Pain
            if (this.fbo.isSwapchainBased) {
                throw new RuntimeException("Cannot display main fbo to another fbo!");
            }
            this.material = new Material(
                    rosella.common.pipelineManager.registerPipeline(
                            new Pipeline(
                                    rosella.renderer.mainRenderPass,
                                    material.pipeline().getShaderProgram(),
                                    Topology.TRIANGLES,
                                    VertexFormats.POSITION_COLOR3f_UV0,
                                    StateInfo.DEFAULT_GUI
                            )
                    ),
                    ImmutableTextureMap.builder()
                            .entry("texSampler", this.fbo.colourTexture)
                            .build()
            );

            // Explicitly transitioning the depth image
            VkUtils.transitionImageLayout(
                    rosella.renderer,
                    rosella.common.device,
                    this.fbo.depthTexture.getTextureImage().pointer(),
                    VK_FORMAT_D32_SFLOAT,
                    VK_IMAGE_LAYOUT_UNDEFINED,
                    VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL
            );
        }


        if (instanceInfo == null) {
            instanceInfo = new InstanceInfo(new BasicUbo<>(
                    rosella.common,
                    material.pipeline().getShaderProgram(),
                    rosella.renderer.swapchain,
                    uboDataProvider,
                    this
            ), material);
        }
        if (renderInfo == null) {
            renderInfo = CompletableFuture.completedFuture(new RenderInfo(
                    rosella.bufferManager.createVertexBuffer(new ManagedBuffer<>(vertexBuffer, true)),
                    rosella.bufferManager.createIndexBuffer(new ManagedBuffer<>(indices, true)),
                    indices.capacity() / 4
            ));
        }
    }

    @Override
    public void free(VulkanDevice device, Memory memory) {
        instanceInfo.free(device, memory);
        try {
            renderInfo.get().free(device, memory);
        } catch (InterruptedException | ExecutionException e) {
            Rosella.LOGGER.error("Error freeing render info", e);
        }
    }

    @Override
    public void rebuild(Rosella rosella) {
        instanceInfo.rebuild(rosella);
    }

    @Override
    public void hardRebuild(Rosella rosella) {
        instanceInfo.hardRebuild(rosella);
    }

    @Override
    public InstanceInfo getInstanceInfo() {
        return instanceInfo;
    }

    @Override
    public Future<RenderInfo> getRenderInfo() {
        return renderInfo;
    }

    /**
     * A simple way to build {@link RenderObject}'s
     */
    public abstract static class Builder<T> {

        protected final Matrix4f modelMatrix = new Matrix4f();
        protected Matrix4f viewMatrix;
        protected Matrix4f projectionMatrix;
        protected Material material;
        protected UboDataProvider<RenderObject> uboDataProvider;
        protected FrameBufferObject fbo;

        public T material(Material material) {
            this.material = material;
            return (T) this;
        }

        public T fbo(FrameBufferObject frameBufferObject) {
            this.fbo = frameBufferObject;
            return (T) this;
        }

        public T projectionMatrix(Matrix4f projectionMatrix) {
            this.projectionMatrix = projectionMatrix;
            return (T) this;
        }

        public T viewMatrix(Matrix4f viewMatrix) {
            this.viewMatrix = viewMatrix;
            return (T) this;
        }

        public T uboDataProvider(UboDataProvider<RenderObject> uboDataProvider) {
            this.uboDataProvider = uboDataProvider;
            return (T) this;
        }

        public T translate(float x, float y, float z) {
            modelMatrix.translate(x, y, z);
            return (T) this;
        }

        public T translate(Vector3f position) {
            modelMatrix.translate(position);
            return (T) this;
        }

        public T scale(Vector3f scale) {
            modelMatrix.scale(scale);
            return (T) this;
        }

        public T scale(float scale) {
            modelMatrix.scale(scale);
            return (T) this;
        }

        /**
         * Safety check. makes sure that an RenderObject can be built safely
         */
        protected void check(boolean disableMaterialCheck) {
            if (viewMatrix == null) {
                throw new RuntimeException("No viewMatrix was passed or it was null.");
            }

            if (projectionMatrix == null) {
                throw new RuntimeException("No projectionMatrix was passed or it was null.");
            }

            if (uboDataProvider == null) {
                throw new RuntimeException("No uboDataProvider was passed or it was null.");
            }

            if (material == null && !disableMaterialCheck) {
                throw new RuntimeException("No material was passed or it was null.");
            }
        }
    }
}
