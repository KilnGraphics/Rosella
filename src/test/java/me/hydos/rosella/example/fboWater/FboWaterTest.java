package me.hydos.rosella.example.fboWater;

import me.hydos.rosella.Rosella;
import me.hydos.rosella.display.GlfwWindow;
import me.hydos.rosella.file.model.GlbModelLoader;
import me.hydos.rosella.file.model.GlbRenderObject;
import me.hydos.rosella.render.pipeline.state.StateInfo;
import me.hydos.rosella.render.resource.Global;
import me.hydos.rosella.render.resource.Identifier;
import me.hydos.rosella.render.shader.RawShaderProgram;
import me.hydos.rosella.render.shader.ShaderProgram;
import me.hydos.rosella.render.vertex.VertexFormats;
import me.hydos.rosella.scene.object.impl.SimpleObjectManager;
import me.hydos.rosella.test_utils.NoclipCamera;
import me.hydos.rosella.util.Color;
import org.joml.Matrix4f;
import org.lwjgl.system.Configuration;

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

    public static NoclipCamera camera = new NoclipCamera();

    public static void main(String[] args) {
        // TODO: Update Assimp when non-broken version is released
        //  https://github.com/LWJGL/lwjgl3/issues/642
        if (System.getProperty("os.name").contains("Linux")) {
            Configuration.ASSIMP_LIBRARY_NAME.set("/home/haydenv/IdeaProjects/hYdos/rosella/libassimp.so"); //FIXME: LWJGL bad. LWJGL 4 when https://github.com/LWJGL/lwjgl3/issues/642
        }

        loadShaders();
        loadMaterials();
        setupMainMenuScene();
        rosella.renderer.rebuildCommandBuffers(rosella.renderer.mainRenderPass, (SimpleObjectManager) rosella.objectManager);

        camera.setup(window.pWindow);
        window.startAutomaticLoop(rosella, () -> {
            camera.update();
            return true;
        });

        rosella.free();
    }

    private static void setupMainMenuScene() {
        rosella.renderer.lazilyClearColor(new Color(10, 20, 200, 255));
        GlbModelLoader.NodeSelector everything = (name) -> true;

        terrainScene = GlbModelLoader.createGlbRenderObject(rosella, Global.INSTANCE.ensureResource(new Identifier("example", "waterFboTest/scene.glb")), normalShader, VertexFormats.POSITION_NORMAL_UV0, everything, camera.viewMatrix, projectionMatrix, StateInfo.NO_CULL_3D);
        skybox = GlbModelLoader.createGlbRenderObject(rosella, Global.INSTANCE.ensureResource(new Identifier("example", "shared/skybox.glb")), skyboxShader, VertexFormats.POSITION_NORMAL_UV0, everything, camera.viewMatrix, projectionMatrix, StateInfo.NO_CULL_3D).get(0);

//        skybox.modelMatrix.rotateAffineXYZ((float) Math.toRadians(180), 0, 0);
        skybox.modelMatrix.scale(10);
        rosella.objectManager.addObject(skybox);

        for (GlbRenderObject subModel : terrainScene) {
//            subModel.modelMatrix.rotateAffineXYZ((float) Math.toRadians(180), 0, 0);
            rosella.objectManager.addObject(subModel);
        }
    }

    private static void loadMaterials() {
//        portalLogo = new Material(
//                ((SimpleObjectManager) rosella.objectManager).pipelineManager.registerPipeline(
//                        new Pipeline(
//                                rosella.renderer.mainRenderPass,
//                                guiShader,
//                                Topology.TRIANGLES,
//                                PolygonMode.FILL,
//                                VertexFormats.POSITION_COLOR3f_UV0,
//                                StateInfo.DEFAULT_GUI
//                        )
//                ),
//                ImmutableTextureMap.builder()
//                        .entry("texSampler", loadTexture(
//                                VK10.VK_FORMAT_R8G8B8A8_SRGB,
//                                new SamplerCreateInfo(TextureFilter.NEAREST, WrapMode.REPEAT),
//                                Global.INSTANCE.ensureResource(new Identifier("example", "textures/gui/portal2logo.png"))
//                        ))
//                        .build()
//        );
    }

    private static void loadShaders() {
        basicShader = rosella.objectManager.addShader(
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

        normalShader = rosella.objectManager.addShader(
                new RawShaderProgram(
                        Global.INSTANCE.ensureResource(new Identifier("rosella", "shaders/shading.v.glsl")),
                        Global.INSTANCE.ensureResource(new Identifier("rosella", "shaders/shading.f.glsl")),
                        rosella.common.device,
                        rosella.common.memory,
                        1024,
                        RawShaderProgram.PoolUboInfo.INSTANCE,
                        new RawShaderProgram.PoolSamplerInfo(-1, "texSampler")
                )
        );

        skyboxShader = rosella.objectManager.addShader(
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

        guiShader = rosella.objectManager.addShader(
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
