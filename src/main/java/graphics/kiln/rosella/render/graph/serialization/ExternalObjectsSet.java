package graphics.kiln.rosella.render.graph.serialization;

import java.util.function.BiConsumer;

public interface ExternalObjectsSet {

    void forEachExternalBuffer(BiConsumer<Long, Void> consumer);

    void forEachExternalImage(BiConsumer<Long, Void> consumer);
}
