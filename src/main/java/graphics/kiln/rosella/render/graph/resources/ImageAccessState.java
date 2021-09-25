package graphics.kiln.rosella.render.graph.resources;

import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class ImageAccessState {

    @NotNull
    public final List<Partition> partitions;

    public ImageAccessState(@NotNull Collection<Partition> partitions) {
        this.partitions = new ObjectImmutableList<>(partitions);
    }

    public record Partition(int owningQueue, int currentLayout, @NotNull ImageSubresourceRange range) {
    }
}
