package graphics.kiln.rosella.render.graph.memory;

import graphics.kiln.rosella.render.graph.resources.BufferAccessRegistry;
import graphics.kiln.rosella.render.graph.resources.BufferReference;
import graphics.kiln.rosella.render.graph.resources.ImageAccessRegistry;
import graphics.kiln.rosella.render.graph.resources.ImageReference;
import org.jetbrains.annotations.NotNull;

public interface AllocationSetBuilder {

    void allocateBuffer(@NotNull BufferReference buffer, @NotNull BufferAllocationRequirements requirements, @NotNull BufferAccessRegistry registry);

    void freeBuffer(@NotNull BufferReference buffer);

    void allocateImage(@NotNull ImageReference image, @NotNull ImageAllocationRequirements requirements, @NotNull ImageAccessRegistry registry);

    void freeImage(@NotNull ImageReference image);

    AllocationSet build();
}
