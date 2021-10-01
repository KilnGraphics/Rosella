package graphics.kiln.rosella.example.source;

import graphics.kiln.rosella.Rosella;
import graphics.kiln.rosella.display.GlfwWindow;
import graphics.kiln.rosella.render.texture.*;
import graphics.kiln.rosella.scene.object.GlbRenderObject;
import graphics.kiln.rosella.render.Topology;
import graphics.kiln.rosella.render.material.Material;
import graphics.kiln.rosella.render.pipeline.Pipeline;
import graphics.kiln.rosella.render.pipeline.state.StateInfo;
import graphics.kiln.rosella.render.resource.Global;
import graphics.kiln.rosella.render.resource.Identifier;
import graphics.kiln.rosella.render.resource.Resource;
import graphics.kiln.rosella.render.shader.RawShaderProgram;
import graphics.kiln.rosella.render.shader.ShaderProgram;
import graphics.kiln.rosella.render.vertex.VertexFormats;
import graphics.kiln.rosella.scene.object.impl.SimpleObjectManager;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.Configuration;
import org.lwjgl.vulkan.VK10;

import java.util.List;

/**
 * Test which contains source related stuff
 */
public class GenericSourceTest {
    public static final GlfwWindow window;
    public static final Rosella rosella;

    public static final int WIDTH = 1280;

    public static final int TOP = 720;

    public static final Matrix4f viewMatrix = new Matrix4f();
    public static final Matrix4f projectionMatrix = new Matrix4f().ortho(-WIDTH / 2f, WIDTH / 2f, -TOP / 2f, TOP / 2f, -2000f, 2000f, true);
    ;

    public static Material menuBackground;
    public static Material portalLogo;

    public static ShaderProgram basicShader;
    public static ShaderProgram guiShader;

    public static List<GlbRenderObject> spy;
    public static List<GlbRenderObject> spy2;

    public static List<GlbRenderObject> twofort;
    public static List<GlbRenderObject> engineer2;
    public static List<GlbRenderObject> engineer3;

    public static void main(String[] args) {
        // TODO: Update Assimp when non-broken version is released
        //  https://github.com/LWJGL/lwjgl3/issues/642
        if (System.getProperty("os.name").contains("Linux")) {
            Configuration.ASSIMP_LIBRARY_NAME.set("/home/haydenv/IdeaProjects/hYdos/rosella/libassimp.so"); //FIXME: LWJGL bad. LWJGL 4 when https://github.com/LWJGL/lwjgl3/issues/642
        }

        loadShaders();
        loadMaterials();
        setupMainMenuScene();
        rosella.renderer.rebuildCommandBuffers(rosella.renderer.mainRenderPass);

        window.startAutomaticLoop(rosella, () -> {

            for (GlbRenderObject glbRenderObject : engineer2) {
                glbRenderObject.modelMatrix.rotateAffineYXZ(0, 0, (float) (GLFW.glfwGetTime() * 0.001f));
            }

            for (GlbRenderObject glbRenderObject : engineer3) {
                glbRenderObject.modelMatrix.rotateAffineYXZ(0, 0, (float) (GLFW.glfwGetTime() * -0.001f));
            }

            for (GlbRenderObject glbRenderObject : spy) {
                glbRenderObject.modelMatrix.rotateAffineYXZ(0, (float) (GLFW.glfwGetTime() * -0.0005f), (float) (GLFW.glfwGetTime() * -0.001f));
            }

            for (GlbRenderObject glbRenderObject : spy2) {
                glbRenderObject.modelMatrix.rotateAffineYXZ(0, (float) (GLFW.glfwGetTime() * -0.0005f), (float) (GLFW.glfwGetTime() * 0.001f));
            }
            return true;
        });

        rosella.free();
    }

