package graphics.kiln.rosella.render.graph.resources;

public record BufferAllocationRequest(
        long bufferID,
        long size,
        int usageFlags,
        int requiredHeapProperties,
        int preferredHeapProperties,
        int memoryHeapOverride
) {

    public BufferAllocationRequest(long bufferID, long size, int usageFlags, int requiredHeapProperties, int preferredHeapProperties) {
        this(
                bufferID,
                size,
                usageFlags,
                requiredHeapProperties,
                preferredHeapProperties,
                -1
        );
    }

    public BufferAllocationRequest(long bufferID, long size, int usageFlags, int memoryHeapIndex) {
        this(
                bufferID,
                size,
                usageFlags,
                0,
                0,
                memoryHeapIndex
        );
    }

    public boolean hasFixedHeap() {
        return this.memoryHeapOverride != -1;
    }
}
