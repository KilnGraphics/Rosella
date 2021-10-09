package graphics.kiln.rosella.render.graph.serialization;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import graphics.kiln.rosella.render.graph.ops.QueueRecordable;
import graphics.kiln.rosella.render.graph.resources.HandleProvider;
import graphics.kiln.rosella.util.VkUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public record Serialization(long uuid,
                            int queueFamilyIndex,
                            List<QueueRecordable> ops,
                            List<Integer> waitSemaphores,
                            List<Integer> signalSemaphores) {

    private static final AtomicLong nextUUID = new AtomicLong(0);

    public Serialization(int queueFamilyIndex, List<QueueRecordable> ops, List<Integer> waitSemaphores, List<Integer> signalSemaphores) {
        this(
                nextUUID.getAndIncrement(),
                queueFamilyIndex,
                ops,
                Collections.unmodifiableList(waitSemaphores),
                Collections.unmodifiableList(signalSemaphores)
        );
    }

    public void record(@NotNull VkCommandBuffer commandBuffer, @NotNull HandleProvider handles) {
        try(MemoryStack stack = MemoryStack.stackPush()) {
            VkCommandBufferBeginInfo beginInfo = VkCommandBufferBeginInfo.calloc(stack);
            beginInfo.sType(VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO);

            VkUtils.ok(VK10.vkBeginCommandBuffer(commandBuffer, beginInfo));

            for(QueueRecordable op : this.ops) {
                op.record(commandBuffer, handles);
            }

            VkUtils.ok(VK10.vkEndCommandBuffer(commandBuffer));
        }
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

        for(QueueRecordable op : this.ops) {
            result.add(op.convertToJson());
        }

        return result;
    }
}
