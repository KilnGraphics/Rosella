package graphics.kiln.rosella.memory;

import graphics.kiln.rosella.device.VulkanDevice;

public record BufferInfo(long buffer, long allocation) implements MemoryCloseable {
    @Override
    public void free(VulkanDevice device, Memory memory) {
        memory.freeBuffer(this);
    }
}
