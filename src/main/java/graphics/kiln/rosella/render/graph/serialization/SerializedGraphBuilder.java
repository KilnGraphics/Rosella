package graphics.kiln.rosella.render.graph.serialization;

import graphics.kiln.rosella.render.graph.memory.AllocationSetBuilder;
import graphics.kiln.rosella.render.graph.memory.BufferAllocationRequirements;
import graphics.kiln.rosella.render.graph.ops.*;
import graphics.kiln.rosella.render.graph.resources.*;
import graphics.kiln.rosella.util.ImageFormat;
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.vulkan.VK10;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReentrantLock;

public class SerializedGraphBuilder {

    private final ReentrantLock lock = new ReentrantLock();
    private boolean opsLocked = false;
    private boolean isBuilding = false;

    private final ObjectArrayList<OpMetadata> ops = new ObjectArrayList<>();
    private final Map<Long, BufferMetadata> buffers = new Long2ObjectAVLTreeMap<>();
    private final Map<Long, ImageMetadata> images = new Long2ObjectAVLTreeMap<>();

    private final List<SerializationBuilder> submissions = new ObjectArrayList<>();
    private AllocationSetBuilder allocationSetBuilder;
    private OpMetadata currentOp;
    private SerializationBuilder currentSubmission;
    private int currentIndex = 0;

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

