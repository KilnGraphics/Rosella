package graphics.kiln.rosella.render.graph.ops;

import graphics.kiln.rosella.render.graph.resources.EventReference;
import graphics.kiln.rosella.render.graph.resources.SemaphoreReference;
import graphics.kiln.rosella.util.IDProvider;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;

import java.util.Set;

public class WaitGroup {
    public final long id = IDProvider.getNextID();

    private boolean locked = false;

    private final Set<SemaphoreReference> waitSemaphores = new ObjectArraySet<>();
    private final Set<EventReference> waitEvents = new ObjectArraySet<>();

    public WaitGroup() {
    }

    public void addSemaphore(SemaphoreReference semaphore) {
        if(this.locked) {
            throw new RuntimeException("Cannot modify locked wait group");
        }

        this.waitSemaphores.add(semaphore);
    }

    public void addEvent(EventReference event) {
        if(this.locked) {
            throw new RuntimeException("Cannot modify locked wait group");
        }

        this.waitEvents.add(event);
    }

    public boolean isLocked() {
        return this.locked;
    }

    public void lock() {
        this.locked = true;
    }
}
