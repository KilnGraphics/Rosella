package me.hydos.rosella.init;

import me.hydos.rosella.logging.DebugLogger;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VKCapabilitiesInstance;
import org.lwjgl.vulkan.VkDebugUtilsMessengerCallbackDataEXT;
import org.lwjgl.vulkan.VkInstance;

import static org.lwjgl.vulkan.EXTDebugUtils.*;

public class VulkanInstance {

    private final VkInstance instance;
    private final VulkanVersion version;
    private final DebugUtilsCallback debugUtilsCallback;

    public VulkanInstance(VkInstance instance) {
        this.instance = instance;
        this.version = VulkanVersion.fromVersionNumber(instance.getCapabilities().apiVersion);
        this.debugUtilsCallback = null;
    }

    public VulkanInstance(VkInstance instance, @Nullable DebugUtilsCallback callback) {
        this.instance = instance;
        this.version = VulkanVersion.fromVersionNumber(instance.getCapabilities().apiVersion);
        this.debugUtilsCallback = callback;
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
        if(this.debugUtilsCallback != null) {
            this.debugUtilsCallback.destroy();
        }
        VK10.vkDestroyInstance(this.instance, null);
    }

    public static class DebugUtilsCallback {
        DebugLogger logger;

        public DebugUtilsCallback(DebugLogger logger) {
            this.logger = logger;
        }

        public int debugCallback(int severity, int messageType, long pCallbackData, long pUserData) {
            VkDebugUtilsMessengerCallbackDataEXT callbackData = VkDebugUtilsMessengerCallbackDataEXT.create(pCallbackData);
            String message = callbackData.pMessageString();

            String msgSeverity = switch (severity) {
                case VK_DEBUG_UTILS_MESSAGE_SEVERITY_VERBOSE_BIT_EXT -> "VERBOSE";
                case VK_DEBUG_UTILS_MESSAGE_SEVERITY_INFO_BIT_EXT -> "INFO";
                case VK_DEBUG_UTILS_MESSAGE_SEVERITY_WARNING_BIT_EXT -> "WARNING";
                case VK_DEBUG_UTILS_MESSAGE_SEVERITY_ERROR_BIT_EXT -> "ERROR";
                default -> throw new IllegalStateException("Unexpected severity: " + severity);
            };

            if(this.logger == null) {
                return VK10.VK_FALSE;
            }

            return switch (messageType) {
                case VK_DEBUG_UTILS_MESSAGE_TYPE_GENERAL_BIT_EXT -> this.logger.logGeneral(message, msgSeverity);
                case VK_DEBUG_UTILS_MESSAGE_TYPE_VALIDATION_BIT_EXT -> this.logger.logValidation(message, msgSeverity);
                case VK_DEBUG_UTILS_MESSAGE_TYPE_PERFORMANCE_BIT_EXT -> this.logger.logPerformance(message, msgSeverity);
                default -> this.logger.logUnknown(message, msgSeverity);
            };
        }

        public void destroy() {
            // Were creating the callback in the instance create info for now so nothing to do here.
        }
    }
}