    private static void setupMainMenuScene() {
        SimpleObjectManager objectManager = rosella.common.fboManager.getObjectManager();
//
//        objectManager.addObject(
//                new GuiRenderObject(menuBackground, -1f, new Vector3f(0, 0, 0), WIDTH, -TOP, viewMatrix, projectionMatrix, new BasicUboDataProvider())
//        );
//
//        objectManager.addObject(
//                new GuiRenderObject(portalLogo, -0.9f, new Vector3f(0, 0, 0), WIDTH / 4f, -TOP / 8f, -1f, -2.6f, viewMatrix, projectionMatrix, new BasicUboDataProvider())
//        );
//
//        GlbModelLoader.NodeSelector basicTf3Nodes = (name) -> name.startsWith("lod_0_") && !name.contains("glove");
//        GlbModelLoader.NodeSelector everything = (name) -> true;
//        twofort = GlbModelLoader.createGlbRenderObject(rosella, Global.INSTANCE.ensureResource(new Identifier("example", "sourceTest/models/engineer.glb")), basicShader, VertexFormats.POSITION_COLOR3f_UV0, basicTf3Nodes, viewMatrix, projectionMatrix);
//        engineer2 = GlbModelLoader.createGlbRenderObject(rosella, Global.INSTANCE.ensureResource(new Identifier("example", "sourceTest/models/engineer.glb")), basicShader, VertexFormats.POSITION_COLOR3f_UV0, basicTf3Nodes, viewMatrix, projectionMatrix);
//        engineer3 = GlbModelLoader.createGlbRenderObject(rosella, Global.INSTANCE.ensureResource(new Identifier("example", "sourceTest/models/engineer.glb")), basicShader, VertexFormats.POSITION_COLOR3f_UV0, basicTf3Nodes, viewMatrix, projectionMatrix);
//        spy = GlbModelLoader.createGlbRenderObject(rosella, Global.INSTANCE.ensureResource(new Identifier("example", "sourceTest/models/spy.glb")), basicShader, VertexFormats.POSITION_COLOR3f_UV0, everything, viewMatrix, projectionMatrix);
//        spy2 = GlbModelLoader.createGlbRenderObject(rosella, Global.INSTANCE.ensureResource(new Identifier("example", "sourceTest/models/spy.glb")), basicShader, VertexFormats.POSITION_COLOR3f_UV0, everything, viewMatrix, projectionMatrix);

        for (GlbRenderObject subModel : twofort) {
            subModel.modelMatrix.scale(10f, 10f, 10f);
            subModel.modelMatrix.translate(0, 36, 0);
            subModel.modelMatrix.rotateAffineXYZ(-90, 0, 0);
            objectManager.addObject(subModel);
        }

        for (GlbRenderObject subModel : engineer2) {
            subModel.modelMatrix.scale(10f, 10f, 10f);
            subModel.modelMatrix.translate(40, 36, 0);
            subModel.modelMatrix.rotateAffineXYZ(-90, 0, 0);
            objectManager.addObject(subModel);
        }

        for (GlbRenderObject subModel : engineer3) {
            subModel.modelMatrix.scale(10f, 10f, 10f);
            subModel.modelMatrix.translate(-40, 36, 0);
            subModel.modelMatrix.rotateAffineXYZ(-90, 0, 0);
            objectManager.addObject(subModel);
        }

        for (GlbRenderObject subModel : spy) {
            subModel.modelMatrix.scale(10, 10, 10);
            subModel.modelMatrix.translate(20, 36, 0);
            subModel.modelMatrix.rotateAffineXYZ(-90, 0, 0);
            objectManager.addObject(subModel);
        }

        for (GlbRenderObject subModel : spy2) {
            subModel.modelMatrix.scale(10f, 10f, 10f);
            subModel.modelMatrix.translate(-20, 36, 0);
            subModel.modelMatrix.rotateAffineXYZ(-90, 0, 0);
            objectManager.addObject(subModel);
        }
    }

    private static void loadMaterials() {
        menuBackground = new Material(
                rosella.common.pipelineManager.registerPipeline(
                        new Pipeline(
                                rosella.renderer.mainRenderPass, // TODO: fix renderpasses being gross af
                                guiShader,
                                Topology.TRIANGLES,
                                VertexFormats.POSITION_COLOR3f_UV0,
                                StateInfo.DEFAULT_GUI
                        )
                ),
                ImmutableTextureMap.builder()
                        .entry("texSampler", loadTexture(
                                VK10.VK_FORMAT_R8G8B8A8_UNORM, // TODO: maybe make this srgb
                                new SamplerCreateInfo(TextureFilter.NEAREST, WrapMode.REPEAT),
                                Global.INSTANCE.ensureResource(new Identifier("example/sourceTest", "textures/background/background01.png"))
                        ))
                        .build()
        );

        portalLogo = new Material(
                rosella.common.pipelineManager.registerPipeline(
                        new Pipeline(
                                rosella.renderer.mainRenderPass, // TODO: fix renderpasses being gross af
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
                                Global.INSTANCE.ensureResource(new Identifier("example/sourceTest", "textures/gui/portal2logo.png"))
                        ))
                        .build()
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

    private static void loadShaders() {
        SimpleObjectManager objectManager = rosella.common.fboManager.getObjectManager();

        basicShader = objectManager.addShader(
                new RawShaderProgram(
                        Global.INSTANCE.ensureResource(new Identifier("rosella", "shaders/water.v.glsl")),
                        Global.INSTANCE.ensureResource(new Identifier("rosella", "shaders/water.f.glsl")),
                        rosella.common.device,
                        rosella.common.memory,
                        10240,
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

    static {
        try {
            System.loadLibrary("renderdoc");
        } catch (UnsatisfiedLinkError e) {
            System.out.println("Failed to load renderdoc.");
        }

        window = new GlfwWindow(WIDTH, TOP, "Portal / Team Fortress 3: Java Edition", true);
        rosella = new Rosella(window, "Portal 3", true);
    }
}
