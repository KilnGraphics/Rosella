package me.hydos.rosella.ubo;

import me.hydos.rosella.scene.object.RenderObject;

import java.nio.ByteBuffer;

public class BasicUboDataProvider implements UboDataProvider<RenderObject> {

    @Override
    public void update(ByteBuffer data, RenderObject renderObject) {
        renderObject.modelMatrix.get(data);
        renderObject.viewMatrix.get((16 * Float.BYTES), data);
        renderObject.projectionMatrix.get((16 * Float.BYTES) * 2, data);
    }

    @Override
    public int getSize() {
        return (16 * Float.BYTES) * 3;
    }
}
