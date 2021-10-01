package graphics.kiln.rosella.example.fboWater.ubo;

import graphics.kiln.rosella.scene.object.RenderObject;
import graphics.kiln.rosella.ubo.UboDataProvider;
import org.joml.Vector4f;

import java.nio.ByteBuffer;

public class ClipPlaneUboDataProvider extends UboDataProvider<RenderObject> {

    public final Vector4f clippingPlane;

    public ClipPlaneUboDataProvider(Vector4f clippingPlane) {
        this.clippingPlane = clippingPlane;
    }

    @Override
    public void update(ByteBuffer data, RenderObject renderObject) {
        reset();
        super.writeMatrix4f(renderObject.modelMatrix, data);
        super.writeMatrix4f(renderObject.viewMatrix, data);
        super.writeMatrix4f(renderObject.projectionMatrix, data);
        super.writeVector4f(clippingPlane, data);
    }

    @Override
    public int getSize() {
        return (16 * Float.BYTES) + // Model Matrix
                (16 * Float.BYTES) + // View Matrix
                (16 * Float.BYTES) + // Projection Matrix
                (4 * Float.BYTES); // Clipping Plane
    }
}
