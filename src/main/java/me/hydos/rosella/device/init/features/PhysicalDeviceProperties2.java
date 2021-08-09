package me.hydos.rosella.device.init.features;

import me.hydos.rosella.device.init.InitializationRegistry;
import org.lwjgl.vulkan.KHRGetPhysicalDeviceProperties2;
import org.lwjgl.vulkan.VKCapabilitiesInstance;

public class PhysicalDeviceProperties2 {

    public static final String INSTANCE_EXTENSION = KHRGetPhysicalDeviceProperties2.VK_KHR_GET_PHYSICAL_DEVICE_PROPERTIES_2_EXTENSION_NAME;

    public static void addInstanceExtension(InitializationRegistry registry) {
        registry.addOptionalInstanceExtension(INSTANCE_EXTENSION);
    }

    public static boolean isSupported(VKCapabilitiesInstance capabilitiesInstance) {
        return capabilitiesInstance.VK_KHR_get_physical_device_properties2;
    }
}
