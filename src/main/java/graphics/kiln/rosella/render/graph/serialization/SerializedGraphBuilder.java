package graphics.kiln.rosella.render.graph.serialization;

import graphics.kiln.rosella.render.graph.ops.AbstractOp;
import graphics.kiln.rosella.render.graph.ops.MemoryBarrierOp;
import graphics.kiln.rosella.render.graph.ops.ObjectRegistry;
import graphics.kiln.rosella.render.graph.ops.UsageRegistry;
import graphics.kiln.rosella.render.graph.resources.*;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.vulkan.EXTTransformFeedback;
import org.lwjgl.vulkan.KHRAccelerationStructure;
import org.lwjgl.vulkan.NVDeviceGeneratedCommands;
import org.lwjgl.vulkan.VK10;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class SerializedGraphBuilder {

    private final ReentrantLock lock = new ReentrantLock();

    private final ObjectArrayList<OpMetadata> ops = new ObjectArrayList<>();
    private final Map<Long, BufferMetadata> buffers = new Long2ObjectAVLTreeMap<>();
    private final Map<Long, ImageMetadata> images = new Long2ObjectAVLTreeMap<>();

    public void addOps(List<AbstractOp> ops, int queue) {
        try {
            this.lock.lock();

            this.ops.ensureCapacity(this.ops.size() + ops.size());
            for(AbstractOp op : ops) {
                op.lock();

                OpMetadata metadata = new OpMetadata(op, queue);
                this.ops.add(metadata);

                op.registerObjects(metadata);
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void build(AllocationRequirementSet allocationRequirements, ExternalObjectsSet externalObjects) {

    }

    private class OpMetadata implements ObjectRegistry {
        private final AbstractOp op;
        private final int queueFamily;

        private List<ObjectMetadata> firstUsed;
        private List<ObjectMetadata> lastUsed;

        protected OpMetadata(@NotNull AbstractOp op, int queueFamily) {
            this.op = op;
            this.queueFamily = queueFamily;
        }

        @Override
        public void registerBuffer(BufferReference buffer) {
            buffers.computeIfAbsent(buffer.getID(), BufferMetadata::new).updateUsage(this);
        }

        @Override
        public void registerImage(ImageReference image) {
            images.computeIfAbsent(image.getID(), ImageMetadata::new).updateUsage(this);
        }
    }

    private class ObjectMetadata {
        private OpMetadata firstUsed = null;
        private OpMetadata lastUsed = null;

        protected void updateUsage(OpMetadata op) {
            if(this.firstUsed == null) {
                this.firstUsed = op;
            }
            this.lastUsed = op;
        }
    }

    private class BufferMetadata extends ObjectMetadata {

        protected BufferMetadata(long id) {
        }
    }

    private class ImageMetadata extends ObjectMetadata {

        protected ImageMetadata(long id) {
        }
    }
}
