package me.hydos.rosella.init.features;

import me.hydos.rosella.init.DeviceBuilder;
import org.lwjgl.vulkan.VK10;

/**
 * Tests if at least one queue exists with graphics capabilities.
 *
 * Does not allocate any queues.
 */
public class Graphics extends SimpleApplicationFeature {

    public static final String NAME = "rosella:graphics";

    public Graphics() {
        super(NAME, Graphics::canEnable, null);
    }

    private static boolean canEnable(DeviceBuilder.DeviceMeta meta) {
        return meta.hasQueueWithFlags(VK10.VK_QUEUE_GRAPHICS_BIT);
    }
}
