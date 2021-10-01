package graphics.kiln.rosella.render.graph.memory;

import graphics.kiln.rosella.render.graph.resources.HandleProvider;

public interface AllocationSet extends HandleProvider {

    void destroy();
}
