package me.hydos.rosella.device.init.features;

import me.hydos.rosella.device.init.DeviceBuildInformation;
import org.lwjgl.vulkan.KHRPortabilitySubset;

public class PortabilitySubset extends DeviceExtensionFeature {

    public static final String DEVICE_EXTENSION = KHRPortabilitySubset.VK_KHR_PORTABILITY_SUBSET_EXTENSION_NAME;

    public PortabilitySubset() {
        super(DEVICE_EXTENSION, PortabilitySubset::canEnable, null);
    }

    private static boolean canEnable(DeviceBuildInformation meta) {
        return PhysicalDeviceProperties2.isSupported(meta.getInstance().getCapabilities());
    }
}
