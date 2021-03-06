package graphics.kiln.rosella.example.fboWater.ubo;

import graphics.kiln.rosella.scene.object.RenderObject;
import graphics.kiln.rosella.test_utils.NoclipCamera;
import graphics.kiln.rosella.ubo.UboDataProvider;

import java.nio.ByteBuffer;

public class CameraPositionUboDataProvider extends UboDataProvider<RenderObject> {

    public final NoclipCamera camera;

    public CameraPositionUboDataProvider(NoclipCamera camera) {
        this.camera = camera;
    }

    @Override
    public void update(ByteBuffer data, RenderObject renderObject) {
        reset();
        super.writeMatrix4f(renderObject.modelMatrix, data);
        super.writeMatrix4f(renderObject.viewMatrix, data);
        super.writeMatrix4f(renderObject.projectionMatrix, data);
        super.writeVector3f(camera.position, data);
    }

    @Override
    public int getSize() {
        return (16 * Float.BYTES) + // Model Matrix
                (16 * Float.BYTES) + // View Matrix
                (16 * Float.BYTES) + // Projection Matrix
                (4 * Float.BYTES); // Camera Position
    }
}
