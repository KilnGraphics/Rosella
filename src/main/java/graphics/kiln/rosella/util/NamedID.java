package graphics.kiln.rosella.util;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * Utility class to quickly identify and compare entities while retaining a human readable name.
 *
 * Creating instances is (relatively) slow but comparing existing ones is very fast so it is highly
 * recommended to avoid creating new instances when not necessary. (Also reduces typing mistakes)
 */
public class NamedID implements Comparable<NamedID> {

    public final String name;
    public final long id;

    public NamedID(@NotNull String name) {
        assert(!name.isBlank());

        this.name = name;

        try (MemoryStack stack = MemoryStack.stackPush()) {
            byte[] stringBytes = name.getBytes(StandardCharsets.UTF_8);
            ByteBuffer newBuffer = stack.malloc(stringBytes.length);
            newBuffer.put(0, stringBytes, 0, stringBytes.length);
            this.id = HashUtil.hash64(newBuffer);
            System.out.println(name + " -> " + id);
        }
    }

    @Override
    public int compareTo(@NotNull NamedID other) {
        long diff = this.id - other.id;
        if(diff == 0) {
            return 0;
        }
        if(diff < 0) {
            return -1;
        } else {
            return 1;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NamedID namedID = (NamedID) o;
        return id == namedID.id;
    }

    @Override
    public int hashCode() {
        return (int) (id);
    }
}
