package me.hydos.rosella.device.init.features;

import me.hydos.rosella.device.init.DeviceBuildConfigurator;
import me.hydos.rosella.device.init.DeviceBuildInformation;
import me.hydos.rosella.device.init.VulkanVersion;
import me.hydos.rosella.util.NamedID;

public class ImagelessFrameBuffers extends SimpleApplicationFeature {

    public static final NamedID NAME = new NamedID("rosella:imageless_fbos");

    public ImagelessFrameBuffers() {
        // we don't want to depend on PortabilitySubset because we default to true if it doesn't exist
        super(NAME, ImagelessFrameBuffers::canEnable, ImagelessFrameBuffers::enable);
    }

    private static void enable(DeviceBuildConfigurator deviceBuildConfigurator) {
        deviceBuildConfigurator.configureDeviceFeatures().getVk12Features().imagelessFramebuffer(true);
    }

    private static boolean canEnable(DeviceBuildInformation meta) {
        // In VK1.2, it is a required feature. if this is vk 1.1, its an extension. in vk1.0 there is no way to use it.
        // TODO OPT: maybe enable the extension if VK1.1 is being used?
        return meta.getInstance().getVersion() == VulkanVersion.VULKAN_1_2;
    }
}
