package graphics.kiln.rosella.render.graph.serialization;

import graphics.kiln.rosella.render.graph.memory.AllocationSet;
import graphics.kiln.rosella.render.graph.memory.AllocationSetBuilder;
import graphics.kiln.rosella.render.graph.ops.*;
import graphics.kiln.rosella.render.graph.resources.*;
import it.unimi.dsi.fastutil.longs.Long2ObjectAVLTreeMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectImmutableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

public class ResourceConfigurationSet {

    private final Semaphore accessLock = new Semaphore(1);

    private final Map<Long, BufferMetadata> buffers = new Long2ObjectAVLTreeMap<>();
    private final Map<Long, ImageMetadata> images = new Long2ObjectAVLTreeMap<>();

    private Iterable<AbstractOp> ops;
    private AllocationSetBuilder allocBuilder;

    private int currentOpIndex;
    private SerializationBuilder currentSerialization;

    public void configureExternalBuffer(@NotNull BufferReference buffer) throws InterruptedException {
        accessLock.acquire();
        try {
            buffers.put(buffer.getID(), new ExternalBufferMetadata(buffer));
        } finally {
            accessLock.release();
        }
    }

    public void configureInternalBuffer(@NotNull BufferReference buffer, @NotNull BufferSpec spec) throws InterruptedException {
        configureInternalBuffer(buffer, spec, false);
    }

    public void configureInternalBuffer(@NotNull BufferReference buffer, @NotNull BufferSpec spec, boolean preserve) throws InterruptedException {
        accessLock.acquire();
        try {
            buffers.put(buffer.getID(), new InternalBufferMetadata(buffer, spec));
        } finally {
            accessLock.release();
        }
    }

    public void configureExternalImage(
            @NotNull ImageReference image,
            @NotNull ImageSubresourceSpec subresourceSpec,
            @Nullable Collection<ImageState> preState,
            @Nullable Collection<ImageState> postState) throws InterruptedException {

        accessLock.acquire();
        try {
            images.put(image.getID(), new ExternalImageMetadata(image, subresourceSpec, preState, postState));
        } finally {
            accessLock.release();
        }
    }

    public Future<SerializedGraph> build(@NotNull Iterable<AbstractOp> ops, @NotNull AllocationSetBuilder allocation) throws InterruptedException {
        accessLock.acquire();
        try {
            this.ops = ops;
            this.allocBuilder = allocation;

            SerializedGraph result = buildInternal();

            return CompletableFuture.completedFuture(result);
        } finally {
            accessLock.release();
        }
    }

    private SerializedGraph buildInternal() {
        dispatchPreBuild();

        registerUsages();

        dispatchPostBuild();

        this.allocBuilder = null;
        this.ops = null;

        return null;
    }

    private void registerUsages() {
        this.currentOpIndex = 0;

        for(AbstractOp op : this.ops) {
            op.registerObjects(this.objectRegistry);
            this.currentOpIndex++;
        }
    }

    private void dispatchPreBuild() {
        for(BufferMetadata meta : this.buffers.values()) {
            meta.preBuild();
        }
        for(ImageMetadata meta : this.images.values()) {
            meta.preBuild();
        }
    }

    private void dispatchPostBuild() {
        for(BufferMetadata meta : this.buffers.values()) {
            meta.postBuild();
        }
        for(ImageMetadata meta : this.images.values()) {
            meta.postBuild();
        }
    }

    private abstract static class ObjectMetadata {
        public abstract void preBuild();

        public abstract void postBuild();
    }

    private abstract class BufferMetadata extends ObjectMetadata {
        public final BufferReference reference;

        private int firstUsedIndex;
        private int lastUsedIndex;

        protected BufferMetadata(@NotNull BufferReference reference) {
            this.reference = reference;
        }

        public void registerUsage(int opIndex) {
            if(opIndex < this.firstUsedIndex) {
                this.firstUsedIndex = opIndex;
            }
            if(opIndex > this.lastUsedIndex) {
                this.lastUsedIndex = opIndex;
            }
        }

        public void registerUsage(int opIndex, int usage) {
            registerUsage(opIndex);
        }

        @Override
        public void preBuild() {
            this.firstUsedIndex = Integer.MAX_VALUE;
            this.lastUsedIndex = Integer.MIN_VALUE;
        }

        @Override
        public void postBuild() {
        }
    }

    private class ExternalBufferMetadata extends BufferMetadata {
        public ExternalBufferMetadata(@NotNull BufferReference reference) {
            super(reference);
        }
    }

    private class InternalBufferMetadata extends BufferMetadata {
        private final BufferSpec spec;

        private int usageFlags;

        public InternalBufferMetadata(@NotNull BufferReference reference, @NotNull BufferSpec spec) {
            super(reference);
            this.spec = spec;
        }

        @Override
        public void registerUsage(int opIndex, int usage) {
            super.registerUsage(opIndex, usage);
            this.usageFlags |= usage;
        }

        @Override
        public void preBuild() {
            this.usageFlags = 0;
        }
    }

    private abstract class ImageMetadata extends ObjectMetadata implements ImageAccessRegistry {
        public final ImageReference reference;
        public final ImageSubresourceSpec subresourceSpec;

        public final List<ImageState> preState;
        public final List<ImageState> postState;

        private int firstUsedIndex;
        private int lastUsedIndex;

