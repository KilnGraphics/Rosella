package graphics.kiln.rosella.init.features;

import graphics.kiln.rosella.init.DeviceBuildInformation;
import graphics.kiln.rosella.util.NamedID;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRGetPhysicalDeviceProperties2;
import org.lwjgl.vulkan.KHRPortabilitySubset;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures2;
import org.lwjgl.vulkan.VkPhysicalDevicePortabilitySubsetFeaturesKHR;

public class TriangleFan extends SimpleApplicationFeature {

    public static final NamedID NAME = new NamedID("rosella:triangle_fan");

    public TriangleFan() {
        // we don't want to depend on PortabilitySubset because we default to true if it doesn't exist
        super(NAME, TriangleFan::canEnable, null);
    }

    private static boolean canEnable(DeviceBuildInformation meta) {
        if (meta.isApplicationFeatureSupported(DeviceExtensionFeature.getIdFromExtensionName(PortabilitySubset.DEVICE_EXTENSION))) {
            try (MemoryStack stack = MemoryStack.stackPush()) {
                VkPhysicalDevicePortabilitySubsetFeaturesKHR portabilitySubsetFeatures = VkPhysicalDevicePortabilitySubsetFeaturesKHR.calloc(stack)
                        .sType(KHRPortabilitySubset.VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PORTABILITY_SUBSET_FEATURES_KHR);

                VkPhysicalDeviceFeatures2 features2 = VkPhysicalDeviceFeatures2.callocStack(stack)
                        .sType(KHRGetPhysicalDeviceProperties2.VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2_KHR)
                        .pNext(portabilitySubsetFeatures.address())
                        .features(meta.getDeviceFeatures().getVk10Features());

                KHRGetPhysicalDeviceProperties2.vkGetPhysicalDeviceFeatures2KHR(meta.getPhysicalDevice(), features2);

                return portabilitySubsetFeatures.triangleFans();
            }
        } else {
            // the device probably supports it if it doesn't have the extension
            return true;
        }
    }
}
