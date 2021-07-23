package me.hydos.rosella.dependencygraph;

import java.util.ArrayList;
import java.util.List;

/**
 * The Framework for a Dependency Graph
 */
public abstract class Dependency {

    /**
     * @return the dependencies of the current dependency graph
     */
    public List<Dependency> getDependencies() {
        return new ArrayList<>();
    }

    /**
     * Checks if this dependency can be run at the same time along with another
     *
     * @param dependency the dependency to check
     * @return true if it can be done synchronous, false if it has to be done async
     */
    public abstract boolean canBeSynchronousWith(Dependency dependency);
}
