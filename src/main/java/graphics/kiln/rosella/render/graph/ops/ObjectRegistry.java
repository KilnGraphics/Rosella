package graphics.kiln.rosella.render.graph.ops;

import graphics.kiln.rosella.render.graph.resources.BufferReference;
import graphics.kiln.rosella.render.graph.resources.ImageReference;

public interface ObjectRegistry {
    
    void registerBuffer(BufferReference buffer);

    void registerImage(ImageReference image);
}
