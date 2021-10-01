package graphics.kiln.rosella.render.graph.memory;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.util.vma.Vma;
import org.lwjgl.vulkan.VK10;

public class VMAImage {

    private long handle = VK10.VK_NULL_HANDLE;
    private long allocation = VK10.VK_NULL_HANDLE;

    public void create(@NotNull ImageAllocationRequirements requirements, long allocator) {
        if(this.handle != VK10.VK_NULL_HANDLE) {
            throw new RuntimeException("Tried to create image that has already been created");
        }

        // TODO
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
