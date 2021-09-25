package graphics.kiln.rosella.render.graph.resources;

public class BufferAccessState {

    public final int pendingAccesses;
    public final int pendingStages;
    public final int owningQueue;

    public BufferAccessState(int pendingAccesses, int pendingStages, int owningQueue) {
        this.pendingAccesses = pendingAccesses;
        this.pendingStages = pendingStages;
        this.owningQueue = owningQueue;
    }
}
