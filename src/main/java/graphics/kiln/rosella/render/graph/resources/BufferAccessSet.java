package graphics.kiln.rosella.render.graph.resources;


public class BufferAccessSet {
    public int queueFamily = -1;
    public int accessMask = 0;
    public int stageMask = 0;

    public void clear() {
        this.queueFamily = -1;
        this.accessMask = 0;
        this.stageMask = 0;
    }

    public void clear(int queueFamily) {
        this.queueFamily = queueFamily;
        this.accessMask = 0;
        this.stageMask = 0;
    }
}
