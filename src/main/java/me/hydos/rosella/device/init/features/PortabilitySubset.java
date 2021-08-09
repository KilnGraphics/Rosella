package me.hydos.rosella.device.init.features;

import me.hydos.rosella.device.init.DeviceBuildConfigurator;
import me.hydos.rosella.device.init.DeviceBuildInformation;
import me.hydos.rosella.util.NamedID;
import org.lwjgl.vulkan.KHRPortabilitySubset;

public class PortabilitySubset extends SimpleApplicationFeature {

    public static final String DEVICE_EXTENSION = KHRPortabilitySubset.VK_KHR_PORTABILITY_SUBSET_EXTENSION_NAME;

    public static final NamedID NAME = new NamedID("rosella:portability_subset");

    public PortabilitySubset() {
        super(NAME, PortabilitySubset::canEnable, PortabilitySubset::enable);
    }

    private static boolean canEnable(DeviceBuildInformation meta) {
        return PhysicalDeviceProperties2.isSupported(meta.getInstance().getCapabilities()) && meta.isExtensionAvailable(DEVICE_EXTENSION);
    }

    private static void enable(DeviceBuildConfigurator meta) {
        meta.enableExtension(DEVICE_EXTENSION);
    }
}
