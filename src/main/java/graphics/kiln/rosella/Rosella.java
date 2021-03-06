package graphics.kiln.rosella;

import graphics.kiln.rosella.device.VulkanQueues;
import graphics.kiln.rosella.init.DeviceBuilder;
import graphics.kiln.rosella.init.InitializationRegistry;
import graphics.kiln.rosella.init.VulkanInstance;
import graphics.kiln.rosella.render.util.SprirVUtilsKt;
import graphics.kiln.rosella.init.features.*;
import graphics.kiln.rosella.display.Display;
import graphics.kiln.rosella.logging.DebugLogger;
import graphics.kiln.rosella.logging.DefaultDebugLogger;
import graphics.kiln.rosella.memory.ThreadPoolMemory;
import graphics.kiln.rosella.memory.buffer.GlobalBufferManager;
import graphics.kiln.rosella.render.pipeline.PipelineManager;
import graphics.kiln.rosella.render.renderer.Renderer;
import graphics.kiln.rosella.render.shader.ShaderManager;
import graphics.kiln.rosella.render.texture.TextureManager;
import graphics.kiln.rosella.scene.object.impl.SimpleObjectManager;
import graphics.kiln.rosella.util.SemaphorePool;
import graphics.kiln.rosella.vkobjects.LegacyVulkanInstance;
import graphics.kiln.rosella.vkobjects.VkCommon;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.StringFormatterMessageFactory;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkLayerProperties;

import java.nio.IntBuffer;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static graphics.kiln.rosella.util.VkUtils.ok;
import static org.lwjgl.vulkan.EXTDebugUtils.vkDestroyDebugUtilsMessengerEXT;
import static org.lwjgl.vulkan.KHRSurface.vkDestroySurfaceKHR;
import static org.lwjgl.vulkan.VK10.vkDestroyCommandPool;
import static org.lwjgl.vulkan.VK10.vkEnumerateInstanceLayerProperties;
import static org.lwjgl.vulkan.VK12.VK_API_VERSION_1_2;

/**
 * Main Rosella class. If you're interacting with the engine from here, You will most likely be safe.
 */
public class Rosella {

    public static final Logger LOGGER = LogManager.getLogger("Rosella", new StringFormatterMessageFactory());
    public static int GENERIC_ERROR_CODE = -10;
    public static int FBO_ERROR_CODE = -11;
    public static int LOOP_FAIL_ERROR_CODE = -12;
    public static final int VULKAN_VERSION = VK_API_VERSION_1_2;
    public final GlobalBufferManager bufferManager;
    public final VkCommon common = new VkCommon();
    public final Renderer renderer;
    public final SimpleObjectManager baseObjectManager;

    public final VulkanInstance vulkanInstance;

    //FIXME: i don't see why we have this. it is alot of duplicated code too. just leaving it out for now to make my life a bit easier
/*    public Rosella(InitializationRegistry registry, Display display, String applicationName, int applicationVersion) {
        SprirVUtilsKt.init();
        registry.enableValidation(true);

        common.display = display;
        display.getRequiredExtensions().forEach(registry::addRequiredInstanceExtensions);
        PhysicalDeviceProperties2.addInstanceExtension(registry); // Required to detect triangle fan support

        // Needed because debug callbacks are handled by LegacyVulkanInstance. TODO remove this
        common.vkInstance = new LegacyVulkanInstance(registry, applicationName, applicationVersion, new DefaultDebugLogger());
        this.vulkanInstance = common.vkInstance.newInstance;

        common.surface = display.createSurface(common);
        registry.registerApplicationFeature(new RosellaLegacy(common));
        registry.addRequiredApplicationFeature(RosellaLegacy.NAME);

        registry.registerApplicationFeature(new PortabilitySubset()); // Required to detect triangle fan support
        registry.registerApplicationFeature(new TriangleFan());

        common.device = new DeviceBuilder(this.vulkanInstance, registry).build();

        RosellaLegacy.RosellaLegacyFeatures legacyFeatures = RosellaLegacy.getMetadata(common.device);
        try {
            common.queues = new VulkanQueues(legacyFeatures.graphicsQueue().get(), legacyFeatures.presentQueue().get());
        } catch (Exception ex) {
            throw new RuntimeException("Not good stuff.");
        }

        // TODO: Tons and tons of old code. Need to remove
        common.memory = new ThreadPoolMemory(common);
        common.semaphorePool = new SemaphorePool(common.device.getRawDevice());

        this.baseObjectManager = new SimpleObjectManager(this, common);
        this.renderer = new Renderer(this);
        this.common.textureManager.initializeBlankTexture(renderer);
        this.baseObjectManager.postInit(renderer);
        this.bufferManager = new GlobalBufferManager(this);

        display.onReady();
    }*/

