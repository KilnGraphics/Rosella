package graphics.kiln.rosella.render.graph.ops;

import graphics.kiln.rosella.render.graph.resources.BufferRange;
import graphics.kiln.rosella.render.graph.resources.BufferReference;
import graphics.kiln.rosella.render.graph.resources.ImageReference;

public interface UsageRegistry {

    void registerBuffer(BufferReference buffer);

    void registerBuffer(BufferReference buffer, int accessMask, int stageMask, BufferRange range);

    void registerImage(ImageReference image);

    void registerImage(ImageReference image, int accessMask, int stageMask, int initialLayout, int finalLayout);
}
