package graphics.kiln.rosella.render.graph.ops;

import com.google.gson.JsonObject;
import graphics.kiln.rosella.render.graph.resources.HandleProvider;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.vulkan.VkCommandBuffer;

public interface QueueRecordable {

    void record(@NotNull VkCommandBuffer commandBuffer, @NotNull HandleProvider handleProvider);

    JsonObject convertToJson();
}
