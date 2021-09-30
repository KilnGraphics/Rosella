package graphics.kiln.rosella.render.graph.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public record SerializedGraph(
        List<Serialization> serializations,
        int semaphoreCount) {

    public SerializedGraph(@NotNull List<Serialization> serializations, int semaphoreCount) {
        this.serializations = Collections.unmodifiableList(serializations);
        this.semaphoreCount = semaphoreCount;

    }

    public JsonObject convertToJson() {
        JsonObject result = new JsonObject();

        result.add("serializations", generateJsonSerializations());

        return result;
    }

    private JsonArray generateJsonSerializations() {
        JsonArray result = new JsonArray();

        for(Serialization serialization : this.serializations) {
            result.add(serialization.convertToJson());
        }

        return result;
    }
}
