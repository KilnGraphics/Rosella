package graphics.kiln.rosella.render_graph.ops;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractOp {

    private AbstractOp next = null;

    protected AbstractOp() {
    }

    public AbstractOp getNext() {
        return this.next;
    }

    public void insertAfter(@NotNull AbstractOp next) {
        AbstractOp last = next.getLast(this);
        if(last == null) {
            throw new RuntimeException("Illegal insertion");
        }

        last.next = this.next;
        this.next = next;
    }

    public AbstractOp getLast() {
        return this.getLast(null);
    }

    public abstract void registerResourceUsages(UsageRegistry registry);

    public abstract void record();

    private AbstractOp getLast(@Nullable AbstractOp avoid) {
        AbstractOp current = this;
        if(current == avoid) {
            return null;
        }

        while (current.next != null) {
            current = current.next;
            if(current == avoid) {
                return null;
            }
        }

        return current;
    }
}
