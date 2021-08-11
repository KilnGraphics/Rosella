package me.hydos.rosella.device;

/**
 * The presentation and graphics queue used in {@link me.hydos.rosella.Rosella}
 */
public class VulkanQueues {

    public final VulkanQueue graphicsQueue;
    public final VulkanQueue presentQueue;

    public VulkanQueues(VulkanQueue graphicsQueue, VulkanQueue presentQueue) {
        this.graphicsQueue = graphicsQueue;
        this.presentQueue = presentQueue;
    }
}
