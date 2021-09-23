package graphics.kiln.rosella.util;

import java.util.concurrent.atomic.AtomicLong;

public class IDProvider {
    private static final AtomicLong nextID = new AtomicLong(1);

    public static long getNextID() {
        return nextID.getAndIncrement();
    }
}
