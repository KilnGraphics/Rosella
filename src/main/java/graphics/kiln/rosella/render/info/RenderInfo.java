package graphics.kiln.rosella.render.info;

import graphics.kiln.rosella.device.VulkanDevice;
import graphics.kiln.rosella.memory.BufferInfo;
import graphics.kiln.rosella.memory.Memory;
import graphics.kiln.rosella.memory.MemoryCloseable;

public record RenderInfo(BufferInfo vertexBuffer, BufferInfo indexBuffer, int indexCount) implements MemoryCloseable {
    @Override
    public void free(VulkanDevice device, Memory memory) {
        memory.freeBuffer(vertexBuffer);
        memory.freeBuffer(indexBuffer);
    }
}
