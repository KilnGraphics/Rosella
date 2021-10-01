package graphics.kiln.rosella.scene.object;

import graphics.kiln.rosella.render.renderer.Renderer;
import graphics.kiln.rosella.render.shader.RawShaderProgram;
import graphics.kiln.rosella.render.shader.ShaderProgram;

/**
 * Allows for multiple ways for the engine to handle objects.
 */
public interface ObjectManager {

    /**
     * adds an object into the current scene.
     *
     * @param renderable the material to add to the scene
     */
    Renderable addObject(Renderable renderable);

    /**
     * registers a {@link RawShaderProgram} into the engine.
     *
     * @param program the program to register
     */
    ShaderProgram addShader(RawShaderProgram program);

    /**
     * Called when the engine is exiting.
     */
    void free();

    /**
     * Called after an instance of the renderer is created
     *
     * @param renderer the renderer
     */
    void postInit(Renderer renderer);
}

