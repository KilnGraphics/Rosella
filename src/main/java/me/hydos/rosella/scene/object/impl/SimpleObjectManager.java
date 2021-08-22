package me.hydos.rosella.scene.object.impl;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.hydos.rosella.Rosella;
import me.hydos.rosella.render.renderer.Renderer;
import me.hydos.rosella.render.shader.RawShaderProgram;
import me.hydos.rosella.render.shader.ShaderProgram;
import me.hydos.rosella.scene.object.GlbRenderObject;
import me.hydos.rosella.scene.object.ObjectManager;
import me.hydos.rosella.scene.object.Renderable;
import me.hydos.rosella.vkobjects.VkCommon;

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
