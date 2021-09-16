package graphics.kiln.rosella.render_graph.resources;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicLong;

public abstract class ResourceReference {
    private static final AtomicLong nextID = new AtomicLong(1);

    public final VulkanResourceType type;
    public final long id;

    protected ResourceReference(@NotNull VulkanResourceType type) {
        this.type = type;
        this.id = nextID.getAndIncrement();
    }

    public long getID() {
        return this.id;
    }

    public VulkanResourceType getType() {
        return this.type;
    }

    public long getVulkanHandle(@NotNull HandleProvider provider) {
        return provider.getVulkanHandle(this.id);
    }
}
