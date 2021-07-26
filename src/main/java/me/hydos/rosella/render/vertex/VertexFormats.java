package me.hydos.rosella.render.vertex;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.Arrays;

public class VertexFormats {

    private static final Int2ObjectMap<VertexFormat> VERTEX_FORMAT_REGISTRY = new Int2ObjectOpenHashMap<>();

    // UV0 = TEXTURE, UV1 = OVERLAY, UV2 = LIGHT
    public static final VertexFormat POSITION_COLOUR4_UV0_UV2_NORMAL = getFormat(VertexFormatElements.POSITION, VertexFormatElements.COLOUR4ub, VertexFormatElements.UVf, VertexFormatElements.UVs, VertexFormatElements.NORMAL, VertexFormatElements.PADDINGb);
    public static final VertexFormat POSITION_COLOUR4_UV0_UV1_UV2_NORMAL = getFormat(VertexFormatElements.POSITION, VertexFormatElements.COLOUR4ub, VertexFormatElements.UVf, VertexFormatElements.UVs, VertexFormatElements.UVs, VertexFormatElements.NORMAL, VertexFormatElements.PADDINGb);
    public static final VertexFormat POSITION_UV0_COLOUR4_UV2 = getFormat(VertexFormatElements.POSITION, VertexFormatElements.UVf, VertexFormatElements.COLOUR4ub, VertexFormatElements.UVs);
    public static final VertexFormat POSITION = getFormat(VertexFormatElements.POSITION);
    public static final VertexFormat POSITION_COLOUR4 = getFormat(VertexFormatElements.POSITION, VertexFormatElements.COLOUR4ub);
    public static final VertexFormat POSITION_COLOUR4_NORMAL = getFormat(VertexFormatElements.POSITION, VertexFormatElements.COLOUR4ub, VertexFormatElements.NORMAL, VertexFormatElements.PADDINGb);
    public static final VertexFormat POSITION_COLOUR4_UV2 = getFormat(VertexFormatElements.POSITION, VertexFormatElements.COLOUR4ub, VertexFormatElements.UVs);
    public static final VertexFormat POSITION_UV0 = getFormat(VertexFormatElements.POSITION, VertexFormatElements.UVf);
    public static final VertexFormat POSITION_COLOUR4_UV0 = getFormat(VertexFormatElements.POSITION, VertexFormatElements.COLOUR4ub, VertexFormatElements.UVf);
    public static final VertexFormat POSITION_UV0_COLOUR4 = getFormat(VertexFormatElements.POSITION, VertexFormatElements.UVf, VertexFormatElements.COLOUR4ub);
    public static final VertexFormat POSITION_COLOUR4_UV0_UV2 = getFormat(VertexFormatElements.POSITION, VertexFormatElements.COLOUR4ub, VertexFormatElements.UVf, VertexFormatElements.UVs);
    public static final VertexFormat POSITION_UV0_UV2_COLOUR4 = getFormat(VertexFormatElements.POSITION, VertexFormatElements.UVf, VertexFormatElements.UVs, VertexFormatElements.COLOUR4ub);
    public static final VertexFormat POSITION_UV0_COLOUR4_NORMAL = getFormat(VertexFormatElements.POSITION, VertexFormatElements.UVf, VertexFormatElements.COLOUR4ub, VertexFormatElements.NORMAL, VertexFormatElements.PADDINGb);
    public static final VertexFormat POSITION_COLOUR3_UV0 = getFormat(VertexFormatElements.POSITION, VertexFormatElements.COLOUR3f, VertexFormatElements.UVf);

    // makes sure we don't waste a ton of memory with duplicates that get caught in the materials cache
    public static VertexFormat getFormat(VertexFormatElement... elements) {
        // for some reason it doesn't calculate a deep hash code for the array, so we have to do it ourselves
        return VERTEX_FORMAT_REGISTRY.computeIfAbsent(Arrays.hashCode(elements), i -> new VertexFormat(elements));
    }
}
