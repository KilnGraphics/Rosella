package graphics.kiln.rosella.render.graph.memory;

import graphics.kiln.rosella.render.graph.resources.BufferAccessRegistry;
import graphics.kiln.rosella.render.graph.resources.BufferReference;
import graphics.kiln.rosella.render.graph.resources.ImageAccessRegistry;
import graphics.kiln.rosella.render.graph.resources.ImageReference;
import graphics.kiln.rosella.util.VkUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkBufferCreateInfo;

import java.nio.LongBuffer;

public class LinkedListAllocationSetBuilder implements AllocationSetBuilder {



    @Override
    public void allocateBuffer(@NotNull BufferReference buffer, @NotNull BufferAllocationRequirements requirements, @NotNull BufferAccessRegistry registry) {

    }

    @Override
    public void freeBuffer(@NotNull BufferReference buffer) {

    }

    @Override
    public void allocateImage(@NotNull ImageReference image, @NotNull ImageAllocationRequirements requirements, @NotNull ImageAccessRegistry registry) {

    }

    @Override
    public void freeImage(@NotNull ImageReference image) {

    }

    @Override
    public AllocationSet build() {
        return null;
    }

    private class BufferMetadata {
        public final BufferReference reference;
        private long handle;

        private BufferMetadata(@NotNull BufferReference reference, @NotNull BufferAllocationRequirements requirements) {
            this.reference = reference;

            try (MemoryStack stack = MemoryStack.stackPush()) {
                VkBufferCreateInfo info = VkBufferCreateInfo.calloc(stack);
                info.sType(VK10.VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO);
                info.size();
                info.usage(); // TODO
                info.sharingMode(VK10.VK_SHARING_MODE_EXCLUSIVE);

                LongBuffer handle = stack.mallocLong(1);
                VkUtils.ok(VK10.vkCreateBuffer(null /*TODO*/ , info, null, handle));

                this.handle = handle.get();
            }
        }
    }
}
