package graphics.kiln.rosella.render.graph.resources;

import graphics.kiln.rosella.render.graph.ops.MemoryBarrierOp;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.vulkan.EXTTransformFeedback;
import org.lwjgl.vulkan.KHRAccelerationStructure;
import org.lwjgl.vulkan.NVDeviceGeneratedCommands;
import org.lwjgl.vulkan.VK10;

public class BufferAccessHistory {

    private boolean hasBarrier = false;

    private int postQueueFamily = -1;
    private int postAccessMask = 0;
    private int postStageMask = 0;

    private int preQueueFamily = -1;
    private int preAccessMask = 0;
    private int preStageMask = 0;

    private BarrierRequirements currentBarrier = null;

    public int getPostQueueFamily() {
        return this.postQueueFamily;
    }

    public int getPreQueueFamily() {
        if(this.hasBarrier) {
            return this.preQueueFamily;
        } else {
            return this.postQueueFamily;
        }
    }

    public boolean requiresBarrierAfter(@NotNull BufferAccessSet set) {
        if(this.postQueueFamily == -1) {
            return false;
        }
        return (this.postQueueFamily != set.queueFamily) || (isWriteAccess(this.postAccessMask) && this.postAccessMask != 0) || isWriteAccess(set.accessMask);
    }

    public boolean requiresBarrierAfter(@NotNull BufferAccessHistory history) {
        if(this.postQueueFamily == -1) {
            return false;
        }
        // TODO handle -1 queue family
        if(history.hasBarrier) {
            return this.postQueueFamily != history.preQueueFamily || (isWriteAccess(this.postAccessMask) && this.postAccessMask != 0) || isWriteAccess(history.preAccessMask);
        } else {
            return this.postQueueFamily != history.postQueueFamily || (isWriteAccess(this.postAccessMask) && this.postAccessMask != 0) || isWriteAccess(history.postAccessMask);
        }
    }

    public BarrierRequirements addAfter(@NotNull BufferAccessSet set) {
        if(requiresBarrierAfter(set)) {
            insertBarrier();

            this.postQueueFamily = set.queueFamily;
            this.postAccessMask = set.accessMask;
            this.postStageMask = set.stageMask;
            return this.currentBarrier;

        } else {
            this.postQueueFamily = set.queueFamily;
            this.postAccessMask |= set.accessMask;
            this.postStageMask |= set.stageMask;
            return null;
        }
    }

    public BarrierRequirements checkAfter(@NotNull BufferAccessHistory history) {
        if(requiresBarrierAfter(history)) {
            int dstQueueFamily, dstAccessMask, dstStageMask;
            if(history.hasBarrier) {
                dstQueueFamily = history.preQueueFamily;
                dstAccessMask = history.preAccessMask;
                dstStageMask = history.preStageMask;
            } else {
                dstQueueFamily = history.postAccessMask;
                dstAccessMask = history.postAccessMask;
                dstStageMask = history.postStageMask;
            }

            return new BarrierRequirements(
                    this.postQueueFamily,
                    this.postAccessMask,
                    this.postStageMask,
                    dstQueueFamily,
                    dstAccessMask,
                    dstStageMask
            );
        }
        return null;
    }

    private void insertBarrier() {
        if (currentBarrier != null) {
            this.currentBarrier.commit();
        }

        if(!this.hasBarrier) {
            this.hasBarrier = true;
            this.preQueueFamily = this.postQueueFamily;
            this.preAccessMask = this.postAccessMask;
            this.preStageMask = this.postStageMask;
        }

        this.currentBarrier = new BarrierRequirements(
                this.postQueueFamily,
                this.postAccessMask,
                this.postStageMask
        );
    }

    public class BarrierRequirements {
        private boolean isCommitted;

        private final int srcQueueFamily;
        private final int srcAccessMask;
        private final int srcStageMask;

        private int dstQueueFamily;
        private int dstAccessMask;
        private int dstStageMask;

        private BarrierRequirements(int srcQueueFamily, int srcAccessMask, int srcStageMask) {
            this.isCommitted = false;
            this.srcQueueFamily = srcQueueFamily;
            this.srcAccessMask = srcAccessMask;
            this.srcStageMask = srcStageMask;
        }

        private BarrierRequirements(int srcQueueFamily, int srcAccessMask, int srcStageMask, int dstQueueFamily, int dstAccessMask, int dstStageMask) {
            this.isCommitted = true;
            this.srcQueueFamily = srcQueueFamily;
            this.srcAccessMask = srcAccessMask;
            this.srcStageMask = srcStageMask;
            this.dstQueueFamily = dstQueueFamily;
            this.dstAccessMask = dstAccessMask;
            this.dstStageMask = dstStageMask;
        }

        public void commit() {
            if(!this.isCommitted) {
                this.isCommitted = true;
                this.dstQueueFamily = postQueueFamily;
                this.dstAccessMask = postAccessMask;
                this.dstStageMask = postStageMask;
            }
        }

        public boolean requiresTransfer() {
            return this.srcQueueFamily != getDstQueueFamily();
        }

        public void record(MemoryBarrierOp op, BufferReference buffer) {
            op.addBufferBarrier(buffer, this.srcAccessMask, this.srcStageMask, getDstAccessMask(), getDstStageMask());
        }

        public void record(MemoryBarrierOp release, MemoryBarrierOp acquire, BufferReference buffer) {
            recordRelease(release, buffer);
            recordAcquire(acquire, buffer);
        }

        public void recordRelease(MemoryBarrierOp op, BufferReference buffer) {
            op.addBufferReleaseBarrier(buffer, this.srcAccessMask, this.srcStageMask, getDstQueueFamily());
        }

        public void recordAcquire(MemoryBarrierOp op, BufferReference buffer) {
            op.addBufferAcquireBarrier(buffer, getDstAccessMask(), getDstStageMask(), this.srcQueueFamily);
        }

        private int getDstQueueFamily() {
            if(this.isCommitted) {
                return this.dstQueueFamily;
            } else {
                return postQueueFamily;
            }
        }

        private int getDstAccessMask() {
            if(this.isCommitted) {
                return this.dstAccessMask;
            } else {
                return postAccessMask;
            }
        }

        private int getDstStageMask() {
            if(this.isCommitted) {
                return this.dstStageMask;
            } else {
                return postStageMask;
            }
        }
    }

    private static final int WRITE_ACCESS_MASK = VK10.VK_ACCESS_SHADER_WRITE_BIT | VK10.VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT | VK10.VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT | VK10.VK_ACCESS_TRANSFER_WRITE_BIT | VK10.VK_ACCESS_HOST_WRITE_BIT | VK10.VK_ACCESS_MEMORY_WRITE_BIT | EXTTransformFeedback.VK_ACCESS_TRANSFORM_FEEDBACK_WRITE_BIT_EXT | EXTTransformFeedback.VK_ACCESS_TRANSFORM_FEEDBACK_COUNTER_WRITE_BIT_EXT | KHRAccelerationStructure.VK_ACCESS_ACCELERATION_STRUCTURE_WRITE_BIT_KHR | NVDeviceGeneratedCommands.VK_ACCESS_COMMAND_PREPROCESS_WRITE_BIT_NV;
    private static boolean isWriteAccess(int accessMask) {
        return (accessMask & WRITE_ACCESS_MASK) != 0;
    }
}
