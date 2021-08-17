package me.hydos.rosella.device.init;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures;
import org.lwjgl.vulkan.VkPhysicalDeviceFeatures2;
import org.lwjgl.vulkan.VkPhysicalDeviceVulkan11Features;
import org.lwjgl.vulkan.VkPhysicalDeviceVulkan12Features;

import static org.lwjgl.vulkan.VK11.VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2;
import static org.lwjgl.vulkan.VK12.VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_1_FEATURES;
import static org.lwjgl.vulkan.VK12.VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_2_FEATURES;

/**
 * Builds all information about features on the device and what is enabled.
 */
public class DeviceFeatureBuilder {

    private static final int VK_11 = 4198400;
    private static final int VK_12 = 4202496;
    //TODO: maybe also do VkPhysicalDeviceVulkan11Properties & VkPhysicalDeviceVulkan12Properties?
    public VkPhysicalDeviceFeatures2 vulkanFeatures;
    public VkPhysicalDeviceVulkan11Features vulkan11Features;
    public VkPhysicalDeviceVulkan12Features vulkan12Features;

    /**
     * @param stack        A {@link MemoryStack} which will be valid until this is built
     * @param vkApiVersion the Vulkan API Version
     */
    public DeviceFeatureBuilder(MemoryStack stack, int vkApiVersion) {
        //TODO SOON: add support for VK1.0 by not doing any of this on vk1.0. instead just create a simple VkPhysicalDeviceFeatures field
        vulkanFeatures = VkPhysicalDeviceFeatures2.callocStack(stack)
                .sType(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_FEATURES_2);

        switch (vkApiVersion) {
            case VK_12 -> {
                vulkan11Features = VkPhysicalDeviceVulkan11Features.callocStack(stack)
                        .sType(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_1_FEATURES);

                vulkan12Features = VkPhysicalDeviceVulkan12Features.callocStack(stack)
                        .sType(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_2_FEATURES);

                // Setup the chain
                vulkan11Features.pNext(vulkan12Features.address());
                vulkanFeatures.pNext(vulkan11Features.address());
            }

            case VK_11 -> {
                vulkan11Features = VkPhysicalDeviceVulkan11Features.callocStack(stack)
                        .sType(VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_VULKAN_1_1_FEATURES);

                // Setup the chain
                vulkanFeatures.pNext(vulkan11Features.address());
            }
        }
    }

    /**
     * @return the currently enabled {@link VkPhysicalDeviceFeatures}
     */
    @NotNull
    public VkPhysicalDeviceFeatures getVk10Features() {
        return vulkanFeatures.features();
    }

    /**
     * @return If Vulkan 1.1 is available, {@link VkPhysicalDeviceVulkan11Features} will be returned. If not available, then null will be returned.
     */
    @Nullable
    public VkPhysicalDeviceVulkan11Features getVk11Features() {
        return vulkan11Features;
    }

    /**
     * @return If Vulkan 1.2 is available, {@link VkPhysicalDeviceVulkan12Features} will be returned. If not available, then null will be returned.
     */
    @Nullable
    public VkPhysicalDeviceVulkan12Features getVk12Features() {
        return vulkan12Features;
    }

    /**
     * Takes all version features and combines it into a chain.
     *
     * @return VkPhysicalDeviceFeatures2
     */
    public VkPhysicalDeviceFeatures2 build() {
        return vulkanFeatures;
    }
}
