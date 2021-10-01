package graphics.kiln.rosella.ubo;

import org.joml.*;

import java.nio.ByteBuffer;

/**
 * Allows for easy manipulation of data in a Uniform Buffer Object (UBO)
 */
public abstract class UboDataProvider<T> {

    //FIXME: add std140 alignment https://www.oreilly.com/library/view/opengl-programming-guide/9780132748445/app09lev1sec2.html
    protected int position;

    /**
     * Should be called every frame
     */
    protected void reset() {
        this.position = 0;
    }

    /**
     * Writes a Matrix3f into a {@link ByteBuffer}
     * @param matrix3f the matrix3f you want to write
     * @param data the {@link ByteBuffer} you want to write into.
     */
    protected void writeMatrix3f(Matrix3f matrix3f, ByteBuffer data) {
        matrix3f.get(position, data);
        position += Float.BYTES * 9;
    }

    /**
     * Writes a Matrix4f into a {@link ByteBuffer}
     * @param matrix4f the matrix4f you want to write
     * @param data the {@link ByteBuffer} you want to write into.
     */
    protected void writeMatrix4f(Matrix4f matrix4f, ByteBuffer data) {
        matrix4f.get(position, data);
        position += Float.BYTES * 16;
    }

    /**
     * Writes a Vector2f into a {@link ByteBuffer}
     * @param vector2f the vector2f you want to write
     * @param data the {@link ByteBuffer} you want to write into.
     */
    protected void writeVector2f(Vector2f vector2f, ByteBuffer data) {
        vector2f.get(position, data);
        position += Float.BYTES * 2;
    }

    /**
     * Writes a Vector3f into a {@link ByteBuffer}
     * @param vector3f the vector3f you want to write
     * @param data the {@link ByteBuffer} you want to write into.
     */
    protected void writeVector3f(Vector3f vector3f, ByteBuffer data) {
        vector3f.get(position, data);
        position += Float.BYTES * 4; // FIXME: std140 moment
    }

    /**
     * Writes a Vector4f into a {@link ByteBuffer}
     * @param vector4f the vector3f you want to write
     * @param data the {@link ByteBuffer} you want to write into.
     */
    protected void writeVector4f(Vector4f vector4f, ByteBuffer data) {
        vector4f.get(position, data);
        position += Float.BYTES * 4;
    }

    /**
     * Writes a float into a {@link ByteBuffer}
     * @param f the float you want to write
     * @param data the {@link ByteBuffer} you want to write into.
     */
    protected void writeFloat(float f, ByteBuffer data) {
        data.putFloat(position, f);
        position += Float.BYTES;
    }

    /**
     * Called every frame
     *
     * @param buffer the buffer to write data to
     * @param thing  the thing you are writing data to
     */
    public abstract void update(ByteBuffer buffer, T thing);

    /**
     * Called on initialization to set the size of the data
     *
     * @return the size of all data you are sending to the ubo
     */
    public abstract int getSize();
}
