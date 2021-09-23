package graphics.kiln.rosella.render.graph.ops;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import graphics.kiln.rosella.render.graph.resources.BufferReference;
import graphics.kiln.rosella.render.graph.resources.ImageReference;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.lwjgl.vulkan.VK10;

import java.util.List;

public class MemoryBarrierOp implements QueueRecordable {

    public final String TYPE_NAME = "MemoryBarrier";

    protected final int queueFamily;

    protected final List<Barrier> barriers = new ObjectArrayList<>();
    protected final List<BufferBarrier> bufferBarriers = new ObjectArrayList<>();
    protected final List<ImageBarrier> imageBarriers = new ObjectArrayList<>();

    public MemoryBarrierOp(int queueFamily) {
        this.queueFamily = queueFamily;
    }

    public void addBarrier(int srcAccessMask, int srcStageMask, int dstAccessMask, int dstStageMask) {
        this.barriers.add(new Barrier(srcAccessMask, srcStageMask, dstAccessMask, dstStageMask));
    }

    public void addBufferBarrier(BufferReference buffer, int srcAccessMask, int srcStageMask, int dstAccessMask, int dstStageMask) {
        this.bufferBarriers.add(new BufferBarrier(buffer, srcAccessMask, srcStageMask, dstAccessMask, dstStageMask, 0, 0, 0, VK10.VK_WHOLE_SIZE));
    }

    public void addBufferReleaseBarrier(BufferReference buffer, int srcAccessMask, int srcStageMask, int dstQueue) {
        this.bufferBarriers.add(new BufferBarrier(buffer, srcAccessMask, srcStageMask, 0, 0, this.queueFamily, dstQueue, 0, VK10.VK_WHOLE_SIZE));
    }

    public void addBufferAcquireBarrier(BufferReference buffer, int dstAccessMask, int dstStageMask, int srcQueue) {
        this.bufferBarriers.add(new BufferBarrier(buffer, 0, 0, dstAccessMask, dstStageMask, srcQueue, this.queueFamily, 0, VK10.VK_WHOLE_SIZE));
    }

    public void addImageBarrier(ImageReference image, int srcAccessMask, int srcStageMask, int dstAccessMask, int dstStageMask) {
        this.imageBarriers.add(new ImageBarrier(image, srcAccessMask, srcStageMask, dstAccessMask, dstStageMask, 0, 0, 0, 0));
    }

    public void addImageBarrier(ImageReference image, int srcAccessMask, int srcStageMask, int dstAccessMask, int dstStageMask, int oldLayout, int newLayout) {
        this.imageBarriers.add(new ImageBarrier(image, srcAccessMask, srcStageMask, dstAccessMask, dstStageMask, oldLayout, newLayout, 0, 0));
    }

    public void addImageReleaseBarrier(ImageReference image, int srcAccessMask, int srcStageMask, int dstQueue) {
        this.imageBarriers.add(new ImageBarrier(image, srcAccessMask, srcStageMask, 0, 0, 0, 0, this.queueFamily, dstQueue));
    }

    public void addImageReleaseBarrier(ImageReference image, int srcAccessMask, int srcStageMask, int dstQueue, int oldLayout, int newLayout) {
        this.imageBarriers.add(new ImageBarrier(image, srcAccessMask, srcStageMask, 0, 0, oldLayout, newLayout, this.queueFamily, dstQueue));
    }

    public void addImageAcquireBarrier(ImageReference image, int dstAccessMask, int dstStageMask, int srcQueue) {
        this.imageBarriers.add(new ImageBarrier(image, 0, 0, dstAccessMask, dstStageMask, 0, 0, srcQueue, this.queueFamily));
    }

    public void addImageAcquireBarrier(ImageReference image, int dstAccessMask, int dstStageMask, int srcQueue, int oldLayout, int newLayout) {
        this.imageBarriers.add(new ImageBarrier(image, 0, 0, dstAccessMask, dstStageMask, oldLayout, newLayout, srcQueue, this.queueFamily));
    }

    @Override
    public JsonObject convertToJson() {
        JsonObject result = new JsonObject();
        result.addProperty("type", TYPE_NAME);

        JsonArray globalBarriers = new JsonArray();
        for(Barrier barrier : this.barriers) {
            JsonObject jsonBarrier = new JsonObject();
            jsonBarrier.addProperty("srcAccessMask", barrier.srcAccessMask);
            jsonBarrier.addProperty("srcStageMask", barrier.srcStageMask);
            jsonBarrier.addProperty("dstAccessMask", barrier.dstAccessMask);
            jsonBarrier.addProperty("dstStageMask", barrier.dstStageMask);
            globalBarriers.add(jsonBarrier);
        }
        result.add("globalBarriers", globalBarriers);

        JsonArray bufferBarriers = new JsonArray();
        for(BufferBarrier barrier : this.bufferBarriers) {
            JsonObject jsonBarrier = new JsonObject();
            jsonBarrier.addProperty("buffer", barrier.buffer.getID());
            jsonBarrier.addProperty("offset", barrier.offset);
            jsonBarrier.addProperty("size", barrier.size);
            jsonBarrier.addProperty("srcQueue", barrier.srcQueue);
            jsonBarrier.addProperty("srcAccessMask", barrier.srcAccessMask);
            jsonBarrier.addProperty("srcStageMask", barrier.srcStageMask);
            jsonBarrier.addProperty("dstQueue", barrier.dstQueue);
            jsonBarrier.addProperty("dstAccessMask", barrier.dstAccessMask);
            jsonBarrier.addProperty("dstStageMask", barrier.dstStageMask);
            bufferBarriers.add(jsonBarrier);
        }
        result.add("bufferBarriers", bufferBarriers);

        return result;
    }

    @Override
    public void record() {

    }

    protected record Barrier(int srcAccessMask, int srcStageMask, int dstAccessMask, int dstStageMask) {
    }

    protected record BufferBarrier(BufferReference buffer, int srcAccessMask, int srcStageMask, int dstAccessMask, int dstStageMask, int srcQueue, int dstQueue, long offset, long size) {
    }

    protected record ImageBarrier(ImageReference image, int srcAccessMask, int srcStageMask, int dstAccessMask, int dstStageMask, int oldLayout, int newLayout, int srcQueue, int dstQueue) {
    }
}
