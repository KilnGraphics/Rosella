package graphics.kiln.rosella.graph_dev_test;

import graphics.kiln.rosella.render.graph.ops.AbstractOp;
import graphics.kiln.rosella.render.graph.ops.CopyBufferOp;
import graphics.kiln.rosella.render.graph.resources.BufferReference;
import graphics.kiln.rosella.render.graph.serialization.SerializedGraphBuilder;

public class GraphTest {

    public static void main(String[] args) {
        SerializedGraphBuilder builder = new SerializedGraphBuilder();

        BufferReference bufferA = builder.addBuffer(-1, -1);
        BufferReference bufferB = builder.addBuffer(-1, -1);
        BufferReference bufferC = builder.addBuffer(-1, -1);

        AbstractOp ops = new CopyBufferOp().setSrcBuffer(bufferA).setDstBuffer(bufferB);
        AbstractOp lastOp = ops;
        lastOp.insertAfter(new CopyBufferOp().setSrcBuffer(bufferB).setDstBuffer(bufferB));

        builder.addSerialization(0, ops);

        ops = new CopyBufferOp().setSrcBuffer(bufferA).setDstBuffer(bufferC);

        builder.addSerialization(1, ops);

        System.out.println(builder.build().convertToJson());
    }
}
