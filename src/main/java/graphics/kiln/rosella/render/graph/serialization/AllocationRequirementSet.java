package graphics.kiln.rosella.render.graph.serialization;

import graphics.kiln.rosella.render.graph.resources.BufferAllocationRequirements;
import graphics.kiln.rosella.render.graph.resources.ImageAllocationRequirements;

import java.util.function.BiConsumer;

public interface AllocationRequirementSet {

    void forEachAllocationBuffer(BiConsumer<Long, BufferAllocationRequirements> consumer);

    void forEachAllocationImage(BiConsumer<Long, ImageAllocationRequirements> consumer);
}
