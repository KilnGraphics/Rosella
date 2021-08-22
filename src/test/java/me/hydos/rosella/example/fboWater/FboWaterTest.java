package me.hydos.rosella.example.fboWater;

import me.hydos.rosella.Rosella;
import me.hydos.rosella.display.GlfwWindow;
import me.hydos.rosella.example.fboWater.ubo.ClipPlaneUboDataProvider;
import me.hydos.rosella.render.Topology;
import me.hydos.rosella.render.fbo.FrameBufferObject;
import me.hydos.rosella.render.material.Material;
import me.hydos.rosella.render.pipeline.Pipeline;
import me.hydos.rosella.render.pipeline.state.StateInfo;
import me.hydos.rosella.render.resource.Global;
import me.hydos.rosella.render.resource.Identifier;
import me.hydos.rosella.render.resource.Resource;
import me.hydos.rosella.render.shader.RawShaderProgram;
import me.hydos.rosella.render.shader.ShaderProgram;
import me.hydos.rosella.render.texture.*;
import me.hydos.rosella.render.vertex.VertexFormats;
import me.hydos.rosella.scene.object.GlbRenderObject;
import me.hydos.rosella.scene.object.TexturedGuiRenderObject;
import me.hydos.rosella.scene.object.impl.SimpleObjectManager;
import me.hydos.rosella.test_utils.NoclipCamera;
import me.hydos.rosella.ubo.BasicUboDataProvider;
import me.hydos.rosella.util.Color;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.system.Configuration;
import org.lwjgl.vulkan.VK10;

import java.util.List;

/**
 * Test which contains source related stuff
 */
public class FboWaterTest {
    public static final GlfwWindow window;
    public static final Rosella rosella;

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;
    public static final int FOV = 90;

    public static final Matrix4f projectionMatrix = new Matrix4f().perspective((float) Math.toRadians(FOV), (float) WIDTH / (float) HEIGHT, 0.1f, 4000f, true);

    public static ShaderProgram basicShader;
    public static ShaderProgram normalShader;
    public static ShaderProgram skyboxShader;
    public static ShaderProgram guiShader;

    public static List<GlbRenderObject> terrainScene;
    public static GlbRenderObject skybox;
    public static GlbRenderObject waterQuad;
    public static TexturedGuiRenderObject fboOverlay;

    public static Material fboOverlayTexture;

    public static FrameBufferObject mainFbo;
    public static FrameBufferObject secondFbo;

    public static NoclipCamera camera = new NoclipCamera();

    public static void main(String[] args) {
        // TODO: Update Assimp when non-broken version is released
        //  https://github.com/LWJGL/lwjgl3/issues/642
        if (System.getProperty("os.name").contains("Linux")) {
            Configuration.ASSIMP_LIBRARY_NAME.set("/home/haydenv/IdeaProjects/hYdos/rosella/libassimp.so"); //FIXME: LWJGL bad. LWJGL 4 when https://github.com/LWJGL/lwjgl3/issues/642
        }

        mainFbo = rosella.common.fboManager.getActiveFbo();
        secondFbo = rosella.common.fboManager.addFbo(new FrameBufferObject(false, rosella.renderer.swapchain, rosella.common, rosella.renderer.mainRenderPass, rosella.renderer, rosella.baseObjectManager));

        loadShaders();
        loadMaterials();
        setupMainMenuScene();
        rosella.renderer.rebuildCommandBuffers(rosella.renderer.mainRenderPass);

        camera.setup(window.pWindow);
        window.startAutomaticLoop(rosella, () -> {
            camera.updateMatrix();
            return true;
        });

        rosella.free();
    }

    private static void setupMainMenuScene() {
        SimpleObjectManager mainObjectManager = mainFbo.objectManager;
        SimpleObjectManager waterFboObjectManager = secondFbo.objectManager;

        rosella.renderer.lazilyClearColor(new Color(0, 0, 0, 0));

        terrainScene = new GlbRenderObject.Builder()
                .file(Global.INSTANCE.ensureResource(new Identifier("example", "waterFboTest/scene.glb")))
                .viewMatrix(camera.viewMatrix)
                .projectionMatrix(projectionMatrix)
                .stateInfo(StateInfo.NO_CULL_3D)
                .shader(normalShader)
                .uboDataProvider(new ClipPlaneUboDataProvider(new Vector4f(0, 1, 0, 15)))
                .build(rosella);
        for (GlbRenderObject subModel : terrainScene) {
            waterFboObjectManager.addObject(subModel);
            mainObjectManager.addObject(subModel);
        }

        skybox = new GlbRenderObject.Builder()
                .file(Global.INSTANCE.ensureResource(new Identifier("example", "shared/skybox.glb")))
                .viewMatrix(camera.viewMatrix)
                .projectionMatrix(projectionMatrix)
                .stateInfo(StateInfo.NO_CULL_3D)
                .shader(skyboxShader)
                .uboDataProvider(new BasicUboDataProvider())
                .build(rosella)
                .get(0);
        skybox.modelMatrix.scale(10);
        mainObjectManager.addObject(skybox);
        waterFboObjectManager.addObject(skybox);

        waterQuad = new GlbRenderObject.Builder()
                .file(Global.INSTANCE.ensureResource(new Identifier("example", "waterFboTest/waterQuad.glb")))
                .viewMatrix(camera.viewMatrix)
                .projectionMatrix(projectionMatrix)
                .stateInfo(StateInfo.NO_CULL_3D)
                .shader(normalShader)
                .uboDataProvider(new BasicUboDataProvider())
                .build(rosella)
                .get(0);

        TexturedGuiRenderObject fboRenderObject = new TexturedGuiRenderObject.Builder()
                .fbo(secondFbo)
                .viewMatrix(camera.viewMatrix)
                .projectionMatrix(projectionMatrix)
                .z(1f)
                .material(fboOverlayTexture) //TODO: this material gets replaced and is just a place holder until then. ideally this should be set to an "empty" material if fbo is set inside the builder
                .uboDataProvider(new BasicUboDataProvider())
                .translate(1.27777f, 0.5f)
                .build();
        mainObjectManager.addObject(fboRenderObject); // Render 2nd fbo onto a quad on the 1st fbo
    }

