package graphics.kiln.rosella.render.graph.serialization;

import graphics.kiln.rosella.render.graph.resources.ImageSubresourceRange;
import graphics.kiln.rosella.util.ListSpacePartition;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.vulkan.VK10;

import java.util.Map;

public class ImageAccessTracker {

    private final int aspectMask;
    private final int mipLevels;
    private final int arrayLayers;

    private final Map<Integer, ListSpacePartition<State>> partitions = new Int2ObjectArrayMap<>();

    public ImageAccessTracker(int aspectMask, int mipLevels, int arrayLayers) {
        this.aspectMask = aspectMask;
        this.mipLevels = mipLevels;
        this.arrayLayers = arrayLayers;

        int i = 0;
        while(aspectMask != 0) {
            if((aspectMask & 1) == 1) {
                partitions.put(i, new ListSpacePartition<>(2));
            }
            aspectMask >>>= 1;
            i++;
        }
    }

    public void addAccess(int queueFamily, int imageLayout, int accessMask, int stageMask, @NotNull ImageSubresourceRange range, @Nullable ImageBarrierProvider barrier) {
        int aspectMask = range.aspectMask() & this.aspectMask;

        this.tmpBarrier = barrier;

        final State newState = new State(queueFamily, imageLayout, accessMask, stageMask);
        int[] startRange = new int[]{ range.baseMipLevel(), range.baseArrayLayer() };
        int[] endRange = new int[]{
                range.mipLevels() == VK10.VK_REMAINING_MIP_LEVELS ? this.mipLevels : range.baseMipLevel() + range.mipLevels(),
                range.arrayLayers() == VK10.VK_REMAINING_ARRAY_LAYERS ? this.arrayLayers : range.baseArrayLayer() + range.arrayLayers()
        };

        int i = 0;
        while(aspectMask != 0) {
            if((aspectMask & 1) == 1) {
                this.tmpAspectMask = 1 << i;
                partitions.get(i).insert(newState, startRange, endRange, this::onRegionTransition);
            }
            aspectMask >>>= 1;
            i++;
        }
    }

    private int tmpAspectMask;
    private ImageBarrierProvider tmpBarrier;

    private void onRegionTransition(ListSpacePartition<State>.Partition srcP, State dst) {
        if(this.tmpBarrier == null) {
            return;
        }

        ImageSubresourceRange transitionRange = convertToRange(this.tmpAspectMask, srcP.getRegionStart(), srcP.getRegionEnd());
        State src = srcP.getState();

        if(src.queue != dst.queue) {
            this.tmpBarrier.addImageTransferBarrier(src.queue, dst.queue, src.layout, dst.layout, src.pendingWrites, src.pendingStages, dst.pendingWrites, dst.pendingStages, transitionRange);
        } else {
            this.tmpBarrier.addImageBarrier(src.layout, dst.layout, src.pendingWrites, src.pendingStages, dst.pendingWrites, dst.pendingStages, transitionRange);
        }
    }

    private static ImageSubresourceRange convertToRange(int aspectMask, int[] start, int[] end) {
        return new ImageSubresourceRange(aspectMask, start[0], end[0] - start[0], start[1], end[1] - start[1]);
    }

    private record State(
            int queue,
            int layout,
            int pendingWrites,
            int pendingStages) {
    }

    public interface ImageBarrierProvider {
        void addImageBarrier(int srcLayout, int dstLayout, int srcAccessMask, int srcStageMask, int dstAccessMask, int dstStageMask, @NotNull ImageSubresourceRange range);

        void addImageTransferBarrier(int srcQueue, int dstQueue, int srcLayout, int dstLayout, int srcAccessMask, int srcStageMask, int dstAccessMask, int dstStageMask, @NotNull ImageSubresourceRange range);
    }
}
