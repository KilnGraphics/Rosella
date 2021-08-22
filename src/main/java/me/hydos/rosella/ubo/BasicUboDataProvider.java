package me.hydos.rosella.ubo;

import me.hydos.rosella.scene.object.RenderObject;

import java.nio.ByteBuffer;

public class BasicUboDataProvider extends UboDataProvider<RenderObject> {

    @Override
    public void update(ByteBuffer data, RenderObject renderObject) {
        reset();
        super.writeMatrix4f(renderObject.modelMatrix, data);
        super.writeMatrix4f(renderObject.viewMatrix, data);
        super.writeMatrix4f(renderObject.projectionMatrix, data);
    }

    @Override
    public int getSize() {
        return (16 * Float.BYTES) + // Model Matrix
                        (16 * Float.BYTES) + // View Matrix
                        (16 * Float.BYTES); // Projection Matrix
    }
}
