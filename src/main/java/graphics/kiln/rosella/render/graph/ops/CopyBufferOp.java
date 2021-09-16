package graphics.kiln.rosella.render.graph.ops;

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

        if(this.srcBuffer == this.dstBuffer) {
            registry.registerBuffer(this.srcBuffer, VK10.VK_ACCESS_TRANSFER_READ_BIT | VK10.VK_ACCESS_TRANSFER_WRITE_BIT, VK10.VK_PIPELINE_STAGE_TRANSFER_BIT, new BufferRange(0, VK10.VK_WHOLE_SIZE));
        } else {
            registry.registerBuffer(this.srcBuffer, VK10.VK_ACCESS_TRANSFER_READ_BIT, VK10.VK_PIPELINE_STAGE_TRANSFER_BIT, new BufferRange(0, VK10.VK_WHOLE_SIZE));
            registry.registerBuffer(this.dstBuffer, VK10.VK_ACCESS_TRANSFER_WRITE_BIT, VK10.VK_PIPELINE_STAGE_TRANSFER_BIT, new BufferRange(0, VK10.VK_WHOLE_SIZE));
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
