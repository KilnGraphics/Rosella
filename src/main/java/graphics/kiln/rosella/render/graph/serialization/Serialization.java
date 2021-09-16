package graphics.kiln.rosella.render.graph.serialization;

import graphics.kiln.rosella.render.graph.ops.AbstractOp;
import graphics.kiln.rosella.render.graph.resources.BufferAllocationRequest;

import java.util.Collections;
import java.util.List;

public record Serialization(int queueFamilyIndex,
                            AbstractOp ops,
                            List<Integer> waitSemaphores,
                            List<Integer> signalSemaphores) {

    public Serialization(int queueFamilyIndex, AbstractOp ops, List<Integer> waitSemaphores, List<Integer> signalSemaphores) {
        this.queueFamilyIndex = queueFamilyIndex;
        this.ops = ops;
        this.waitSemaphores = Collections.unmodifiableList(waitSemaphores);
        this.signalSemaphores = Collections.unmodifiableList(signalSemaphores);
    }
}
