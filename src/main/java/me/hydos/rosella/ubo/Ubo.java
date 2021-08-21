package me.hydos.rosella.ubo;

import me.hydos.rosella.device.VulkanDevice;
import me.hydos.rosella.memory.BufferInfo;
import me.hydos.rosella.memory.Memory;
import me.hydos.rosella.memory.MemoryCloseable;
import me.hydos.rosella.render.descriptorsets.DescriptorSets;
import me.hydos.rosella.render.swapchain.Swapchain;

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
