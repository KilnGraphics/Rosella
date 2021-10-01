package graphics.kiln.rosella.render.graph.memory;

import org.jetbrains.annotations.Nullable;

public record BufferAllocationRequirements(long size, int usageFlags, @Nullable MemoryTypeRestriction restriction) {
}
