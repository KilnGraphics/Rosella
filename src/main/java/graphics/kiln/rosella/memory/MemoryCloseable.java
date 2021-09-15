package graphics.kiln.rosella.memory;

import graphics.kiln.rosella.device.VulkanDevice;

/**
 * Used to safely close memory when an object wants to be de-allocated.
 */
public interface MemoryCloseable {
    void free(VulkanDevice device, Memory memory);
}
