package graphics.kiln.rosella.render.graph.memory;

import graphics.kiln.rosella.util.VkUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.util.vma.VmaAllocationCreateInfo;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkExtent3D;
import org.lwjgl.vulkan.VkImageCreateInfo;

import java.nio.LongBuffer;

public class VMAImage implements VMACloseable {

    private long handle = VK10.VK_NULL_HANDLE;
    private long allocation = VK10.VK_NULL_HANDLE;

    public void create(@NotNull ImageAllocationRequirements requirements, long allocator) {
        if(this.handle != VK10.VK_NULL_HANDLE) {
            throw new RuntimeException("Tried to create image that has already been created");
        }

        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkExtent3D extent = VkExtent3D.malloc(stack);
            requirements.spec().fillExtent(extent);

            VkImageCreateInfo info = VkImageCreateInfo.calloc(stack);
            info.sType(VK10.VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO);
            info.imageType(requirements.spec().getImageType());
            info.format(requirements.spec().format().vulkan);
            info.extent(extent);
            info.mipLevels(requirements.spec().mipLevels());
            info.arrayLayers(requirements.spec().arrayLayers());
            info.samples(requirements.spec().sampleCount().vulkan);
            info.tiling(VK10.VK_IMAGE_TILING_OPTIMAL);
            info.usage(requirements.usageFlags());
            info.sharingMode(VK10.VK_SHARING_MODE_EXCLUSIVE);
            info.initialLayout(VK10.VK_IMAGE_LAYOUT_UNDEFINED);

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

            VkUtils.ok(Vma.vmaCreateImage(allocator, info, allocInfo, handle, allocation, null));

            this.handle = handle.get();
            this.allocation = allocation.get();
        }
    }

    public void destroy(long allocator) {
        if(this.handle == VK10.VK_NULL_HANDLE) {
            throw new RuntimeException("Tried to destroy image that is already destroyed");
        }

        Vma.vmaDestroyImage(allocator, this.handle, this.allocation);
        this.handle = VK10.VK_NULL_HANDLE;
        this.allocation = VK10.VK_NULL_HANDLE;
    }

    public long getHandle() {
        return this.handle;
    }
}
