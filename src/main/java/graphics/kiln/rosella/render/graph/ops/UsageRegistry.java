package graphics.kiln.rosella.render.graph.ops;

import graphics.kiln.rosella.render.graph.resources.BufferAccessSet;
import graphics.kiln.rosella.render.graph.resources.BufferReference;
import graphics.kiln.rosella.render.graph.resources.ImageReference;

public interface UsageRegistry {

    void registerBuffer(BufferReference buffer);

    void registerBuffer(BufferReference buffer, BufferAccessSet access);

    void registerImage(ImageReference image);

    void registerImage(ImageReference image, int accessMask, int stageMask, int initialLayout, int finalLayout);
}
