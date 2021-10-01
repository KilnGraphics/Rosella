package graphics.kiln.rosella.test_utils;

import graphics.kiln.rosella.device.VulkanDevice;
import graphics.kiln.rosella.init.DeviceBuilder;
import graphics.kiln.rosella.init.InitializationRegistry;
import graphics.kiln.rosella.init.InstanceBuilder;
import graphics.kiln.rosella.init.VulkanInstance;
import org.lwjgl.vulkan.VK10;

public class VulkanTestInstanceDevice implements AutoCloseable {

    public final VulkanInstance instance;
    public final VulkanDevice device;

    public VulkanTestInstanceDevice(InitializationRegistry registry) {
        InstanceBuilder instanceBuilder = new InstanceBuilder(registry);
        this.instance = instanceBuilder.build("RosellaTests", VK10.VK_MAKE_VERSION(1, 0, 0));

        DeviceBuilder deviceBuilder = new DeviceBuilder(this.instance, registry);
        this.device = deviceBuilder.build();
    }

    @Override
    public void close() {
        this.device.destroy();
        this.instance.destroy();
    }
}
