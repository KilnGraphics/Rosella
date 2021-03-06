package graphics.kiln.rosella.init;

import graphics.kiln.rosella.util.NamedID;
import graphics.kiln.rosella.init.features.ApplicationFeature;
import org.lwjgl.vulkan.*;

import java.util.List;
import java.util.Map;

public interface DeviceBuildInformation {

    boolean isApplicationFeatureSupported(NamedID name);

    ApplicationFeature.Instance getApplicationFeature(NamedID name);

    VulkanInstance getInstance();

    VkPhysicalDevice getPhysicalDevice();

    VkPhysicalDeviceProperties getPhysicalDeviceProperties();

    DeviceFeatureBuilder getDeviceFeatures();

    /**
     * Checks if a device extension is supported
     *
     * @param extension The name of the extension.
     * @return True if the extension is supported, false otherwise.
     */
    boolean isExtensionAvailable(String extension);

    /**
     * Retrieves all supported extensions.
     *
     * @return A map of all supported extensions
     */
    Map<String, VkExtensionProperties> getAllExtensionProperties();

    /**
     * Returns the properties of a device extension or null if the extension is not supported.
     *
     * @param extension The name of the extension
     * @return The properties of the extension or null if it is not supported
     */
    VkExtensionProperties getExtensionProperties(String extension);

    /**
     * Lists all queue families.
     *
     * @return A list of all available queue families.
     */
    List<VkQueueFamilyProperties> getQueueFamilyProperties();

    /**
     * Finds all queue families that satisfy the specified criteria.
     *
     * @param flags The flags that the queue must support
     * @param noTransferLimit If false only queues that have a min transfer granularity of 1 will be returned
     * @return A list of queue family indices that satisfy the requirements
     */
    List<Integer> findQueueFamilies(int flags, boolean noTransferLimit);
}
