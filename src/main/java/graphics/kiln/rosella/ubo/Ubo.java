package graphics.kiln.rosella.ubo;

import graphics.kiln.rosella.device.VulkanDevice;
import graphics.kiln.rosella.memory.Memory;
import graphics.kiln.rosella.memory.MemoryCloseable;
import graphics.kiln.rosella.render.descriptorsets.DescriptorSets;
import graphics.kiln.rosella.render.swapchain.Swapchain;
import graphics.kiln.rosella.memory.BufferInfo;

import java.util.List;

public abstract class Ubo implements MemoryCloseable {

    /**
     * Called when the uniform buffers should be created
     */
    public abstract void create(Swapchain swapchain);

    /**
     * Called before each frame to update the ubo
     */
    public abstract void update(int currentImg, Swapchain swapchain);

    /**
     * Gets the size of the ubo
     */
    public abstract int getSize();

    /**
     * Gets an list of pointers to the ubo frames
     */
    public abstract List<BufferInfo> getUniformBuffers();

    /**
     * Gets the descriptor sets used with this ubo
     */
    public abstract DescriptorSets getDescriptors();

    /**
     * Called when the program is closing and free's memory
     */
    public abstract void free(VulkanDevice device, Memory memory);

    public abstract void setDescriptors(DescriptorSets descriptorSets);
}
