package graphics.kiln.rosella.render.graph.ops;

import com.google.gson.JsonObject;

public interface QueueRecordable {

    JsonObject convertToJson();

    void record();
}
