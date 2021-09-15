package graphics.kiln.rosella.init.features;

import graphics.kiln.rosella.init.DeviceBuildInformation;
import graphics.kiln.rosella.util.NamedID;
import org.lwjgl.vulkan.VK10;

/**
 * Tests if at least one queue exists with graphics capabilities.
 *
 * Does not allocate any queues.
 */
public class Compute extends SimpleApplicationFeature {

    public static final NamedID NAME = new NamedID("rosella:compute");

    public Compute() {
        super(NAME, Compute::canEnable, null);
    }

    private static boolean canEnable(DeviceBuildInformation meta) {
        return !meta.findQueueFamilies(VK10.VK_QUEUE_COMPUTE_BIT, true).isEmpty();
    }
}
