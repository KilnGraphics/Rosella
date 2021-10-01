package graphics.kiln.rosella.scene.object.impl;

import graphics.kiln.rosella.Rosella;
import graphics.kiln.rosella.render.renderer.Renderer;
import graphics.kiln.rosella.render.shader.RawShaderProgram;
import graphics.kiln.rosella.render.shader.ShaderProgram;
import graphics.kiln.rosella.scene.object.Renderable;
import graphics.kiln.rosella.vkobjects.VkCommon;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import graphics.kiln.rosella.scene.object.GlbRenderObject;
import graphics.kiln.rosella.scene.object.ObjectManager;

import java.util.List;

/**
 * Just a basic object manager
 */
public class SimpleObjectManager implements ObjectManager {

    private final VkCommon common;
    private final Rosella rosella;
    public final List<Renderable> renderObjects = new ObjectArrayList<>();

    public SimpleObjectManager(Rosella rosella, VkCommon common) {
        this.rosella = rosella;
        this.common = common;
    }

    public SimpleObjectManager(SimpleObjectManager objectManager) {
        this.rosella = objectManager.rosella;
        this.common = objectManager.common;
    }

    @Override
    public Renderable addObject(Renderable obj) {
        obj.onAddedToScene(rosella);
        renderObjects.add(obj);
        return obj;
    }

    @Override
    public ShaderProgram addShader(RawShaderProgram program) {
        return common.shaderManager.getOrCreateShader(program);
    }

    @Override
    public void free() {
        for (Renderable renderObject : renderObjects) {
            renderObject.free(common.device, common.memory);
        }
    }

    @Override
    public void postInit(Renderer renderer) {
    }

    public SimpleObjectManager duplicate() {
        return new SimpleObjectManager(this);
    }

    public void addObjects(List<GlbRenderObject> objects) {
        for (GlbRenderObject glbRenderObject : objects) {
            addObject(glbRenderObject);
        }
    }
}
