package me.hydos.rosella.render.material;

import me.hydos.rosella.Rosella;
import me.hydos.rosella.render.Topology;
import me.hydos.rosella.render.material.state.StateInfo;
import me.hydos.rosella.render.resource.Resource;
import me.hydos.rosella.render.shader.ShaderProgram;
import me.hydos.rosella.render.texture.*;
import me.hydos.rosella.render.vertex.VertexFormat;
import me.hydos.rosella.scene.object.impl.SimpleObjectManager;

import static org.lwjgl.vulkan.VK10.VK_FORMAT_R8G8B8A8_SRGB;
import static org.lwjgl.vulkan.VK10.VK_FORMAT_R8G8B8A8_UNORM;

/**
 * A Material is like texture information, normal information, and all of those things which give an object character wrapped into one class.
 * similar to how unity material's works
 * guaranteed to change in the future
 */
public class Material {

    @Deprecated
    protected final Resource resource;
    protected UploadableImage image;
    protected final ShaderProgram shader;
    protected final ImageFormat imgFormat;
    protected final Topology topology;
    public final VertexFormat vertexFormat;
    protected final SamplerCreateInfo samplerCreateInfo;
    protected final StateInfo stateInfo;

    public Material(UploadableImage image, ShaderProgram shader, ImageFormat imgFormat, Topology topology, VertexFormat vertexFormat, SamplerCreateInfo samplerCreateInfo, StateInfo stateInfo) {
        this((Resource) null, shader, imgFormat, topology, vertexFormat, samplerCreateInfo, stateInfo);
        this.image = image;
    }

    @Deprecated
    public Material(Resource resource, ShaderProgram shader, int imgFormat, Topology topology, VertexFormat vertexFormat, SamplerCreateInfo samplerCreateInfo, StateInfo stateInfo) {
        this(resource, shader, ImageFormat.fromVkFormat(imgFormat), topology, vertexFormat, samplerCreateInfo, stateInfo);
    }

    @Deprecated
    public Material(Resource resource, ShaderProgram shader, ImageFormat imgFormat, Topology topology, VertexFormat vertexFormat, SamplerCreateInfo samplerCreateInfo, StateInfo stateInfo) {
        this.resource = resource;
        this.shader = shader;
        this.imgFormat = imgFormat;
        this.topology = topology;
        this.vertexFormat = vertexFormat;
        this.samplerCreateInfo = samplerCreateInfo;
        this.stateInfo = stateInfo;
    }

    protected PipelineInfo pipeline;
    protected Texture[] textures;

    public void loadTextures(SimpleObjectManager objectManager, Rosella rosella) { //FIXME this is also temporary
        if (resource != Resource.Empty.INSTANCE) {
            TextureManager textureManager = objectManager.textureManager;
            int textureId = textureManager.generateTextureId(); // FIXME this texture can't be removed

            UploadableImage image = this.image;

            // Hack to fix the fact I need to keep in old code for Blaze4D Compatability -hydos
            if (this.image == null) {
                image = new StbiImage(resource, imgFormat);
            }

            textureManager.createTexture(
                    rosella.renderer,
                    textureId,
                    image.getWidth(),
                    image.getHeight(),
                    VK_FORMAT_R8G8B8A8_UNORM
            );
            textureManager.setTextureSampler(
                    textureId,
                    0,
                    samplerCreateInfo
            ); // 0 is the default texture no., but it's still gross
            textureManager.drawToExistingTexture(rosella.renderer, textureId, image);
            Texture texture = textureManager.getTexture(textureId);
            textures = new Texture[]{texture}; //FIXME THIS SUCKS. Yes, indeed it does
        }
    }

    public ShaderProgram getShader() {
        return shader;
    }

    public PipelineInfo getPipeline() {
        return pipeline;
    }

    public void setPipeline(PipelineInfo pipeline) {
        this.pipeline = pipeline;
    }

    public Texture[] getTextures() {
        if(textures == null) {
            throw new RuntimeException("ENGINE STATE IS BROKEN! (Material Submitted yet textures dont exist?)");
        }
        return textures;
    }
}

