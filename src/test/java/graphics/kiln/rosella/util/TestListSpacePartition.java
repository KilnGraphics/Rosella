package graphics.kiln.rosella.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Iterator;
import java.util.List;

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

        Iterator<ListSpacePartition<Integer>.Partition> partitions = list.iterator();
        while(partitions.hasNext()) {
            ListSpacePartition<Integer>.Partition next = partitions.next();
            Region2D region = new Region2D(next.getRegionStart(), next.getRegionEnd());
            assertTrue(regions.contains(region));
            regions.remove(region);
        }

        assertTrue(regions.isEmpty());
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
