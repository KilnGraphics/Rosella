package graphics.kiln.rosella.render.graph.ops;

import graphics.kiln.rosella.util.IDProvider;

public class SignalGroup {
    public final long id = IDProvider.getNextID();

    private boolean locked = false;

    public SignalGroup() {
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void lock() {
        this.locked = true;
    }
}
