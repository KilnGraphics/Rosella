package graphics.kiln.rosella.render.graph.ops;

import graphics.kiln.rosella.render.graph.resources.BufferAccessSet;
import graphics.kiln.rosella.render.graph.resources.BufferRange;
import graphics.kiln.rosella.render.graph.resources.BufferReference;
import org.lwjgl.vulkan.VK10;

public class CopyBufferOp extends AbstractOp {
    public static final String TYPE_NAME = "CopyBuffer";

    private BufferReference srcBuffer = null;
    private BufferReference dstBuffer = null;

    public CopyBufferOp() {
    }

    public CopyBufferOp setSrcBuffer(BufferReference buffer) {
        this.srcBuffer = buffer;
        return this;
    }

    public CopyBufferOp setDstBuffer(BufferReference buffer) {
        this.dstBuffer = buffer;
        return this;
    }

    public CopyBufferOp addCopyRange() {
        return this;
    }

    @Override
    public void registerResourceUsages(UsageRegistry registry) {
        assert(this.srcBuffer != null && this.dstBuffer != null);

        BufferAccessSet access = new BufferAccessSet();

        if(this.srcBuffer == this.dstBuffer) {
            access.accessMask = VK10.VK_ACCESS_TRANSFER_READ_BIT | VK10.VK_ACCESS_TRANSFER_WRITE_BIT;
            access.stageMask = VK10.VK_PIPELINE_STAGE_TRANSFER_BIT;
            registry.registerBuffer(this.srcBuffer, access);
        } else {
            access.accessMask = VK10.VK_ACCESS_TRANSFER_READ_BIT;
            access.stageMask = VK10.VK_PIPELINE_STAGE_TRANSFER_BIT;
            registry.registerBuffer(this.srcBuffer, access);

            access.accessMask = VK10.VK_ACCESS_TRANSFER_WRITE_BIT;
            registry.registerBuffer(this.dstBuffer, access);
        }
    }

    @Override
    public void record() {

    }

    @Override
    protected String getJsonType() {
        return TYPE_NAME;
    }
}
