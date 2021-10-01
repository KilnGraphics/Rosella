package graphics.kiln.rosella.device;

import graphics.kiln.rosella.Rosella;

/**
 * The presentation and graphics queue used in {@link Rosella}
 */
public class VulkanQueues {

    public final VulkanQueue graphicsQueue;
    public final VulkanQueue presentQueue;

    public VulkanQueues(VulkanQueue graphicsQueue, VulkanQueue presentQueue) {
        this.graphicsQueue = graphicsQueue;
        this.presentQueue = presentQueue;
    }
}
