package graphics.kiln.rosella.render.graph.memory;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class VMAAllocationSet implements AllocationSet {

    private final long allocator;

    private final Map<Long, Long> handles;
    private final List<VMACloseable> resources;

    public VMAAllocationSet(long allocator, @NotNull Map<Long, Long> handles, @NotNull List<VMACloseable> resources) {
        this.allocator = allocator;
        this.handles = handles;
        this.resources = resources;
    }

    @Override
    public long getVulkanHandle(long resourceID) {
        return this.handles.get(resourceID);
    }

    @Override
    public Map<Long, Long> getHandleMapping() {
        return this.handles;
    }

    @Override
    public void destroy() {
        for(VMACloseable resource : this.resources) {
            resource.destroy(this.allocator);
        }
        this.resources.clear();
        this.handles.clear();
    }
}
