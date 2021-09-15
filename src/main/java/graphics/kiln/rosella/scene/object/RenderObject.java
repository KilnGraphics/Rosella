package graphics.kiln.rosella.scene.object;

import graphics.kiln.rosella.Rosella;
import graphics.kiln.rosella.memory.Memory;
import graphics.kiln.rosella.render.Topology;
import graphics.kiln.rosella.render.info.InstanceInfo;
import graphics.kiln.rosella.render.material.Material;
import graphics.kiln.rosella.render.vertex.VertexFormats;
import graphics.kiln.rosella.device.VulkanDevice;
import graphics.kiln.rosella.memory.ManagedBuffer;
import graphics.kiln.rosella.render.fbo.FrameBufferObject;
import graphics.kiln.rosella.render.info.RenderInfo;
import graphics.kiln.rosella.render.pipeline.Pipeline;
import graphics.kiln.rosella.render.pipeline.state.StateInfo;
import graphics.kiln.rosella.render.texture.ImmutableTextureMap;
import graphics.kiln.rosella.ubo.BasicUbo;
import graphics.kiln.rosella.ubo.UboDataProvider;
import graphics.kiln.rosella.util.VkUtils;
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
    public FrameBufferObject[] fbos;
    protected ByteBuffer indices;
    protected ByteBuffer vertexBuffer;
    private final UboDataProvider<RenderObject> uboDataProvider;

    protected RenderObject(Material material, Matrix4f projectionMatrix, Matrix4f viewMatrix, Matrix4f modelMatrix, UboDataProvider<RenderObject> dataProvider, @Nullable FrameBufferObject[] fbos) {
        this.material = material;
        this.projectionMatrix = projectionMatrix;
        this.viewMatrix = viewMatrix;
        this.modelMatrix = modelMatrix;
        this.uboDataProvider = dataProvider;
        this.fbos = fbos;
    }

    @Override
    public void onAddedToScene(Rosella rosella) {
        if (fbos != null) {
            int i = 0;
            ImmutableTextureMap.Builder builder = ImmutableTextureMap.builder();
            for (FrameBufferObject fbo : fbos) {
                if (fbo.colourTexture == null) {
                    fbo.createColourTexture(rosella);
                    fbo.createDepthTexture(rosella);
                }

                // Pain
                if (fbo.isSwapchainBased) {
                    throw new RuntimeException("Cannot display main fbo to another fbo!");
                }

                // Explicitly transitioning the depth image
                VkUtils.transitionImageLayout(
                        rosella.renderer,
                        rosella.common.device,
                        fbo.depthTexture.getTextureImage().pointer(),
                        VK_FORMAT_D32_SFLOAT,
                        VK_IMAGE_LAYOUT_UNDEFINED,
                        VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL
                );

                builder.entry("texSampler_" + i, fbo.colourTexture);
                i++;
            }

            this.material = new Material(
                    rosella.common.pipelineManager.registerPipeline(
                            new Pipeline(
                                    rosella.renderer.mainRenderPass,
                                    material.pipeline().getShaderProgram(),
                                    Topology.TRIANGLES,
                                    VertexFormats.POSITION_COLOR3f_UV0,
                                    StateInfo.NO_CULL_3D
                            )
                    ), builder.build()
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
        protected FrameBufferObject[] fbo;

        public T material(Material material) {
            this.material = material;
            return (T) this;
        }

        public T fbo(FrameBufferObject frameBufferObject) {
            this.fbo = new FrameBufferObject[1];
            this.fbo[0] = frameBufferObject;
            return (T) this;
        }

        public T fbos(FrameBufferObject... frameBufferObject) {
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

        public T rotate(float x, float y, float z) {
            modelMatrix.rotateAffineXYZ((float) Math.toRadians(x), (float) Math.toRadians(y), (float) Math.toRadians(z));
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
