package graphics.kiln.rosella.render.graph.ops;

import graphics.kiln.rosella.render.graph.resources.BufferReference;
import graphics.kiln.rosella.render.graph.resources.ImageReference;

public interface ObjectRegistry {
    
    void registerBuffer(BufferReference buffer);

    void registerBuffer(BufferReference buffer, int usageFlags);

    void registerImage(ImageReference image);

    void registerImage(ImageReference image, int usageFlags);
}
