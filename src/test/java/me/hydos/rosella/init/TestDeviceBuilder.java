package me.hydos.rosella.init;

import me.hydos.rosella.annotations.RequiresVulkan;
import me.hydos.rosella.device.VulkanDevice;
import me.hydos.rosella.test_utils.VulkanTestInstance;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TestDeviceBuilder {

    @Test
    @RequiresVulkan
    void testMinimalBuild() {
        InitializationRegistry registry = new InitializationRegistry();
        try (VulkanTestInstance instance = new VulkanTestInstance(registry)) {

            assertDoesNotThrow(() -> {
                DeviceBuilder builder = new DeviceBuilder(instance.instance, registry);
                VulkanDevice device = builder.build();
                device.destroy();
            });
        }
    }
}