    @Deprecated
    public Rosella(Display display, String applicationName, boolean enableBasicValidation) {
        this(display, enableBasicValidation ? Collections.singletonList(ValidationLayers.INSTANCE_LAYER_NAME) : Collections.emptyList(), applicationName, new DefaultDebugLogger());
    }

    @Deprecated
    public Rosella(Display display, List<String> requestedValidationLayers, String applicationName, DebugLogger debugLogger) {
        SprirVUtilsKt.init();
        List<String> requiredExtensions = display.getRequiredExtensions();

        InitializationRegistry initializationRegistry = new InitializationRegistry();
        requestedValidationLayers.forEach(initializationRegistry::addRequiredInstanceLayer);
        requiredExtensions.forEach(initializationRegistry::addRequiredInstanceExtension);
        PhysicalDeviceProperties2.addInstanceExtension(initializationRegistry); // Required to detect triangle fan support

        // Setup core vulkan stuff
        common.display = display;
        common.vkInstance = new LegacyVulkanInstance(initializationRegistry, applicationName, VK10.VK_MAKE_VERSION(1, 0, 0), debugLogger);
        this.vulkanInstance = common.vkInstance.newInstance;

        common.surface = display.createSurface(common);
        initializationRegistry.registerApplicationFeature(new RosellaLegacy(common));

        initializationRegistry.registerApplicationFeature(new PortabilitySubset()); // Required to detect triangle fan support
        initializationRegistry.registerApplicationFeature(new TriangleFan());
        initializationRegistry.registerApplicationFeature(new ImagelessFrameBuffers()); // Absolutely Required for Multiple FBO's to work
        initializationRegistry.registerApplicationFeature(new GlClipDistance()); // Required for water tests to work.

        common.device = new DeviceBuilder(this.vulkanInstance, initializationRegistry).build();

        RosellaLegacy.RosellaLegacyFeatures legacyFeatures = RosellaLegacy.getMetadata(common.device);
        try {
            common.queues = new VulkanQueues(legacyFeatures.graphicsQueue().get(), legacyFeatures.presentQueue().get());
        } catch (Exception ex) {
            throw new RuntimeException("Not good stuff.");
        }

        common.memory = new ThreadPoolMemory(common);
        common.semaphorePool = new SemaphorePool(common.device.getRawDevice());

        // Setup the object manager
        this.baseObjectManager = new SimpleObjectManager(this, common);
        this.common.shaderManager = new ShaderManager(this);
        this.common.textureManager = new TextureManager(common);
        this.renderer = new Renderer(this); //TODO: make swapchain, etc initialization happen outside of the renderer and in here
        this.common.textureManager.initializeBlankTexture(renderer); // TODO: move this maybe
        this.baseObjectManager.postInit(renderer);
        this.common.pipelineManager = new PipelineManager(common, renderer);
        this.bufferManager = new GlobalBufferManager(this);

        // Tell the display we are initialized
        display.onReady();
    }

    /**
     * Free's the vulkan resources.
     */
    public void free() {
        SprirVUtilsKt.free();
        common.device.waitForIdle();
        baseObjectManager.free();
        common.shaderManager.free();
        common.textureManager.free();
        common.pipelineManager.free();
        bufferManager.free();
        renderer.free();

        // Free the rest of it
        common.memory.free();
        common.semaphorePool.free();

        vkDestroyCommandPool(common.device.getRawDevice(), renderer.commandPool, null);

        common.device.destroy();

        vkDestroySurfaceKHR(common.vkInstance.rawInstance, common.surface, null);

        common.vkInstance.messenger.ifPresent(messenger -> { // FIXME
            vkDestroyDebugUtilsMessengerEXT(common.vkInstance.rawInstance, messenger, null);
        });

        vulkanInstance.destroy();

        common.display.exit();
    }

    /**
     * Checks if the system supports validation layers.
     *
     * @param requestedValidationLayers the validation layers requested by the application/user
     * @return if the system supports the request validation layers.
     */
    private boolean validationLayersSupported(List<String> requestedValidationLayers) {
        return getSupportedValidationLayers().containsAll(requestedValidationLayers);
    }

    /**
     * Gets all validation layers supported by the machine
     *
     * @return all validation layers that are supported
     */
    private Set<String> getSupportedValidationLayers() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer pLayerCount = stack.ints(0);
            ok(vkEnumerateInstanceLayerProperties(pLayerCount, null));
            VkLayerProperties.Buffer availableLayers = VkLayerProperties.mallocStack(pLayerCount.get(0), stack);
            ok(vkEnumerateInstanceLayerProperties(pLayerCount, availableLayers));
            return availableLayers.stream()
                    .map(VkLayerProperties::layerNameString)
                    .collect(Collectors.toSet());
        }

    }

    static {
        LOGGER.atLevel(Level.ALL);
    }
}
