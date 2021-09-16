package graphics.kiln.rosella.render.graph.resources;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.vulkan.VK10;

public class BufferRange {
    public long offset;
    public long length;

    public BufferRange(long offset, long length) {
        this.offset = offset;
        this.length = length;
    }

    public boolean isWholeSize() {
        return this.offset == 0 && this.length == VK10.VK_WHOLE_SIZE;
    }

    public boolean intersects(@NotNull BufferRange other) {
        if(this.isWholeSize() || other.isWholeSize() || this.offset == other.offset) {
            return true;
        }

        if(this.offset < other.offset) {
            return this.offset + this.length >= other.offset;
        } else {
            return other.offset + other.length >= this.offset;
        }
    }
}
