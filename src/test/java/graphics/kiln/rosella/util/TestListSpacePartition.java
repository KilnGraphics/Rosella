package graphics.kiln.rosella.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestListSpacePartition {

    @Test
    public void testDisjointInsert() {
        ListSpacePartition<Integer> list = new ListSpacePartition<>(2);

        List<Region2D> regions = new ObjectArrayList<>();
        regions.add(new Region2D(0, 1, 0, 1));
        regions.add(new Region2D(0, 2, 1, 2));
        regions.add(new Region2D(-1, 0, -1, 0));
        regions.add(new Region2D(1000, 2000, 47598, 243939));
        regions.add(new Region2D(5, 10, 5, 10));
        regions.add(new Region2D(0, 5, 5, 10));
        regions.add(new Region2D(5, 10, 0, 5));

        int nextID = 0;
        for(Region2D region : regions) {
            list.insert(nextID++, region.getStart(), region.getEnd(), (a, b) -> fail());
        }

        for (ListSpacePartition<Integer>.Partition next : list) {
            Region2D region = new Region2D(next.getRegionStart(), next.getRegionEnd());
            assertTrue(regions.contains(region));
            regions.remove(region);
        }

        assertTrue(regions.isEmpty());
    }

    @Test
    public void testTransitionInsert() {
        ListSpacePartition<Integer> list = new ListSpacePartition<>(2);

        final AtomicBoolean called = new AtomicBoolean(false);

        list.insert(0, new int[]{0, 0}, new int[]{2, 2}, (a, b) -> fail());
        list.insert(1, new int[]{1, 1}, new int[]{2, 2}, (old, v) -> {
            assertEquals(0, (int) old.getState());
            assertEquals(1, old.getRegionStart()[0]);
            assertEquals(1, old.getRegionStart()[1]);
            assertEquals(2, old.getRegionEnd()[0]);
            assertEquals(2, old.getRegionEnd()[1]);
            assertEquals(1, (int) v);
            called.set(true);
        });
        assertTrue(called.get());

        List<Region2D> expectedRegions = new ObjectArrayList<>();
        expectedRegions.add(new Region2D(0, 1, 0, 2));
        expectedRegions.add(new Region2D(1, 2, 0, 1));
        expectedRegions.add(new Region2D(1, 2, 1, 2));
        for (ListSpacePartition<Integer>.Partition next : list) {
            Region2D region = new Region2D(next.getRegionStart(), next.getRegionEnd());
            assertTrue(expectedRegions.contains(region));
            expectedRegions.remove(region);
        }
        assertTrue(expectedRegions.isEmpty());

        list.clear();
        list.insert(0, new int[]{0, 0}, new int[]{2, 2}, (a, b) -> fail());
        list.insert(0, new int[]{5, 5}, new int[]{100, 100}, (a, b) -> fail());
        list.insert(1, new int[]{1, 1}, new int[]{4, 4}, (old, v) -> {
            assertEquals(0, (int) old.getState());
            assertEquals(1, old.getRegionStart()[0]);
            assertEquals(1, old.getRegionStart()[1]);
            assertEquals(2, old.getRegionEnd()[0]);
            assertEquals(2, old.getRegionEnd()[1]);
            assertEquals(1, (int) v);
            called.set(true);
        });

        expectedRegions.add(new Region2D(0, 1, 0, 2));
        expectedRegions.add(new Region2D(1, 2, 0, 1));
        expectedRegions.add(new Region2D(1, 4, 1, 4));
        expectedRegions.add(new Region2D(5, 100, 5, 100));
        for (ListSpacePartition<Integer>.Partition next : list) {
            Region2D region = new Region2D(next.getRegionStart(), next.getRegionEnd());
            assertTrue(expectedRegions.contains(region));
            expectedRegions.remove(region);
        }
        assertTrue(expectedRegions.isEmpty());

        list.clear();
        list.insert(0, new int[]{0, 0}, new int[]{3, 3}, (a, b) -> fail());
        list.insert(1, new int[]{1, 1}, new int[]{2, 2}, (old, v) -> {
            assertEquals(0, (int) old.getState());
            assertEquals(1, old.getRegionStart()[0]);
            assertEquals(1, old.getRegionStart()[1]);
            assertEquals(2, old.getRegionEnd()[0]);
            assertEquals(2, old.getRegionEnd()[1]);
            assertEquals(1, (int) v);
            called.set(true);
        });

        expectedRegions.add(new Region2D(0, 1, 0, 3));
        expectedRegions.add(new Region2D(2, 3, 0, 3));
        expectedRegions.add(new Region2D(1, 2, 0, 1));
        expectedRegions.add(new Region2D(1, 2, 2, 3));
        expectedRegions.add(new Region2D(1, 2, 1, 2));
        for (ListSpacePartition<Integer>.Partition next : list) {
            Region2D region = new Region2D(next.getRegionStart(), next.getRegionEnd());
            assertTrue(expectedRegions.contains(region));
            expectedRegions.remove(region);
        }
        assertTrue(expectedRegions.isEmpty());
    }

    private record Region2D(int startX, int endX, int startY, int endY) {
        public Region2D(int[] start, int[] end) {
            this(
                    start[0],
                    end[0],
                    start[1],
                    end[1]
            );
        }

        public int[] getStart() {
            return new int[]{ this.startX, this.startY };
        }

        public int[] getEnd() {
            return new int[]{ this.endX, this.endY };
        }
    }
}
