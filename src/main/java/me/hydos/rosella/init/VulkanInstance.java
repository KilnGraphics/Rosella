package me.hydos.rosella.init;

import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VKCapabilitiesInstance;
import org.lwjgl.vulkan.VkInstance;

public class VulkanInstance {

    private final VkInstance instance;
    private final VulkanVersion version;

    public VulkanInstance(VkInstance instance) {
        this.instance = instance;
        this.version = VulkanVersion.fromVersionNumber(instance.getCapabilities().apiVersion);
    }

    public VkInstance getInstance() {
        return this.instance;
    }

    public VKCapabilitiesInstance getCapabilities() {
        return instance.getCapabilities();
    }

    public VulkanVersion getVersion() {
        return this.version;
    }

    public void destroy() {
        VK10.vkDestroyInstance(this.instance, null);
    }
}