    private static void loadMaterials() {
        fboOverlayTexture = new Material(
                rosella.common.pipelineManager.registerPipeline(
                        new Pipeline(
                                rosella.renderer.mainRenderPass,
                                guiShader,
                                Topology.TRIANGLES,
                                VertexFormats.POSITION_COLOR3f_UV0,
                                StateInfo.DEFAULT_GUI
                        )
                ),
                ImmutableTextureMap.builder()
                        .entry("texSampler", loadTexture(
                                VK10.VK_FORMAT_R8G8B8A8_SRGB,
                                new SamplerCreateInfo(TextureFilter.NEAREST, WrapMode.REPEAT),
                                Global.INSTANCE.ensureResource(new Identifier("example/waterFboTest", "textures/thisIsAnFbo.png"))
                        ))
                        .build()
        );
    }

    private static void loadShaders() {
        SimpleObjectManager objectManager = rosella.common.fboManager.getObjectManager();

        basicShader = objectManager.addShader(
                new RawShaderProgram(
                        Global.INSTANCE.ensureResource(new Identifier("rosella", "shaders/base.v.glsl")),
                        Global.INSTANCE.ensureResource(new Identifier("rosella", "shaders/base.f.glsl")),
                        rosella.common.device,
                        rosella.common.memory,
                        1024,
                        RawShaderProgram.PoolUboInfo.INSTANCE,
                        new RawShaderProgram.PoolSamplerInfo(-1, "texSampler")
                )
        );

        normalShader = objectManager.addShader(
                new RawShaderProgram(
                        Global.INSTANCE.ensureResource(new Identifier("example", "waterFboTest/shaders/shadedWaterObject.v.glsl")),
                        Global.INSTANCE.ensureResource(new Identifier("example", "waterFboTest/shaders/shadedWaterObject.f.glsl")),
                        rosella.common.device,
                        rosella.common.memory,
                        1024,
                        RawShaderProgram.PoolUboInfo.INSTANCE,
                        new RawShaderProgram.PoolSamplerInfo(-1, "texSampler")
                )
        );

        skyboxShader = objectManager.addShader(
                new RawShaderProgram(
                        Global.INSTANCE.ensureResource(new Identifier("example", "shared/shaders/skybox.v.glsl")),
                        Global.INSTANCE.ensureResource(new Identifier("example", "shared/shaders/skybox.f.glsl")),
                        rosella.common.device,
                        rosella.common.memory,
                        2,
                        RawShaderProgram.PoolUboInfo.INSTANCE,
                        new RawShaderProgram.PoolSamplerInfo(-1, "texSampler")
                )
        );

        guiShader = objectManager.addShader(
                new RawShaderProgram(
                        Global.INSTANCE.ensureResource(new Identifier("rosella", "shaders/gui.v.glsl")),
                        Global.INSTANCE.ensureResource(new Identifier("rosella", "shaders/gui.f.glsl")),
                        rosella.common.device,
                        rosella.common.memory,
                        1024,
                        RawShaderProgram.PoolUboInfo.INSTANCE,
                        new RawShaderProgram.PoolSamplerInfo(-1, "texSampler")
                )
        );
    }

    public static Texture loadTexture(int vkImgFormat, SamplerCreateInfo samplerCreateInfo, Resource imageResource) {
        TextureManager textureManager = rosella.common.textureManager;

        if (imageResource.equals(Resource.Empty.INSTANCE)) {
            Rosella.LOGGER.error("Resource passed to loadTexture was empty, defaulting blank texture");
            return textureManager.getTexture(TextureManager.BLANK_TEXTURE_ID);
        }

        int textureId = textureManager.generateTextureId();
        UploadableImage image = new StbiImage(imageResource, ImageFormat.fromVkFormat(vkImgFormat));
        textureManager.createTexture(
                rosella.renderer,
                textureId,
                image.getWidth(),
                image.getHeight(),
                vkImgFormat
        );
        textureManager.setTextureSampler(
                textureId,
                "texSampler",
                samplerCreateInfo
        );
        textureManager.drawToExistingTexture(rosella.renderer, textureId, image);
        return textureManager.getTexture(textureId);
    }

    static {
        try {
            System.loadLibrary("renderdoc");
        } catch (UnsatisfiedLinkError e) {
            System.out.println("Failed to load renderdoc.");
        }

        window = new GlfwWindow(WIDTH, HEIGHT, "FrameBufferObject Water Test", false);
        rosella = new Rosella(window, "FBO_WATER_TEST", true);
        projectionMatrix.m11(-projectionMatrix.m11());
    }
}
