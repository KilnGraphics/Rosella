package graphics.kiln.rosella.render.graph.ops;

import graphics.kiln.rosella.render.graph.resources.BufferReference;

public class CopyBufferOp extends AbstractOp {
    public static final String TYPE_NAME = "CopyBuffer";

    private BufferReference srcBuffer = null;
    private BufferReference dstBuffer = null;

    public CopyBufferOp() {
    }

    @Override
    public void registerObjects(ObjectRegistry registry) {

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
    public void registerResourceUsages(AccessRegistryProvider registry) {
    }

    @Override
    public void record() {

    }

    @Override
    protected String getJsonType() {
        return TYPE_NAME;
    }
}
