package graphics.kiln.rosella.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.function.BiConsumer;

public class ListSpacePartition<T> implements Iterable<ListSpacePartition<T>.Partition> {

    private final int dimensionCount;

    private Partition partitionList = null;

    public ListSpacePartition(int dimensionCount) {
        this.dimensionCount = dimensionCount;
    }

    public ListSpacePartition(int dimensionCount, Iterator<Partition> source) {
        this.dimensionCount = dimensionCount;
        while(source.hasNext()) {
            this.partitionList = new Partition(source.next(), this.partitionList);
        }
    }

    public void insert(@Nullable T newState, @NotNull int[] regionStart, @NotNull int[] regionEnd, @Nullable BiConsumer<Partition, T> transitionFunction) {
        if(this.partitionList == null) {
            if(newState != null) {
                this.partitionList = new Partition(newState, regionStart.clone(), regionEnd.clone());
            }
        } else {
            this.partitionList.insert(null, newState, regionStart.clone(), regionEnd.clone(), transitionFunction);
        }
    }

    public void clear() {
        this.partitionList = null;
    }

    @Override
    @NotNull
    public Iterator<Partition> iterator() {
        return new PartitionIterator<>(this.partitionList);
    }

    public class Partition {
        private Partition next = null;

        @NotNull
        private final T state;

        private final int[] start;
        private final int[] end;

        protected Partition(@NotNull T state, int[] start, int[] end) {
            this.state = state;
            this.start = start;
            this.end = end;
        }

        protected Partition(@NotNull T state, int[] start, int[] end, Partition next) {
            this.next = next;

            this.state = state;
            this.start = start;
            this.end = end;
        }

        protected Partition(Partition other, Partition next) {
            this.next = next;
            this.state = other.state;
            this.start = other.start.clone();
            this.end = other.end.clone();
        }

        public boolean intersects(int[] otherStart, int[] otherEnd) {
            for(int i = 0; i < dimensionCount; i++) {
                if(this.start[i] >= otherEnd[i] || this.end[i] <= otherStart[i]) {
                    return false;
                }
            }
            return true;
        }

        @NotNull
        public T getState() {
            return this.state;
        }

        public int[] getRegionStart() {
            return this.start;
        }

        public int[] getRegionEnd() {
            return this.end;
        }

        protected void insert(Partition previous, T otherState, int[] otherStart, int[] otherEnd, BiConsumer<Partition, T> transitionFunction) {
            Partition insertAfter = this;

            if(this.intersects(otherStart, otherEnd)) {
                for(int i = 0; i < dimensionCount; i++) {
                    if(otherStart[i] > this.start[i]) {
                        int[] splitEnd = this.end.clone();
                        splitEnd[i] = otherStart[i];

                        Partition split = new Partition(this.state, this.start.clone(), splitEnd, this);
                        if(previous != null) {
                            previous.next = split;
                        } else {
                            partitionList = split;
                        }
                        previous = split;

                        this.start[i] = otherStart[i];
                    }
                    if(otherEnd[i] < this.end[i]) {
                        int[] splitStart = this.start.clone();
                        splitStart[i] = otherEnd[i];

                        Partition split = new Partition(this.state, splitStart, this.end.clone(), this);
                        if(previous != null) {
                            previous.next = split;
                        } else {
                            partitionList = split;
                        }
                        previous = split;

                        this.end[i] = otherEnd[i];
                    }
                }
                previous.next = next;
                insertAfter = previous;

                if(!this.state.equals(otherState) && transitionFunction != null) {
                    transitionFunction.accept(this, otherState);
                }
            }

            if(insertAfter.next != null) {
                insertAfter.next.insert(this, otherState, otherStart, otherEnd, transitionFunction);
            } else {
                if(otherState != null) {
                    insertAfter.next = new Partition(otherState, otherStart, otherEnd);
                }
            }
        }
    }

    private static class PartitionIterator<T> implements Iterator<ListSpacePartition<T>.Partition> {

        private ListSpacePartition<T>.Partition next;

        protected PartitionIterator(ListSpacePartition<T>.Partition initial) {
            this.next = initial;
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public ListSpacePartition<T>.Partition next() {
            ListSpacePartition<T>.Partition result = this.next;
            this.next = this.next.next;
            return result;
        }
    }
}
