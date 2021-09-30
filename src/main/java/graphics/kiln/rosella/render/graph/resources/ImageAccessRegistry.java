package graphics.kiln.rosella.render.graph.resources;

import org.jetbrains.annotations.NotNull;

public interface ImageAccessRegistry {

    void addAccess(int queueFamily, int layout, int accessMask, int stageMask, @NotNull ImageSubresourceRange range);

    void addDiscardAccess(int stageMask, @NotNull ImageSubresourceRange range);
}
