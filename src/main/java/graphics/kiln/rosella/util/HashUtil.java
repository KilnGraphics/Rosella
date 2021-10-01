package graphics.kiln.rosella.util;

import org.lwjgl.util.xxhash.XXHash;

import java.nio.ByteBuffer;

public class HashUtil {

    private static final boolean USE_XXHASH_WITH_SEED = Boolean.parseBoolean(System.getProperty("rosella:xxhash_with_seed"));
    private static final int SEED = 0xC7AB;

    public static long hash64(ByteBuffer buffer) {
        if (USE_XXHASH_WITH_SEED) {
            return XXHash.XXH64(buffer, SEED);
        } else {
            return XXHash.XXH3_64bits(buffer);
        }
    }
}
