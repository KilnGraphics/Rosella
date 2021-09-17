package graphics.kiln.rosella.render.graph.serialization;

import graphics.kiln.rosella.render.graph.ops.AbstractOp;
import graphics.kiln.rosella.render.graph.ops.MemoryBarrierOp;
import graphics.kiln.rosella.render.graph.ops.UsageRegistry;
import graphics.kiln.rosella.render.graph.resources.*;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.vulkan.EXTTransformFeedback;
import org.lwjgl.vulkan.KHRAccelerationStructure;
import org.lwjgl.vulkan.NVDeviceGeneratedCommands;
import org.lwjgl.vulkan.VK10;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SerializedGraphBuilder {

    private final ObjectArrayList<SerializationMeta> serializations = new ObjectArrayList<>();
    private final AtomicLong nextSerializationID = new AtomicLong(0);

    private final Map<Long, BufferMeta> buffers = new Long2ObjectAVLTreeMap<>();
    private final Map<Long, ImageMeta> images = new Long2ObjectAVLTreeMap<>();

    private final AtomicInteger nextSemaphoreID = new AtomicInteger(0);

    private int sequenceNumber = 1;

    public BufferReference addBuffer(int initialQueue, int finalQueue) {
        BufferReference buffer = new BufferReference();
        buffers.put(buffer.getID(), new BufferMeta(buffer, initialQueue, finalQueue));
        return buffer;
    }

    public BufferReference addLocalBuffer() {
        return addBuffer(-1, -2);
    }

    public ImageReference addImage() {
        return null;
    }

    public void addSerialization(int queueFamily, AbstractOp ops) {
        addSerialization(new SerializationMeta(queueFamily, ops));
    }

    public void addSerialization(int queueFamily, AbstractOp ops, Runnable completedCallback) {
        addSerialization(new SerializationMeta(queueFamily, ops));
    }

    public void addSerialization(int queueFamily, AbstractOp ops, Runnable completedCallback, List<Integer> releaseResources) {
        addSerialization(new SerializationMeta(queueFamily, ops));
    }

    public SerializedGraph build() {
        for(BufferMeta buffer : this.buffers.values()) {
            buffer.complete();
        }
        for(ImageMeta image : this.images.values()) {
            image.complete();
        }

        List<Serialization> builtSerializations = new ObjectArrayList<>();
        for(SerializationMeta meta : this.serializations) {
            builtSerializations.add(meta.convertToSerialization());
        }

        Map<Long, SerializedGraph.BufferResource> bufferResources = new Long2ObjectAVLTreeMap<>();
        for(BufferMeta buffer : this.buffers.values()) {
            bufferResources.put(buffer.buffer.getID(), buffer.convertToResource());
        }

        return new SerializedGraph(builtSerializations, bufferResources, this.nextSemaphoreID.get());
    }

    private void addSerialization(SerializationMeta meta) {


        this.serializations.add(meta);
        meta.process();
    }

    private int addSemaphore() {
        return this.nextSemaphoreID.getAndIncrement();
    }

    private class SerializationMeta implements UsageRegistry {
        private final long idMask;

        private Serialization generatedSerialization;

        private long dependencyMask;
        private final List<Integer> waitSemaphores = new IntArrayList();
        private final List<Integer> signalSemaphores = new IntArrayList();

        private final int queueFamily;
        private AbstractOp ops;

        private AbstractOp prevOp = null;

        private MemoryBarrierOp lastBarrier = null;
        private int lastBarrierSequenceNumber = -1;

        private SerializationMeta(int queueFamily, AbstractOp ops) {
            this.idMask = 1L << nextSerializationID.getAndIncrement();
            this.dependencyMask = this.idMask;

            this.queueFamily = queueFamily;
            this.ops = ops;
        }

        private void process() {
            assert(this.prevOp == null);

            AbstractOp current = this.ops;
            while(current != null) {
                current.registerResourceUsages(this);

                this.prevOp = current;
                current = current.getNext();
                sequenceNumber++;
            }
        }

        private Serialization convertToSerialization() {
            if(this.generatedSerialization == null) {
                this.generatedSerialization = new Serialization(this.queueFamily, this.ops, this.waitSemaphores, this.signalSemaphores);
            }
            return this.generatedSerialization;
        }

        protected void insertDependency(SerializationMeta other) {
            assert(other != this);

            if((this.dependencyMask & other.idMask) == 0) {
                this.dependencyMask |= other.dependencyMask;
                int semaphore = addSemaphore();
                other.signalSemaphores.add(semaphore);
                this.waitSemaphores.add(semaphore);
            }
        }

        protected MemoryBarrierOp insertBarrierOp(int minSequenceNumber) {
            if(this.lastBarrier == null || this.lastBarrierSequenceNumber < minSequenceNumber) {
                this.lastBarrier = new MemoryBarrierOp(this.queueFamily);
                this.lastBarrierSequenceNumber = sequenceNumber - 1;

                if(this.prevOp == null) {
                    this.lastBarrier.insertAfter(this.ops);
                    this.ops = this.lastBarrier;
                } else {
                    this.prevOp.insertAfter(this.lastBarrier);

                    if(this.prevOp.getNext() == null) {
                        // This is the last operation so we will not need any new barriers afterwards
                        this.lastBarrierSequenceNumber = Integer.MAX_VALUE;
                    }
                }
            }

            return this.lastBarrier;
        }

        @Override
        public void registerBuffer(BufferReference buffer) {
            buffers.get(buffer.getID()).registerUsage(this);
        }

        @Override
        public void registerBuffer(BufferReference buffer, BufferAccessSet access) {
            access.queueFamily = this.queueFamily;
            buffers.get(buffer.getID()).registerUsage(this, access);
        }

        @Override
        public void registerImage(ImageReference image) {

        }

        @Override
        public void registerImage(ImageReference image, int accessMask, int stageMask, int initialLayout, int finalLayout) {

        }
    }

    protected class ResourceMeta {
        protected SerializationMeta firstAccess = null;
        protected SerializationMeta lastAccess = null;

        protected void registerUsage(SerializationMeta meta) {
            if(this.firstAccess == null) {
                this.firstAccess = meta;
            }
            if(this.lastAccess != null && this.lastAccess != meta) {
                meta.insertDependency(this.lastAccess);
            }
            this.lastAccess = meta;
        }

        protected void complete() {
        }
    }

    protected class BufferMeta extends ResourceMeta {
        private final BufferReference buffer;
        private final int initialQueue;
        private final int finalQueue;

        private MemoryBarrierOp currentBarrierOp = null;
        private BufferAccessHistory.BarrierRequirements currentBarrier = null;

        private SerializationMeta currentOwner = null;
        private int currentSequenceNumber = -1;

        private final BufferAccessHistory history = new BufferAccessHistory();

        public BufferMeta(@NotNull BufferReference buffer, int initialQueue, int finalQueue) {
            this.buffer = buffer;
            this.initialQueue = initialQueue;
            this.finalQueue = finalQueue;
        }

        protected void registerUsage(SerializationMeta meta, BufferAccessSet access) {
            if(this.currentOwner == null) {
                this.currentOwner = meta;
            }

            BufferAccessHistory.BarrierRequirements newBarrier = this.history.addAfter(access);
            if(newBarrier != null) {
                commitCurrentBarrier();

                if(newBarrier.requiresTransfer()) {
                    newBarrier.recordRelease(this.currentOwner.insertBarrierOp(this.currentSequenceNumber), this.buffer);
                }

                this.currentBarrier = newBarrier;
                this.currentBarrierOp = meta.insertBarrierOp(this.currentSequenceNumber);
            }

            this.currentOwner = meta;
            this.currentSequenceNumber = sequenceNumber;

            registerUsage(meta);
        }

        private void commitCurrentBarrier() {
            if(this.currentBarrier != null) {
                this.currentBarrier.commit();
                if(this.currentBarrier.requiresTransfer()) {
                    this.currentBarrier.recordAcquire(this.currentBarrierOp, this.buffer);
                } else {
                    this.currentBarrier.record(this.currentBarrierOp, this.buffer);
                }
                this.currentBarrier = null;
                this.currentBarrierOp = null;
            }
        }

        @Override
        protected void complete() {
            commitCurrentBarrier();

            // TODO add chad barrier
        }

        protected SerializedGraph.BufferResource convertToResource() {
            return new SerializedGraph.BufferResource(this.buffer.getID(), this.firstAccess.generatedSerialization, this.lastAccess.generatedSerialization);
        }
    }

    protected class ImageMeta extends ResourceMeta {
        protected int currentLayout;
    }

    private static final int WRITE_ACCESS_MASK = VK10.VK_ACCESS_SHADER_WRITE_BIT | VK10.VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT | VK10.VK_ACCESS_DEPTH_STENCIL_ATTACHMENT_WRITE_BIT | VK10.VK_ACCESS_TRANSFER_WRITE_BIT | VK10.VK_ACCESS_HOST_WRITE_BIT | VK10.VK_ACCESS_MEMORY_WRITE_BIT | EXTTransformFeedback.VK_ACCESS_TRANSFORM_FEEDBACK_WRITE_BIT_EXT | EXTTransformFeedback.VK_ACCESS_TRANSFORM_FEEDBACK_COUNTER_WRITE_BIT_EXT | KHRAccelerationStructure.VK_ACCESS_ACCELERATION_STRUCTURE_WRITE_BIT_KHR | NVDeviceGeneratedCommands.VK_ACCESS_COMMAND_PREPROCESS_WRITE_BIT_NV;
    private static boolean isWriteAccess(int accessMask) {
        return (accessMask & WRITE_ACCESS_MASK) != 0;
    }
}
