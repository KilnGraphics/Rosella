package graphics.kiln.rosella.render.graph.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import graphics.kiln.rosella.render.graph.ops.AbstractOp;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public record Serialization(long uuid,
                            int queueFamilyIndex,
                            AbstractOp ops,
                            List<Integer> waitSemaphores,
                            List<Integer> signalSemaphores) {

    private static final AtomicLong nextUUID = new AtomicLong(0);

    public Serialization(int queueFamilyIndex, AbstractOp ops, List<Integer> waitSemaphores, List<Integer> signalSemaphores) {
        this(
                nextUUID.getAndIncrement(),
                queueFamilyIndex,
                ops,
                Collections.unmodifiableList(waitSemaphores),
                Collections.unmodifiableList(signalSemaphores)
        );
    }

    public JsonObject convertToJson() {
        JsonObject result = new JsonObject();

        result.addProperty("uuid", this.uuid);
        result.addProperty("queueFamilyIndex", this.queueFamilyIndex);
        result.add("waitSemaphores", generateJsonWaitSemaphores());
        result.add("signalSemaphores", generateJsonSignalSemaphores());
        result.add("ops", generateJsonOps());

        return result;
    }

    private JsonArray generateJsonWaitSemaphores() {
        JsonArray result = new JsonArray();

        for(Integer semaphore : this.waitSemaphores) {
            result.add(semaphore);
        }

        return result;
    }

    private JsonArray generateJsonSignalSemaphores() {
        JsonArray result = new JsonArray();

        for(Integer semaphore : this.signalSemaphores) {
            result.add(semaphore);
        }

        return result;
    }

    private JsonArray generateJsonOps() {
        JsonArray result = new JsonArray();

        AbstractOp current = this.ops;
        while(current != null) {
            result.add(current.convertToJson());
            current = current.getNext();
        }

        return result;
    }
}
