package graphics.kiln.rosella.init.features;

import graphics.kiln.rosella.render.swapchain.Swapchain;
import graphics.kiln.rosella.render.swapchain.SwapchainSupportDetails;
import graphics.kiln.rosella.vkobjects.VkCommon;
import graphics.kiln.rosella.device.QueueFamilyIndices;
import graphics.kiln.rosella.device.VulkanDevice;
import graphics.kiln.rosella.device.VulkanQueue;
import graphics.kiln.rosella.init.DeviceBuildConfigurator;
import graphics.kiln.rosella.init.DeviceBuildInformation;
import graphics.kiln.rosella.util.NamedID;
import graphics.kiln.rosella.util.VkUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSwapchain;

import java.util.concurrent.Future;

/**
 * Configures the device to run the legacy rosella engine.
 */
public class RosellaLegacy extends ApplicationFeature {

    public static final NamedID NAME = new NamedID("rosella:legacy");

    private final VkCommon common;

    public RosellaLegacy(VkCommon common) {
        super(NAME);
        this.common = common;
    }

    @Override
    public RosellaLegacyInstance createInstance() {
        return new RosellaLegacyInstance();
    }

    public class RosellaLegacyInstance extends ApplicationFeature.Instance {

        private QueueFamilyIndices queueFamilyIndices;

        @Override
        public void testFeatureSupport(DeviceBuildInformation meta) {
            canEnable = false;

            queueFamilyIndices = VkUtils.findQueueFamilies(meta.getPhysicalDevice(), common.surface);

            try (MemoryStack stack = MemoryStack.stackPush()) {
                boolean swapChainAdequate;
                boolean featureSupported;

                SwapchainSupportDetails swapchainSupport = Swapchain.Companion.querySwapchainSupport(meta.getPhysicalDevice(), stack, common.surface);
                swapChainAdequate = swapchainSupport.formats.hasRemaining() && swapchainSupport.presentModes.hasRemaining();
                featureSupported = meta.getDeviceFeatures().getVk10Features().samplerAnisotropy();

                canEnable = queueFamilyIndices.isComplete() && swapChainAdequate && featureSupported && meta.isExtensionAvailable(KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME);
            }
        }

        @Override
        public Object enableFeature(DeviceBuildConfigurator meta) {
            meta.configureDeviceFeatures().getVk10Features()
                    .samplerAnisotropy(true)
                    .depthClamp(true);

            meta.enableExtension(KHRSwapchain.VK_KHR_SWAPCHAIN_EXTENSION_NAME);

            Future<VulkanQueue> graphicsRequest = meta.addQueueRequest(queueFamilyIndices.graphicsFamily);
            Future<VulkanQueue> presentRequest = meta.addQueueRequest(queueFamilyIndices.presentFamily);
            return new RosellaLegacyFeatures(graphicsRequest, presentRequest);
        }
    }

    public static RosellaLegacyFeatures getMetadata(VulkanDevice device) {
        Object o = device.getFeatureMeta(NAME);

        if(o == null) {
            return null;
        }

        if(!(o instanceof RosellaLegacyFeatures)) {
            throw new RuntimeException("Meta object could not be cast to RosellaLegacyFeatures");
        }

        return (RosellaLegacyFeatures) o;
    }

    public record RosellaLegacyFeatures(Future<VulkanQueue> graphicsQueue, Future<VulkanQueue> presentQueue) {
    }
}
