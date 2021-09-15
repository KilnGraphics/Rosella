package graphics.kiln.rosella.init;

import graphics.kiln.rosella.annotations.RequiresVulkan;
import graphics.kiln.rosella.init.features.ValidationLayers;
import org.junit.jupiter.api.Test;
import org.lwjgl.vulkan.VK10;

import static org.junit.jupiter.api.Assertions.*;

public class TestInstanceBuilder {

    @Test
    @RequiresVulkan
    void testMinimalBuild() {
        InitializationRegistry registry = new InitializationRegistry();

        assertDoesNotThrow(() -> {
            InstanceBuilder builder = new InstanceBuilder(registry);
            VulkanInstance instance = builder.build("RosellaTests", VK10.VK_MAKE_VERSION(1, 0, 0));
            instance.destroy();
        });
    }

    @Test
    @RequiresVulkan
    void testEnableValidation() {
        InitializationRegistry registry = new InitializationRegistry();
        registry.addRequiredInstanceLayer(ValidationLayers.INSTANCE_LAYER_NAME);

        assertDoesNotThrow(() -> {
            InstanceBuilder builder = new InstanceBuilder(registry);
            VulkanInstance instance = builder.build("RosellaTests", VK10.VK_MAKE_VERSION(1, 0, 0));
            instance.destroy();
        });
    }
}
