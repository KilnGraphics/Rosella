package graphics.kiln.rosella.util;

import org.lwjgl.util.xxhash.XXHash;

import java.nio.ByteBuffer;

public class HashUtil {

    public static long hash64(ByteBuffer buffer) {
        return XXHash.XXH3_64bits(buffer);
    }
}
