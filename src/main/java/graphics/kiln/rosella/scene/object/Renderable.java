package graphics.kiln.rosella.scene.object;

import graphics.kiln.rosella.Rosella;
import graphics.kiln.rosella.memory.MemoryCloseable;
import graphics.kiln.rosella.render.info.InstanceInfo;
import graphics.kiln.rosella.render.info.RenderInfo;

import java.util.concurrent.Future;

/**
 * Contains data for what you want to render
 */
public interface Renderable extends MemoryCloseable {

    /**
     * Called when the Application asked {@link Rosella} to add this to the scene.
     *
     * @param rosella the common fields used by {@link Rosella}
     */
    void onAddedToScene(Rosella rosella);

    /**
     * Called when the command buffers need to be refreshed.
     *
     * @param rosella the instance of the {@link Rosella} engine used.
     */
    void rebuild(Rosella rosella);

    /**
     * Called when the swapchain needs to be resized
     *
     * @param rosella the instance of the {@link Rosella} engine used.
     */
    void hardRebuild(Rosella rosella);

    InstanceInfo getInstanceInfo();

    Future<RenderInfo> getRenderInfo();
}
