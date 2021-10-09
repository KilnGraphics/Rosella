package graphics.kiln.rosella.render.graph.serialization;

import graphics.kiln.rosella.render.graph.resources.ImageSubresourceRange;
import org.jetbrains.annotations.NotNull;

public record ImageState(@NotNull ImageSubresourceRange range, int owningQueue, int layout, boolean forceComplete) {
}