            this.buffers.computeIfAbsent(buffer.getID(), BufferMetadata::new).configureInternal(spec, preserve, 0);

        } finally {
            this.lock.unlock();
        }
    }

    public void configureExternalBuffer(BufferReference buffer, @Nullable BufferAccessState initialState) {
        try {
            this.lock.lock();

            if(this.isBuilding) {
                throw new RuntimeException("Cannot configure resources while building");
            }

            this.buffers.computeIfAbsent(buffer.getID(), BufferMetadata::new).configureExternal(initialState);

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

    public Future<SerializedGraph> build() {
        try {
            this.lock.lock();
            this.opsLocked = true;
            this.isBuilding = true;

            CompletableFuture<SerializedGraph> result = new CompletableFuture<>();
            buildInternal(result);
            return result;
        } finally {
            this.lock.unlock();
        }
    }

    private void buildInternal(CompletableFuture<SerializedGraph> result) {
        try {
            this.lock.lock();

            this.currentSubmission = null;
            this.currentIndex = 0;
            this.submissions.clear();
            this.allocationSetBuilder = null; // TODO

            for(OpMetadata op : this.ops) {
                serializeOp(op);
            }
            completeSubmission();

            // so much TODO
            for(BufferMetadata buffer : this.buffers.values()) {
            }
            for(ImageMetadata image : this.images.values()) {
            }

            final List<Serialization> serializations = this.submissions.stream().map(SerializationBuilder::build).toList();
            SerializedGraph graph = new SerializedGraph(serializations, 0);
            result.cancel(true);
        } finally {
            this.lock.unlock();
        }
    }

    private void serializeOp(OpMetadata op) {
        if(this.currentSubmission == null) {
            this.currentSubmission = new SerializationBuilder(op.queueFamily);
            
        } else if(this.currentSubmission.queueFamily != op.queueFamily) {
            completeSubmission();
            this.currentSubmission = new SerializationBuilder(op.queueFamily);
        }

        this.currentOp = op;
        op.op.registerResourceUsages(op);
        this.currentSubmission.addOp(op.op);

        this.currentIndex++;
    }

    private void completeSubmission() {
        if(this.currentSubmission.isEmpty()) {
            this.currentSubmission = null;
            return;
        }

        this.submissions.add(this.currentSubmission);

        this.currentSubmission = null;
    }

    private class OpMetadata implements ObjectRegistry, AccessRegistryProvider {
        private final AbstractOp op;
        private final int queueFamily;

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

        @Override
        public BufferAccessRegistry forBuffer(@NotNull BufferReference buffer) {
            return buffers.get(buffer.id);
        }

        @Override
        public ImageAccessRegistry forImage(@NotNull ImageReference image) {
            return images.get(image.id);
        }
    }

    private class ObjectMetadata {
        protected final long id;

        private OpMetadata firstUsed = null;
        private OpMetadata lastUsed = null;

        protected ObjectMetadata(long id) {
            this.id = id;
        }

        protected OpMetadata getFirstUsed() {
            return this.firstUsed;
        }

        protected OpMetadata getLastUsed() {
            return this.lastUsed;
        }

        protected void updateUsage(OpMetadata op) {
            if(this.firstUsed == null) {
                this.firstUsed = op;
            }
            this.lastUsed = op;
        }
    }

    private class BufferMetadata extends ObjectMetadata implements BufferAccessRegistry {

        private final BufferReference reference;

        private BufferSpec specI;
        private BufferAllocationSpec allocationSpecI;

        private BufferAccessState initialStateE;

        private boolean preserveI;

        private int usageFlags = 0;

        private SerializationBuilder pendingSerializationB = null;
        private int pendingIndex = -1;
        private int pendingWritesB = 0;
        private int pendingStagesB = 0;

        private SerializationBuilder buildingSerializationB = null;
        private MemoryBarrierOp buildingBarrierB = null;
        private int buildingAccessesB = 0;
        private int buildingStagesB = 0;

        protected BufferMetadata(@NotNull BufferReference reference) {
            super(reference.id);
            this.reference = reference;
        }

        protected BufferMetadata(long id) {
            super(id);
            this.reference = new BufferReference(id);
        }

        protected void updateUsage(OpMetadata op, int usageFlags) {
            super.updateUsage(op);
            this.usageFlags |= usageFlags;
        }

        protected void configureInternal(@NotNull BufferSpec spec, boolean preserve, int additionalUsageFlags) {
            this.specI = spec;
            this.initialStateE = null;
            this.preserveI = preserve;

            this.allocationSpecI = new BufferAllocationSpec(additionalUsageFlags);
        }

        protected void configureExternal(@Nullable BufferAccessState initialState) {
            this.specI = null;
            this.allocationSpecI = null;

            this.initialStateE = initialState;
        }

        public boolean isInternal() {
            return this.allocationSpecI != null;
        }

        @Override
        public void addAccess(int queueFamily, int accessMask, int stageMask) {
            if(this.buildingSerializationB == null) {
                this.buildingSerializationB = currentSubmission;
                if(this.isInternal()) {
                    allocationSetBuilder.allocateBuffer(this.reference, this.generateAllocationRequirements(), this);
                }
            }

            // No need to test if were building a discard barrier as it can be promoted to a non discard barrier without issues
            if(VulkanBitmasks.hasWriteAccess(this.buildingAccessesB) || VulkanBitmasks.hasWriteAccess(accessMask) || (this.buildingSerializationB.queueFamily != currentSubmission.queueFamily)) {
                this.submitBuildingBarrier();
                this.buildingSerializationB = currentSubmission;
            }

            this.buildingAccessesB |= accessMask;
            this.buildingStagesB |= stageMask;

            if(this.getLastUsed() == currentOp && this.isInternal()) {
                allocationSetBuilder.freeBuffer(this.reference);
            }
        }

        @Override
        public void addDiscardAccess(int stageMask) {
            if(this.buildingSerializationB == null && this.isInternal()) {
                this.buildingSerializationB = currentSubmission;
                allocationSetBuilder.allocateBuffer(this.reference, this.generateAllocationRequirements(), this);
            }

            if(this.buildingAccessesB != 0) {
                // Were currently building a non discard barrier, so we have to submit that first
                this.submitBuildingBarrier();
            }
            this.buildingStagesB |= stageMask;

            this.buildingSerializationB = currentSubmission;
        }

        private void submitBuildingBarrier() {
            boolean updatePending = false;
            if(this.buildingBarrierB != null) {
                if(this.pendingSerializationB.queueFamily != this.buildingSerializationB.queueFamily) {
                    updatePending = true;
                    this.buildingSerializationB.addDependency(this.pendingSerializationB);

                    if(this.buildingStagesB == 0) {
                        this.buildingStagesB = VK10.VK_PIPELINE_STAGE_ALL_COMMANDS_BIT;
                    }

                    this.pendingSerializationB.getBarrier(this.pendingIndex).addBufferReleaseBarrier(this.reference, this.pendingWritesB, this.pendingStagesB, this.buildingSerializationB.queueFamily);
                    this.buildingBarrierB.addBufferAcquireBarrier(this.reference, this.buildingAccessesB, this.buildingStagesB, this.pendingSerializationB.queueFamily);
                }
                if (this.buildingStagesB != 0) {
                    updatePending = true;

                    this.buildingBarrierB.addBufferBarrier(this.reference, this.pendingWritesB, this.pendingStagesB, this.buildingAccessesB, this.buildingStagesB);
                }
            }

            if(updatePending) {
                this.pendingSerializationB = this.buildingSerializationB;
                this.pendingIndex = currentIndex;
                this.pendingWritesB = VulkanBitmasks.getReadAccesses(this.buildingAccessesB);
                this.pendingStagesB = this.buildingStagesB;
            }

            this.buildingBarrierB = null;
            this.buildingAccessesB = 0;
            this.buildingStagesB = 0;
        }

        private BufferAllocationRequirements generateAllocationRequirements() {
            return new BufferAllocationRequirements();
        }
    }

    private class ImageMetadata extends ObjectMetadata implements ImageAccessRegistry {

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

        @Override
        public void addAccess(int queueFamily, int layout, int accessMask, int stageMask, @NotNull ImageSubresourceRange range) {
            throw new RuntimeException("Not implemented yet");
        }

        @Override
        public void addDiscardAccess(int stageMask, @NotNull ImageSubresourceRange range) {
            throw new RuntimeException("Not implemented yet");
        }
    }

    private record BufferAllocationSpec(int additionalUsageFlags) {
    }

    private record ImageAllocationSpec(int additionalUsageFlags) {
    }

    private class SerializationBuilder {
        private final int queueFamily;
        private final ObjectArrayList<QueueRecordable> ops = new ObjectArrayList<>();
        private int lastOpIndex = -1;

        private MemoryBarrierOp lastBarrier = null;
        private int lastBarrierIndex = Integer.MIN_VALUE;

        private Set<SerializationBuilder> dependencies = new ObjectArraySet<>();

        protected SerializationBuilder(int queueFamily) {
            this.queueFamily = queueFamily;
        }

        protected MemoryBarrierOp getBarrier(int minIndex) {
            if(this.lastBarrierIndex < minIndex) {
                if(this.lastOpIndex != -1 && this.lastOpIndex < minIndex) {
                    throw new RuntimeException("Cannot add barrier after specified index to this submission");
                }
                this.lastBarrier = new MemoryBarrierOp(this.queueFamily);
                this.lastBarrierIndex = this.lastOpIndex;

                this.ops.add(this.lastBarrier);
            }

            return this.lastBarrier;
        }

        protected void addOp(AbstractOp op) {
            this.ops.add(op);
            this.lastOpIndex = currentIndex;
        }

        protected void addDependency(SerializationBuilder other) {
            this.dependencies.add(other);
        }

        protected boolean isEmpty() {
            return this.ops.isEmpty();
        }

        protected Serialization build() {
            return null;
        }
    }
}