        protected ImageMetadata(@NotNull ImageReference reference, @NotNull ImageSubresourceSpec subresourceSpec, @Nullable Collection<ImageState> preState, @Nullable Collection<ImageState> postState) {
            this.reference = reference;
            this.subresourceSpec = subresourceSpec;

            // TODO validate input

            if(preState == null) {
                this.preState = Collections.emptyList();
            } else {
                this.preState = new ObjectImmutableList<>(preState);
            }
            if(postState == null) {
                this.postState = Collections.emptyList();
            } else {
                this.postState = new ObjectImmutableList<>(postState);
            }
        }

        public void registerUsage(int opIndex) {
            if(opIndex < this.firstUsedIndex) {
                this.firstUsedIndex = opIndex;
            }
            if(opIndex > this.lastUsedIndex) {
                this.lastUsedIndex = opIndex;
            }
        }

        public void registerUsage(int opIndex, int usage) {
            registerUsage(opIndex);
        }

        @Override
        public void preBuild() {
            this.firstUsedIndex = Integer.MAX_VALUE;
            this.lastUsedIndex = Integer.MIN_VALUE;
        }

        @Override
        public void postBuild() {
        }

        @Override
        public void addAccess(int queueFamily, int layout, int accessMask, int stageMask, @NotNull ImageSubresourceRange range) {

        }

        @Override
        public void addDiscardAccess(int stageMask, @NotNull ImageSubresourceRange range) {

        }
    }

    private class ExternalImageMetadata extends ImageMetadata {
        public ExternalImageMetadata(@NotNull ImageReference reference, @NotNull ImageSubresourceSpec subresourceSpec, @Nullable Collection<ImageState> preState, @Nullable Collection<ImageState> postState) {
            super(reference, subresourceSpec, preState, postState);
        }
    }

    private class InternalImageMetadata extends ImageMetadata {
        private final ImageSpec spec;

        private int usageFlags;

        public InternalImageMetadata(@NotNull ImageReference reference, @NotNull ImageSpec spec, @Nullable Collection<ImageState> preState, @Nullable Collection<ImageState> postState) {
            super(reference, spec.getSubresourceSpec(), preState, postState);
            this.spec = spec;
        }

        @Override
        public void registerUsage(int opIndex, int usage) {
            super.registerUsage(opIndex, usage);
            this.usageFlags |= usage;
        }

        @Override
        public void preBuild() {
            this.usageFlags = 0;
        }
    }

    private static class SerializationBuilder {
        public final int queueFamily;

        private final ObjectArrayList<QueueRecordable> ops = new ObjectArrayList<>();

        /**
         * The index the preBarrier would have if it was placed as the first instruction in the serialization
         */
        private final int startIndex;

        /**
         * The index the postBarrier would have if it was placed as the last instruction in the serialization.
         */
        private int endIndex;

        private MemoryBarrierOp preBarrier = null;
        private int preBarrierIndex = Integer.MAX_VALUE;

        private MemoryBarrierOp postBarrier = null;
        private int postBarrierIndex = Integer.MIN_VALUE;

        public SerializationBuilder(int queueFamily, int startIndex) {
            this.queueFamily = queueFamily;
            this.startIndex = startIndex;
            this.endIndex = startIndex + 1;
        }

        public void addOp(QueueRecordable op, int opIndex) {
            this.ops.add(op);
            this.endIndex = opIndex;
        }

        public MemoryBarrierOp getBarrierBefore(int index) {
            if(this.preBarrierIndex > index) {
                if(index < this.startIndex) {
                    throw new IllegalStateException("Requested a barrier before than the first op of the serialization");
                }
                this.preBarrierIndex = this.startIndex;
                this.preBarrier = new MemoryBarrierOp(this.queueFamily);
                this.ops.add(0, this.preBarrier);

                if(this.postBarrier == null) {
                    this.postBarrier = this.preBarrier;
                    this.postBarrierIndex = this.preBarrierIndex - 1;
                }
            }
            return this.preBarrier;
        }

        public MemoryBarrierOp getBarrierAfter(int index) {
            if(this.postBarrierIndex < index) {
                if(index > this.endIndex) {
                    throw new IllegalStateException("Requested a barrier after than the last op of the serialization");
                }
                this.postBarrierIndex = this.endIndex;
                this.postBarrier = new MemoryBarrierOp(this.queueFamily);
                this.ops.add(this.postBarrier);

                if(this.preBarrier == null) {
                    this.preBarrier = this.postBarrier;
                    this.preBarrierIndex = this.postBarrierIndex + 1;
                }
            }
            return this.postBarrier;
        }
    }

    private final ObjectRegistry objectRegistry = new ObjectRegistry() {
        @Override
        public void registerBuffer(BufferReference buffer) {
            buffers.get(buffer.getID()).registerUsage(currentOpIndex);
        }

        @Override
        public void registerBuffer(BufferReference buffer, int usageFlags) {
            buffers.get(buffer.getID()).registerUsage(currentOpIndex, usageFlags);
        }

        @Override
        public void registerImage(ImageReference image) {
            images.get(image.getID()).registerUsage(currentOpIndex);
        }

        @Override
        public void registerImage(ImageReference image, int usageFlags) {
            images.get(image.getID()).registerUsage(currentOpIndex, usageFlags);
        }
    };

    private final AccessRegistryProvider accessRegistryProvider = new AccessRegistryProvider() {
        @Override
        public BufferAccessRegistry forBuffer(@NotNull BufferReference buffer) {
            return null;
        }

        @Override
        public ImageAccessRegistry forImage(@NotNull ImageReference image) {
            return images.get(image.getID());
        }
    };
}
