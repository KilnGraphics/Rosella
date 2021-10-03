package graphics.kiln.rosella.render.graph.memory;

public interface VMACloseable {

    void destroy(long vmaAllocator);
}
