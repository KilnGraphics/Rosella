package graphics.kiln.rosella.render.graph.serialization;

import graphics.kiln.rosella.render.graph.ops.AbstractOp;
import graphics.kiln.rosella.render.graph.resources.BufferAllocationRequest;

import java.util.Collections;
import java.util.List;

public record Serialization(AbstractOp ops,
                            List<Integer> waitSemaphores,
                            List<Integer> signalSemaphores,
                            List<BufferAllocationRequest> bufferAllocations) {

    public Serialization(AbstractOp ops, List<Integer> waitSemaphores, List<Integer> signalSemaphores, List<BufferAllocationRequest> bufferAllocations) {
        this.ops = ops;
        this.waitSemaphores = Collections.unmodifiableList(waitSemaphores);
        this.signalSemaphores = Collections.unmodifiableList(signalSemaphores);
        this.bufferAllocations = Collections.unmodifiableList(bufferAllocations);
    }
}
