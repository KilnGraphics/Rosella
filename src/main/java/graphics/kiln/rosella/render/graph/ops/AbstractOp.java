package graphics.kiln.rosella.render.graph.ops;

import com.google.gson.JsonObject;

public abstract class AbstractOp implements QueueRecordable {

    private boolean locked = false;

    private WaitGroup waitGroup = null;
    private SignalGroup signalGroup = null;

    protected AbstractOp() {
    }

    public final void lock() {
        this.locked = true;
        if(this.waitGroup != null) {
            this.waitGroup.lock();
        }
        if(this.signalGroup != null) {
            this.signalGroup.lock();
        }
    }

    public final boolean isLocked() {
        return this.locked;
    }

    public final void setWaitGroup(WaitGroup group) {
        if(this.locked) {
            throw new RuntimeException("Cannot modify locked op");
        }
        this.waitGroup = group;
    }

    public final void setSignalGroup(SignalGroup group) {
        if(this.locked) {
            throw new RuntimeException("Cannot modify locked op");
        }
        this.signalGroup = group;
    }

    public WaitGroup getWaitGroup() {
        return this.waitGroup;
    }

    public SignalGroup getSignalGroup() {
        return this.signalGroup;
    }

    public abstract void registerObjects(ObjectRegistry registry);

    public abstract void registerResourceUsages(UsageRegistry registry);

    @Override
    public JsonObject convertToJson() {
        JsonObject result = new JsonObject();
        result.addProperty("type", getJsonType());
        return result;
    }

    protected abstract String getJsonType();
}
