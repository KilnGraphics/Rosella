package me.hydos.rosella.vkobjects;

import me.hydos.rosella.device.VulkanDevice;
import me.hydos.rosella.device.VulkanQueues;
import me.hydos.rosella.display.Display;
import me.hydos.rosella.memory.Memory;
import me.hydos.rosella.render.fbo.FboManager;
import me.hydos.rosella.render.pipeline.PipelineManager;
import me.hydos.rosella.render.shader.ShaderManager;
import me.hydos.rosella.render.texture.TextureManager;
import me.hydos.rosella.util.SemaphorePool;

/**
 * Common fields shared within the {@link me.hydos.rosella.Rosella} instance. sharing this info with other instances of the engine is extremely unsafe.
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
