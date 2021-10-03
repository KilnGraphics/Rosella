package graphics.kiln.rosella.render.graph.memory;

import graphics.kiln.rosella.render.graph.resources.BufferAccessRegistry;
import graphics.kiln.rosella.render.graph.resources.BufferReference;
import graphics.kiln.rosella.render.graph.resources.ImageAccessRegistry;
import graphics.kiln.rosella.render.graph.resources.ImageReference;
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class VMAAllocationSetBuilder implements AllocationSetBuilder {

    private final long allocator;

    private final Map<Long, VMABuffer> buffers = new Long2ObjectAVLTreeMap<>();
    private final Map<Long, VMAImage> images = new Long2ObjectAVLTreeMap<>();

    public VMAAllocationSetBuilder(long vmaAllocator) {
        this.allocator = vmaAllocator;
    }

    @Override
    public void allocateBuffer(@NotNull BufferReference buffer, @NotNull BufferAllocationRequirements requirements, @NotNull BufferAccessRegistry registry) {
        VMABuffer allocation = new VMABuffer();
        if(this.buffers.putIfAbsent(buffer.getID(), allocation) != allocation) {
            throw new RuntimeException("Tried to allocate buffer that has already been allocated");
        }

        allocation.create(requirements, this.allocator);
    }

    @Override
    public void freeBuffer(@NotNull BufferReference buffer) {
    }

    @Override
    public void allocateImage(@NotNull ImageReference image, @NotNull ImageAllocationRequirements requirements, @NotNull ImageAccessRegistry registry) {
        VMAImage allocation = new VMAImage();
        if(this.images.putIfAbsent(image.getID(), allocation) != allocation) {
            throw new RuntimeException("Tried to allocate image that has already been allocated");
        }

        allocation.create(requirements, this.allocator);
    }

    @Override
    public void freeImage(@NotNull ImageReference image) {
    }

    @Override
    public AllocationSet build() {
        ObjectArrayList<VMACloseable> resources = new ObjectArrayList<>();
        resources.ensureCapacity(buffers.size() + images.size());
        resources.addAll(this.buffers.values());
        resources.addAll(this.images.values());

        Map<Long, Long> mapping = new Long2ObjectAVLTreeMap<>();
        this.buffers.forEach((id, buffer) -> mapping.put(id, buffer.getHandle()));
        this.images.forEach((id, image) -> mapping.put(id, image.getHandle()));

        this.buffers.clear();
        this.images.clear();

        return new VMAAllocationSet(this.allocator, mapping, resources);
    }

    @Override
    public void abort() {
        for(VMABuffer buffer : this.buffers.values()) {
            buffer.destroy(this.allocator);
        }
        this.buffers.clear();
        for(VMAImage image : this.images.values()) {
            image.destroy(this.allocator);
        }
        this.images.clear();
    }
}
