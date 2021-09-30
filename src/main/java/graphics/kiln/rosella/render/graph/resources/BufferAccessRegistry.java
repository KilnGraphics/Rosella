package graphics.kiln.rosella.render.graph.resources;

public interface BufferAccessRegistry {

    void addAccess(int queueFamily, int accessMask, int stageMask);

    void addDiscardAccess(int stageMask);
}
