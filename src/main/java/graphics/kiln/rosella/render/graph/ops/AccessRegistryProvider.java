package graphics.kiln.rosella.render.graph.ops;

import graphics.kiln.rosella.render.graph.resources.BufferReference;
import graphics.kiln.rosella.render.graph.resources.ImageReference;
import graphics.kiln.rosella.render.graph.resources.BufferAccessRegistry;
import graphics.kiln.rosella.render.graph.resources.ImageAccessRegistry;
import org.jetbrains.annotations.NotNull;

public interface AccessRegistryProvider {

    BufferAccessRegistry forBuffer(@NotNull BufferReference buffer);

    ImageAccessRegistry forImage(@NotNull ImageReference image);
}
