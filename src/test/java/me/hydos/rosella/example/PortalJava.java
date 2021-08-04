package me.hydos.rosella.example;

import me.hydos.rosella.Rosella;
import me.hydos.rosella.display.GlfwWindow;
import me.hydos.rosella.file.model.GlbModelLoader;
import me.hydos.rosella.file.model.GlbRenderObject;
import me.hydos.rosella.render.Topology;
import me.hydos.rosella.render.material.Material;
import me.hydos.rosella.render.material.state.StateInfo;
import me.hydos.rosella.render.model.GuiRenderObject;
import me.hydos.rosella.render.resource.Global;
import me.hydos.rosella.render.resource.Identifier;
import me.hydos.rosella.render.shader.RawShaderProgram;
import me.hydos.rosella.render.shader.ShaderProgram;
import me.hydos.rosella.render.texture.SamplerCreateInfo;
import me.hydos.rosella.render.texture.TextureFilter;
import me.hydos.rosella.render.texture.WrapMode;
import me.hydos.rosella.render.vertex.VertexFormats;
import me.hydos.rosella.scene.object.impl.SimpleObjectManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.Configuration;
import org.lwjgl.vulkan.VK10;

public class PortalJava {
    public static final GlfwWindow window;
    public static final Rosella rosella;

    public static final int WIDTH = 1280;

    public static final int TOP = 720;

    static {
//        System.loadLibrary("renderdoc");
        window = new GlfwWindow(WIDTH, TOP, "Portal 3: Java Edition", true);
        rosella = new Rosella(window, "Portal 3", true);
    }

    public static final Matrix4f viewMatrix = new Matrix4f();
    public static final Matrix4f projectionMatrix = new Matrix4f().ortho(-WIDTH / 2f, WIDTH / 2f, -TOP / 2f, TOP / 2f, -100, 1000);

    public static Material menuBackground;
    public static Material portalLogo;

    public static ShaderProgram basicShader;
    public static ShaderProgram guiShader;

    public static void main(String[] args) {
        Configuration.ASSIMP_LIBRARY_NAME.set("/home/haydenv/IdeaProjects/hYdos/rosella/libassimp.so"); //FIXME: LWJGL bad. LWJGL 4 when https://github.com/LWJGL/lwjgl3/issues/642
        loadShaders();
        loadMaterials();
        setupMainMenuScene();
        rosella.renderer.rebuildCommandBuffers(rosella.renderer.renderPass, (SimpleObjectManager) rosella.objectManager);
//        rosella.renderer.queueRecreateSwapchain(); FIXME: # C  [libVkLayer_khronos_validation.so+0xe16204]  CoreChecks::ValidateMemoryIsBoundToBuffer(BUFFER_STATE const*, char const*, char const*) const+0x14
        window.startAutomaticLoop(rosella);
    }

    private static void setupMainMenuScene() {
        rosella.objectManager.addObject(
                new GuiRenderObject(menuBackground, -1f, new Vector3f(0, 0, 0), WIDTH, -TOP, viewMatrix, projectionMatrix)
        );

        rosella.objectManager.addObject(
                new GuiRenderObject(portalLogo, -0.9f, new Vector3f(0, 0, 0), WIDTH / 4f, -TOP / 8f, -1f, -2.6f, viewMatrix, projectionMatrix)
        );

        GlbModelLoader.NodeSelector basicTf3Nodes = (name) -> name.startsWith("lod_0_") && !name.contains("glove");
        for (GlbRenderObject subModel : GlbModelLoader.createGlbRenderObject(rosella, Global.INSTANCE.ensureResource(new Identifier("example", "models/engineer.glb")), basicShader, basicTf3Nodes, viewMatrix, projectionMatrix)) {
            subModel.modelMatrix.scale(10, 10, 10);
            subModel.modelMatrix.translate(0, 36, 0);
            subModel.modelMatrix.rotateAffineXYZ(-90, 0, 0);
            rosella.objectManager.addObject(subModel);
        }
    }

    private static void loadMaterials() {
        menuBackground = rosella.objectManager.registerMaterial(
                new Material(
                        Global.INSTANCE.ensureResource(new Identifier("example", "textures/background/background01.png")),
                        guiShader,
                        VK10.VK_FORMAT_R8G8B8A8_UNORM,
                        Topology.TRIANGLES,
                        VertexFormats.POSITION_COLOUR3_UV0,
                        new SamplerCreateInfo(TextureFilter.NEAREST, WrapMode.REPEAT),
                        StateInfo.DEFAULT_GUI
                )
        );

        portalLogo = rosella.objectManager.registerMaterial(
                new Material(
                        Global.INSTANCE.ensureResource(new Identifier("example", "textures/gui/portal2logo.png")),
                        guiShader,
                        VK10.VK_FORMAT_R8G8B8A8_SRGB,
                        Topology.TRIANGLES,
                        VertexFormats.POSITION_COLOUR3_UV0,
                        new SamplerCreateInfo(TextureFilter.NEAREST, WrapMode.REPEAT),
                        StateInfo.DEFAULT_GUI
                )
        );

        rosella.objectManager.submitMaterials();
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
                        new RawShaderProgram.PoolSamplerInfo(-1, 0)
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
                        new RawShaderProgram.PoolSamplerInfo(-1, 0)
                )
        );
    }
}
