package graphics.kiln.rosella.render.graph.serialization;

import java.util.Collections;
import java.util.List;

public class SerializedGraph {

    private final List<Serialization> serializations;

    private final int semaphoreCount;

    public SerializedGraph(List<Serialization> serializations, int semaphoreCount) {
        this.serializations = Collections.unmodifiableList(serializations);
        this.semaphoreCount = semaphoreCount;
    }

    public List<Serialization> getSerializations() {
        return this.serializations;
    }
}
