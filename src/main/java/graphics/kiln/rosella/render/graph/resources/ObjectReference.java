package graphics.kiln.rosella.render.graph.resources;

import graphics.kiln.rosella.util.IDProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public abstract class ObjectReference {
    public final VulkanObjectType type;
    public final long id;

    protected ObjectReference(@NotNull VulkanObjectType type) {
        this.type = type;
        this.id = IDProvider.getNextID();
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectReference that = (ObjectReference) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
