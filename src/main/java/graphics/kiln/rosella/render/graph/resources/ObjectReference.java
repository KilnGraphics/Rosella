package graphics.kiln.rosella.render.graph.resources;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicLong;

public abstract class ObjectReference {
    private static final AtomicLong nextID = new AtomicLong(1);

    public final VulkanObjectType type;
    public final long id;

    protected ObjectReference(@NotNull VulkanObjectType type) {
        this.type = type;
        this.id = nextID.getAndIncrement();
    }

    public long getID() {
        return this.id;
    }

    public VulkanObjectType getType() {
        return this.type;
    }

    public long getVulkanHandle(@NotNull HandleProvider provider) {
        return provider.getVulkanHandle(this.id);
    }
}
