package me.hydos.rosella.init;

import java.util.*;

/**
 * A class used to collect any callbacks that are used for device and instance initialization.
 */
public class InitializationRegistry {

    private boolean enableValidation = false;

    private final Set<String> requiredInstanceExtensions = new HashSet<>();
    private final Set<String> optionalInstanceExtensions = new HashSet<>();
    private final Set<String> requiredInstanceLayers = new HashSet<>();
    private final Set<String> optionalInstanceLayers = new HashSet<>();

    private final Map<String, MarkedFeature> features = new HashMap<>();
    private final Set<String> requiredFeatures = new HashSet<>();

    public void setEnableValidation(boolean enable) {
        this.enableValidation = enable;
    }

    public boolean getEnableValidation() {
        return this.enableValidation;
    }

    public void addRequiredInstanceLayer(String layer) {
        this.requiredInstanceLayers.add(layer);
    }

    public void addOptionalInstanceLayer(String layer) {
        this.optionalInstanceLayers.add(layer);
    }

    public void addRequiredInstanceExtensions(String extension) {
        this.requiredInstanceExtensions.add(extension);
    }

    public void addOptionalInstanceExtension(String extension) {
        this.optionalInstanceExtensions.add(extension);
    }

    /**
     * Marks a feature as required. This means that during device selection no device will be used
     * that does not support all required features.
     *
     * @param name The name of the feature that is required. The feature does not have to be registered yet.
     */
    public void addRequiredApplicationFeature(String name) {
        this.requiredFeatures.add(name);
    }

    /**
     * Returns an unmodifiable set of all required features.
     *
     * @return A unmodifiable set of all required features.
     */
    public Set<String> getRequiredApplicationFeatures() {
        return Collections.unmodifiableSet(this.requiredFeatures);
    }

    /**
     * Registers a application feature into this registry.
     *
     * @param feature The feature to register
     */
    public void registerApplicationFeature(ApplicationFeature feature) {
        if(this.features.containsKey(feature.name)) {
            throw new RuntimeException("Feature " + feature.name + " is already registered");
        }

        features.put(feature.name, new MarkedFeature(feature));
    }

    /**
     * Topologically sorts all features and returns them as a list.
     * The list can be iterated from beginning to end to ensure all dependencies are always met.
     *
     * @return A topologically sorted list of all registered ApplicationFeatures.
     */
    public List<ApplicationFeature> getOrderedFeatures() {
        ArrayList<ApplicationFeature> sortedFeatures = new ArrayList<>();
        sortedFeatures.ensureCapacity(this.features.size());

        this.features.values().forEach(feature -> feature.mark = MarkedFeature.Mark.UNMARKED);

        for(MarkedFeature feature : features.values()) {
            if(feature.mark == MarkedFeature.Mark.UNMARKED) {
                sortVisit(feature, sortedFeatures);
            }
        }

        return sortedFeatures;
    }

    private void sortVisit(MarkedFeature feature, ArrayList<ApplicationFeature> sorted) {
        if(feature.mark == MarkedFeature.Mark.PROCESSED) {
            return;
        }
        if(feature.mark == MarkedFeature.Mark.PROCESSING) {
            throw new RuntimeException("Dependency graph of application features is not acyclic!");
        }

        feature.mark = MarkedFeature.Mark.PROCESSING;

        for(String dependency : feature.feature.dependencies) {
            MarkedFeature next = this.features.get(dependency);
            if(next != null) {
                sortVisit(next, sorted);
            }
        }

        feature.mark = MarkedFeature.Mark.PROCESSED;
        sorted.add(feature.feature);
    }

    private static class MarkedFeature {
        public final ApplicationFeature feature;
        public Mark mark = Mark.UNMARKED;

        public MarkedFeature(ApplicationFeature feature) {
            this.feature = feature;
        }

        public enum Mark {
            UNMARKED,
            PROCESSING,
            PROCESSED,
        }
    }
}
