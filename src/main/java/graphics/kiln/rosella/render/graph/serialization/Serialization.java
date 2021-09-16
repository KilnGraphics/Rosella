package graphics.kiln.rosella.render.graph.serialization;

import graphics.kiln.rosella.render.graph.ops.AbstractOp;

import java.util.Collections;
import java.util.List;

public class Serialization {

    private final AbstractOp ops;

    private final List<Integer> waitSemaphores;
    private final List<Integer> signalSemaphores;

    public Serialization(AbstractOp ops, List<Integer> waitSemaphores, List<Integer> signalSemaphores) {
        this.ops = ops;
        this.waitSemaphores = Collections.unmodifiableList(waitSemaphores);
        this.signalSemaphores = Collections.unmodifiableList(signalSemaphores);
    }
}
