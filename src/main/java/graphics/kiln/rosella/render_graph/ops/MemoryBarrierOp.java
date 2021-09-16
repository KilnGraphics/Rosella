package graphics.kiln.rosella.render_graph.ops;

import graphics.kiln.rosella.render_graph.resources.BufferReference;
import graphics.kiln.rosella.render_graph.resources.ImageReference;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.lwjgl.vulkan.VK10;

import java.util.List;

public class MemoryBarrierOp extends AbstractOp {

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
    public void registerResourceUsages(UsageRegistry registry) {
        for(BufferBarrier barrier : this.bufferBarriers) {
            registry.registerBuffer(barrier.buffer());
        }
        for(ImageBarrier barrier : this.imageBarriers) {
            registry.registerImage(barrier.image());
        }
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
