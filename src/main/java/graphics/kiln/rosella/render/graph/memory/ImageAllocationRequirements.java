package graphics.kiln.rosella.render.graph.memory;

import graphics.kiln.rosella.render.graph.resources.ImageSpec;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record ImageAllocationRequirements(@NotNull ImageSpec spec, int usageFlags, @Nullable MemoryTypeRestriction restriction) {
}
