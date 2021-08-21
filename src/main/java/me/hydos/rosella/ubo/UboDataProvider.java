package me.hydos.rosella.ubo;

import java.nio.ByteBuffer;

/**
 * Allows for easy manipulation of data in a Uniform Buffer Object (UBO)
 */
public interface UboDataProvider<T> {

    /**
     * Called every frame
     * @param buffer the buffer to write data to
     * @param thing the thing you are writing data to
     */
    void update(ByteBuffer buffer, T thing);

    int getSize();
}
