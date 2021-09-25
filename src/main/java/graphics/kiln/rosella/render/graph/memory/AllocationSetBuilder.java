package graphics.kiln.rosella.render.graph.memory;

import graphics.kiln.rosella.render.graph.resources.BufferReference;
import graphics.kiln.rosella.render.graph.resources.ImageReference;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

public interface AllocationSetBuilder {

    void allocateBuffer(@NotNull BufferReference buffer, @NotNull BufferAllocationRequirements requirements, @NotNull BiConsumer<BufferReference, Integer> reuseCallback);

    void freeBuffer(@NotNull BufferReference buffer);

    void allocateImage(@NotNull ImageReference image, @NotNull ImageAllocationRequirements requirements, @NotNull BiConsumer<ImageReference, Integer> reuseCallback);

    void freeImage(@NotNull ImageReference image);

    AllocationSet build();
}
