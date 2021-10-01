package graphics.kiln.rosella.ubo;

import graphics.kiln.rosella.device.VulkanDevice;
import graphics.kiln.rosella.memory.Memory;
import graphics.kiln.rosella.render.descriptorsets.DescriptorSets;
import graphics.kiln.rosella.render.shader.ShaderProgram;
import graphics.kiln.rosella.render.swapchain.Swapchain;
import graphics.kiln.rosella.vkobjects.VkCommon;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import graphics.kiln.rosella.memory.BufferInfo;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.util.vma.Vma.VMA_MEMORY_USAGE_CPU_ONLY;
import static org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT;

/**
 * Represents a basic, modifiable ubo.
 */
public class BasicUbo<T> extends Ubo {

    private final UboDataProvider<T> dataProvider;
    private final T reference; // I can't find a better name. This works for now
    private ByteBuffer data;
    protected final VkCommon common;
    private final List<BufferInfo> images;
    private final Long2ObjectMap<PointerBuffer> mappedAllocations = new Long2ObjectOpenHashMap<>();
    private DescriptorSets descSets;

    public BasicUbo(VkCommon common, ShaderProgram shader, Swapchain swapchain, UboDataProvider<T> uboDataProvider, T reference) {
        this.common = common;
        this.dataProvider = uboDataProvider;
        this.reference = reference;
        this.descSets = new DescriptorSets(shader.getRaw().getDescriptorPool());
        this.images = new ArrayList<>(swapchain.getSwapChainImages().size());
        this.data = MemoryUtil.memAlloc(uboDataProvider.getSize());
    }

    public ByteBuffer getData() {
        return data;
    }

    @Override
    public void create(@NotNull Swapchain swapchain) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            for (int i = 0; i < swapchain.getSwapChainImages().size(); i++) {
                LongBuffer pBuffer = stack.mallocLong(1);
                images.add(
                        common.memory.createBuffer(
                                getSize(),
                                VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT,
                                VMA_MEMORY_USAGE_CPU_ONLY,
                                pBuffer
                        )
                );
            }
        }
    }

    @Override
    public void update(int currentImg, @NotNull Swapchain swapchain) {
        if (images.isEmpty()) {
            return;
        }
        dataProvider.update(data, reference);

        try (MemoryStack ignored = MemoryStack.stackPush()) {
            PointerBuffer pLocation = mappedAllocations.computeIfAbsent(images.get(currentImg).allocation(), allocation -> {
                PointerBuffer newPointer = MemoryUtil.memAllocPointer(1);
                common.memory.map(allocation, true, newPointer);
                return newPointer;
            });

            ByteBuffer imagesBuffer = pLocation.getByteBuffer(0, getSize());
            MemoryUtil.memCopy(data, imagesBuffer);
        }
    }

    @Override
    public int getSize() {
        return data.capacity();
    }

    @NotNull
    @Override
    public List<BufferInfo> getUniformBuffers() {
        return images;
    }

    @NotNull
    @Override
    public DescriptorSets getDescriptors() {
        return descSets;
    }

    @Override
    public void free(VulkanDevice device, Memory memory) {
        for (BufferInfo uboImg : images) {
            uboImg.free(device, memory);
            common.memory.unmap(uboImg.allocation());
        }

        for (PointerBuffer pointer : mappedAllocations.values()) {
            MemoryUtil.memFree(pointer);
        }

        mappedAllocations.clear();
        images.clear();
        MemoryUtil.memFree(data);
        data = null;
    }

    @Override
    public void setDescriptors(@NotNull DescriptorSets descriptorSets) {
        this.descSets = descriptorSets; //FIXME: ew
    }
}
