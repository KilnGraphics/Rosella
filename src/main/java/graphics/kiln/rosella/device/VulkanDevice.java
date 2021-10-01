package graphics.kiln.rosella.device;

import graphics.kiln.rosella.util.VkUtils;
import graphics.kiln.rosella.util.NamedID;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkPhysicalDeviceProperties;

import java.util.Collections;
import java.util.Map;

import static graphics.kiln.rosella.util.VkUtils.ok;
import static org.lwjgl.vulkan.VK10.vkDeviceWaitIdle;
import static org.lwjgl.vulkan.VK10.vkGetPhysicalDeviceProperties;

public class VulkanDevice {

    private final VkDevice rawDevice;
    private final VkPhysicalDeviceProperties properties;
    private final Map<NamedID, Object> enabledFeatures;

    public VulkanDevice(VkDevice rawDevice, Map<NamedID, Object> enabledFeatures) {
        this.rawDevice = rawDevice;
        this.properties = getPhysicalDeviceProperties(rawDevice.getPhysicalDevice());
        this.enabledFeatures = Collections.unmodifiableMap(enabledFeatures);
    }

    private VkPhysicalDeviceProperties getPhysicalDeviceProperties(VkPhysicalDevice physicalDevice) {
        VkPhysicalDeviceProperties properties = VkPhysicalDeviceProperties.malloc();
        vkGetPhysicalDeviceProperties(physicalDevice, properties);
        return properties;
    }

    public VkDevice getRawDevice() {
        return this.rawDevice;
    }

    public VkPhysicalDeviceProperties getProperties() {
        return properties;
    }

    public void waitForIdle() {
        VkUtils.ok(vkDeviceWaitIdle(rawDevice));
    }

    public void destroy() {
        VK10.vkDestroyDevice(this.rawDevice, null);
        properties.free();
    }

    /**
     * Tests if a ApplicationFeature is enabled for this device.
     *
     * @param name The name of the feature.
     * @return True if the feature is enabled. False otherwise.
     */
    public boolean isFeatureEnabled(NamedID name) {
        return enabledFeatures.containsKey(name);
    }

    /**
     * Retrieves the metadata for an enabled ApplicationFeature.
     *
     * @param name The name of the feature.
     * @return The metadata for the feature or null if the feature didnt generate any metadata.
     */
    public Object getFeatureMeta(NamedID name) {
        return enabledFeatures.get(name);
    }
}
