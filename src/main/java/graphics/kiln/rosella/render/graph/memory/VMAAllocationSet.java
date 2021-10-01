package graphics.kiln.rosella.render.graph.memory;

public class VMAAllocationSet implements AllocationSet {

    private final long allocator;

    public VMAAllocationSet(long allocator) {
        this.allocator = allocator;
    }

    @Override
    public long getVulkanHandle(long resourceID) {
        return 0;
    }

    @Override
    public void destroy() {

    }
}
