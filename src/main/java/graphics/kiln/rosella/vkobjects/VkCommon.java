package graphics.kiln.rosella.vkobjects;

import graphics.kiln.rosella.Rosella;
import graphics.kiln.rosella.render.shader.ShaderManager;
import graphics.kiln.rosella.device.VulkanDevice;
import graphics.kiln.rosella.device.VulkanQueues;
import graphics.kiln.rosella.display.Display;
import graphics.kiln.rosella.memory.Memory;
import graphics.kiln.rosella.render.fbo.FboManager;
import graphics.kiln.rosella.render.pipeline.PipelineManager;
import graphics.kiln.rosella.render.texture.TextureManager;
import graphics.kiln.rosella.util.SemaphorePool;

/**
 * Common fields shared within the {@link Rosella} instance. sharing this info with other instances of the engine is extremely unsafe.
 */
public class VkCommon {

    /**
     * Access to the memory allocator to be used for Vulkan
     */
    public Memory memory;

    /**
     * Semaphore pool utility
     */
    public SemaphorePool semaphorePool;

    /**
     * The display used to display the window.
     */
    public Display display;

    /**
     * The instance of vulkan and the debug logger.
     */
    public LegacyVulkanInstance vkInstance;

    /**
     * The surface of what we are displaying to. In general it will be a GLFW window surface.
     */
    public long surface;

    /**
     * The logical and physical device. used in most Vulkan calls.
     */
    public VulkanDevice device;

    /**
     * The Presentation and Graphics queue.
     */
    public VulkanQueues queues;

    /**
     * Manages all FrameBuffer Objects.
     */
    public FboManager fboManager = new FboManager();

    /**
     * Manages all Shaders being used.
     */
    public ShaderManager shaderManager;

    /**
     * Manages all Textures being used.
     */
    public TextureManager textureManager;

    /**
     * Manages all Pipelines being used.
     */
    public PipelineManager pipelineManager;
}
