package graphics.kiln.rosella.scene.object;

import graphics.kiln.rosella.render.fbo.FrameBufferObject;
import graphics.kiln.rosella.render.material.Material;
import graphics.kiln.rosella.ubo.UboDataProvider;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

public class TexturedGuiRenderObject extends RenderObject {

    protected TexturedGuiRenderObject(Material material, Matrix4f projectionMatrix, Matrix4f viewMatrix, Matrix4f modelMatrix, UboDataProvider<RenderObject> dataProvider, FrameBufferObject[] fbo) {
        super(material, projectionMatrix, viewMatrix, modelMatrix, dataProvider, fbo);
        Vector3f colour = new Vector3f(0, 0, 0);
        int size = material.pipeline().getVertexFormat().getSize();
        vertexBuffer = MemoryUtil.memAlloc(size * 4);

        //TODO: remove colour as we dont even use it in the shader. why would we even pass it. what was i thinking?
        vertexBuffer
                .putFloat(-0.5f).putFloat(-0.5f).putFloat(0f)
                .putFloat(colour.x).putFloat(colour.y).putFloat(colour.z)
                .putFloat(0f).putFloat(0f);

        vertexBuffer
                .putFloat(0.5f).putFloat(-0.5f).putFloat(0f)
                .putFloat(colour.x).putFloat(colour.y).putFloat(colour.z)
                .putFloat(1f).putFloat(0f);

        vertexBuffer
                .putFloat(0.5f).putFloat(0.5f).putFloat(0f)
                .putFloat(colour.x).putFloat(colour.y).putFloat(colour.z)
                .putFloat(1f).putFloat(1f);

        vertexBuffer
                .putFloat(-0.5f).putFloat(0.5f).putFloat(0f)
                .putFloat(colour.x).putFloat(colour.y).putFloat(colour.z)
                .putFloat(0f).putFloat(1f);

        this.indices = MemoryUtil.memAlloc(6 * Integer.BYTES);
        this.indices.putInt(0);
        this.indices.putInt(1);
        this.indices.putInt(2);
        this.indices.putInt(2);
        this.indices.putInt(3);
        this.indices.putInt(0);

        this.vertexBuffer.rewind();
        this.indices.rewind();
    }

    public static class Builder extends RenderObject.Builder<TexturedGuiRenderObject.Builder> {

        public Builder z(float z) {
            this.modelMatrix.translate(0, 0, -z);
            return this;
        }

        public Builder translate(Vector2f position) {
            this.modelMatrix.translate(position.x, position.y, 0);
            return this;
        }

        public Builder translate(float x, float y) {
            this.modelMatrix.translate(x, y, 0);
            return this;
        }

        public Builder scale(Vector2f scale) {
            this.modelMatrix.scale(scale.x, scale.y, 1);
            return this;
        }

        public TexturedGuiRenderObject build() {
            check(true);
            return new TexturedGuiRenderObject(material, projectionMatrix, viewMatrix, modelMatrix, uboDataProvider, fbo);
        }
    }
}
