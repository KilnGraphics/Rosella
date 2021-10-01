package graphics.kiln.rosella.render.graph.memory;

import graphics.kiln.rosella.render.graph.resources.BufferAccessRegistry;
import graphics.kiln.rosella.render.graph.resources.BufferReference;
import graphics.kiln.rosella.render.graph.resources.ImageAccessRegistry;
import graphics.kiln.rosella.render.graph.resources.ImageReference;
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class VMAAllocationSetBuilder implements AllocationSetBuilder {

    private final Map<Long, VMABuffer> buffers = new Long2ObjectAVLTreeMap<>();
    private final Map<Long, VMAImage> images = new Long2ObjectAVLTreeMap<>();

    @Override
    public void allocateBuffer(@NotNull BufferReference buffer, @NotNull BufferAllocationRequirements requirements, @NotNull BufferAccessRegistry registry) {
        VMABuffer allocation = new VMABuffer();
        if(this.buffers.putIfAbsent(buffer.getID(), allocation) != allocation) {
            throw new RuntimeException("Tried to allocate buffer that has already been allocated");
        }

        allocation.create(requirements, 0 /* TODO */);
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

        allocation.create(requirements, 0 /* TODO */);
    }

    @Override
    public void freeImage(@NotNull ImageReference image) {
    }

    @Override
    public AllocationSet build() {
        return null;
    }

    @Override
    public void abort() {
        for(VMABuffer buffer : this.buffers.values()) {
            buffer.destroy(0 /* TODO */);
        }
        this.buffers.clear();
        for(VMAImage image : this.images.values()) {
            image.destroy(0 /* TODO */);
        }
        this.images.clear();
    }
}
