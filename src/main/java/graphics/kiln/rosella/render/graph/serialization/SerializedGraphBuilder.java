package graphics.kiln.rosella.render.graph.serialization;

import graphics.kiln.rosella.render.graph.ops.AbstractOp;
import graphics.kiln.rosella.render.graph.ops.ObjectRegistry;
import graphics.kiln.rosella.render.graph.resources.*;
import graphics.kiln.rosella.util.ImageFormat;
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class SerializedGraphBuilder {

    private final ReentrantLock lock = new ReentrantLock();
    private boolean opsLocked = false;
    private boolean isBuilding = false;

    private final ObjectArrayList<OpMetadata> ops = new ObjectArrayList<>();
    private final Map<Long, BufferMetadata> buffers = new Long2ObjectAVLTreeMap<>();
    private final Map<Long, ImageMetadata> images = new Long2ObjectAVLTreeMap<>();

    public void addOps(List<AbstractOp> ops, int queue) {
        try {
            this.lock.lock();

            if(this.opsLocked) {
                throw new RuntimeException("Cannot add new ops to a builder that has had its build function called");
            }

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

    public void configureInternalBuffer(BufferReference buffer, boolean preserve, @NotNull BufferSpec spec) {
        try {
            this.lock.lock();

            if(this.isBuilding) {
                throw new RuntimeException("Cannot configure resources while building");
            }

            this.buffers.computeIfAbsent(buffer.getID(), BufferMetadata::new).configureInternal(preserve, spec, 0);

        } finally {
            this.lock.unlock();
        }
    }

    public void configureExternalBuffer(BufferReference buffer) {
        try {
            this.lock.lock();

            if(this.isBuilding) {
                throw new RuntimeException("Cannot configure resources while building");
            }

            this.buffers.computeIfAbsent(buffer.getID(), BufferMetadata::new).configureExternal(true);

        } finally {
            this.lock.unlock();
        }
    }

    public void configureInternalImage(ImageReference image, boolean preserve, @NotNull ImageSpec spec) {
        try {
            this.lock.lock();

            if(this.isBuilding) {
                throw new RuntimeException("Cannot configure resources while building");
            }

            this.images.computeIfAbsent(image.getID(), ImageMetadata::new).configureInternal(preserve, spec, 0);

        } finally {
            this.lock.unlock();
        }
    }

    public void configureExternalImage(ImageReference image, ImageFormat format, int mipLevels, int arrayLayers) {
        try {
            this.lock.lock();

            if(this.isBuilding) {
                throw new RuntimeException("Cannot configure resources while building");
            }

            this.images.computeIfAbsent(image.getID(), ImageMetadata::new).configureExternal(true, format, mipLevels, arrayLayers);

        } finally {
            this.lock.unlock();
        }
    }

    public void build() {
        try {
            this.lock.lock();
            this.opsLocked = true;


        } finally {
            this.lock.unlock();
        }
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
        public void registerBuffer(BufferReference buffer, int usageFlags) {
            buffers.computeIfAbsent(buffer.getID(), BufferMetadata::new).updateUsage(this, usageFlags);
        }

        @Override
        public void registerImage(ImageReference image) {
            images.computeIfAbsent(image.getID(), ImageMetadata::new).updateUsage(this);
        }

        @Override
        public void registerImage(ImageReference image, int usageFlags) {
            images.computeIfAbsent(image.getID(), ImageMetadata::new).updateUsage(this, usageFlags);
        }
    }

    private class ObjectMetadata {
        protected final long id;

        private OpMetadata firstUsed = null;
        private OpMetadata lastUsed = null;

        protected ObjectMetadata(long id) {
            this.id = id;
        }

        protected void updateUsage(OpMetadata op) {
            if(this.firstUsed == null) {
                this.firstUsed = op;
            }
            this.lastUsed = op;
        }
    }

    private class BufferMetadata extends ObjectMetadata {

        private BufferSpec spec;
        private BufferAllocationSpec allocationSpec;

        private boolean preserve = false;

        private int usageFlags = 0;

        protected BufferMetadata(long id) {
            super(id);
        }

        protected void updateUsage(OpMetadata op, int usageFlags) {
            super.updateUsage(op);
            this.usageFlags |= usageFlags;
        }

        protected void configureInternal(boolean preserve, @NotNull BufferSpec spec, int additionalUsageFlags) {
            this.spec = spec;
            this.preserve = preserve;

            this.allocationSpec = new BufferAllocationSpec(additionalUsageFlags);
        }

        protected void configureExternal(boolean preserve) {
            this.spec = null;
            this.allocationSpec = null;

            this.preserve = preserve;
        }
    }

    private class ImageMetadata extends ObjectMetadata {

        private ImageSpec spec;
        private ImageAllocationSpec allocationSpec;

        private boolean preserve = false;

        private int usageFlags = 0;

        protected ImageMetadata(long id) {
            super(id);
        }

        protected void updateUsage(OpMetadata op, int usageFlags) {
            super.updateUsage(op);
            this.usageFlags |= usageFlags;
        }

        protected void configureInternal(boolean preserve, @NotNull ImageSpec spec, int additionalUsageFlags) {
            this.spec = spec;
            this.preserve = preserve;

            this.allocationSpec = new ImageAllocationSpec(additionalUsageFlags);
        }

        protected void configureExternal(boolean preserve, ImageFormat format, int mipLayers, int arrayLayers) {
            this.allocationSpec = null;
            this.preserve = preserve;

            this.spec = new ImageSpec(format, 0, 0, 0, mipLayers, arrayLayers, null);
        }
    }

    private record BufferAllocationSpec(int additionalUsageFlags) {
    }

    private record ImageAllocationSpec(int additionalUsageFlags) {
    }
}
