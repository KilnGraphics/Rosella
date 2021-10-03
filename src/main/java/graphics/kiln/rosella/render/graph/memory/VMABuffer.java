package graphics.kiln.rosella.render.graph.memory;

import graphics.kiln.rosella.util.VkUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkBufferCreateInfo;

import java.nio.LongBuffer;

public class VMABuffer implements VMACloseable {
    private long handle = VK10.VK_NULL_HANDLE;
    private long allocation = VK10.VK_NULL_HANDLE;

    public void create(@NotNull BufferAllocationRequirements requirements, long allocator) {
        if(this.handle != VK10.VK_NULL_HANDLE) {
            throw new RuntimeException("Tried to create buffer that has already been created");
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkBufferCreateInfo info = VkBufferCreateInfo.calloc(stack);
            info.sType(VK10.VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO);
            info.size(requirements.size());
            info.usage(requirements.usageFlags());
            info.sharingMode(VK10.VK_SHARING_MODE_EXCLUSIVE);

            VmaAllocationCreateInfo allocInfo = VmaAllocationCreateInfo.calloc(stack);
            if(requirements.restriction() != null) {
                MemoryTypeRestriction restriction = requirements.restriction();
                if(restriction.isOverride()) {
                    allocInfo.memoryTypeBits(1 << restriction.memoryTypeOverride());
                } else {
                    if (restriction.requiredFlagMask() != 0) {
                        allocInfo.requiredFlags(restriction.requiredFlagMask());
                    }
                    if (restriction.preferredFlagMask() != 0 && restriction.preferredFlagMask() != ~0) {
                        allocInfo.preferredFlags(restriction.preferredFlagMask());
                    }
                    if (restriction.requiredTypeMask() != 0 && restriction.requiredTypeMask() != ~0) {
                        allocInfo.memoryTypeBits(restriction.requiredTypeMask());
                    }
                }
            }

            LongBuffer handle = stack.mallocLong(1);
            PointerBuffer allocation = stack.mallocPointer(1);

            VkUtils.ok(Vma.vmaCreateBuffer(allocator, info, allocInfo, handle, allocation, null));

            this.handle = handle.get();
            this.allocation = allocation.get();
        }
    }

    public void destroy(long allocator) {
        if(this.handle == VK10.VK_NULL_HANDLE) {
            throw new RuntimeException("Tried to destroy buffer that is already destroyed");
        }

        Vma.vmaDestroyBuffer(allocator, this.handle, this.allocation);
        this.handle = VK10.VK_NULL_HANDLE;
        this.allocation = VK10.VK_NULL_HANDLE;
    }

    public long getHandle() {
        return this.handle;
    }
}
