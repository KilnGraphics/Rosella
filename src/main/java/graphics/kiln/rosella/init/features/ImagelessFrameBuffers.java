package graphics.kiln.rosella.init.features;

import graphics.kiln.rosella.init.DeviceBuildConfigurator;
import graphics.kiln.rosella.init.DeviceBuildInformation;
import graphics.kiln.rosella.init.VulkanVersion;
import graphics.kiln.rosella.util.NamedID;

public class ImagelessFrameBuffers extends SimpleApplicationFeature {

    public static final NamedID NAME = new NamedID("rosella:imageless_fbos");

    public ImagelessFrameBuffers() {
        // we don't want to depend on PortabilitySubset because we default to true if it doesn't exist
        super(NAME, ImagelessFrameBuffers::canEnable, ImagelessFrameBuffers::enable);
    }

    private static void enable(DeviceBuildConfigurator deviceBuildConfigurator) {
        if (deviceBuildConfigurator.configureDeviceFeatures().getVk12Features() != null) {
            deviceBuildConfigurator.configureDeviceFeatures().getVk12Features().imagelessFramebuffer(true);
        } else {
            deviceBuildConfigurator.enableExtension("VK_KHR_imageless_framebuffer");
        }
    }

    private static boolean canEnable(DeviceBuildInformation meta) {
        // In VK1.2, it is a required feature. if this is vk 1.1 & vk 1.0, its an extension
        // TODO OPT: maybe enable the extension if VK1.1 is being used?
        if (meta.getInstance().getVersion() == VulkanVersion.VULKAN_1_2) {
            return true;
        } else {
            return meta.getAllExtensionProperties().containsKey("VK_KHR_imageless_framebuffer");
        }
    }
}
