package graphics.kiln.rosella.render.graph.memory;

import graphics.kiln.rosella.render.graph.resources.HandleProvider;

import java.util.Map;

public interface AllocationSet extends HandleProvider {

    Map<Long, Long> getHandleMapping();

    void destroy();
}
