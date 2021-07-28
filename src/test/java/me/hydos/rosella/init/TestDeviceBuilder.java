package me.hydos.rosella.init;

import me.hydos.rosella.annotations.RequiresVulkan;
import me.hydos.rosella.device.VulkanDevice;
import me.hydos.rosella.init.features.ApplicationFeature;
import me.hydos.rosella.init.features.SimpleApplicationFeature;
import me.hydos.rosella.test_utils.VulkanTestInstance;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class TestDeviceBuilder {

    @Test
    @RequiresVulkan
    void testMinimalBuild() {
        InitializationRegistry registry = new InitializationRegistry();
        try (VulkanTestInstance instance = new VulkanTestInstance(registry)) {

            assertDoesNotThrow(() -> {
                DeviceBuilder builder = new DeviceBuilder(instance.instance, registry);
                VulkanDevice device = builder.build();
                device.destroy();
            });
        }
    }

    @Test
    @RequiresVulkan
    void testBasicFeatures() {
        ArrayList<ApplicationFeature> features = new ArrayList<>();
        features.add(new SimpleApplicationFeature("t:test1", null));
        features.add(new SimpleApplicationFeature("t:test2", null));

        ArrayList<ApplicationFeature> failFeatures = new ArrayList<>();
        failFeatures.add(new SimpleApplicationFeature("t:test3", Set.of("t:fail")));

        InitializationRegistry registry1 = new InitializationRegistry();
        features.forEach(registry1::registerApplicationFeature);
        failFeatures.forEach(registry1::registerApplicationFeature);
        try(VulkanTestInstance instance = new VulkanTestInstance(registry1)) {
            assertDoesNotThrow(() -> {
                DeviceBuilder builder = new DeviceBuilder(instance.instance, registry1);
                VulkanDevice device = builder.build();

                features.forEach((feature) ->
                    assertTrue(device.isFeatureEnabled(feature.name))
                );

                failFeatures.forEach((feature) ->
                    assertFalse(device.isFeatureEnabled(feature.name))
                );

                device.destroy();
            });
        }
    }

    @Test
    @RequiresVulkan
    void testRequiredFeatures() {
        ArrayList<ApplicationFeature> features = new ArrayList<>();
        features.add(new SimpleApplicationFeature("t:test1", null));
        features.add(new SimpleApplicationFeature("t:test2", null));

        InitializationRegistry registry1 = new InitializationRegistry();
        features.forEach(registry1::registerApplicationFeature);
        registry1.addRequiredApplicationFeature("t:test2");
        try(VulkanTestInstance instance = new VulkanTestInstance(registry1)) {
            assertDoesNotThrow(() -> {
                DeviceBuilder builder = new DeviceBuilder(instance.instance, registry1);
                VulkanDevice device = builder.build();

                features.forEach((feature) ->
                    assertTrue(device.isFeatureEnabled(feature.name))
                );

                device.destroy();
            });
        }
    }

    @Test
    @RequiresVulkan
    void testRequiredFeaturesFail() {
        ArrayList<ApplicationFeature> features = new ArrayList<>();
        features.add(new SimpleApplicationFeature("t:test1", null));
        features.add(new SimpleApplicationFeature("t:test2", Set.of("t:fail")));

        InitializationRegistry registry1 = new InitializationRegistry();
        features.forEach(registry1::registerApplicationFeature);
        registry1.addRequiredApplicationFeature("t:test2");
        try(VulkanTestInstance instance = new VulkanTestInstance(registry1)) {
            assertThrows(RuntimeException.class, () -> {
                DeviceBuilder builder = new DeviceBuilder(instance.instance, registry1);
                VulkanDevice device = builder.build();
                device.destroy();
            });
        }
    }

    @Test
    @RequiresVulkan
    void testDependencyOrdering() {
        ArrayList<ApplicationFeature> features = new ArrayList<>();
        features.add(new SimpleApplicationFeature("t:test9", null));
        features.add(new SimpleApplicationFeature("t:test2", Set.of("t:test9")));
        features.add(new SimpleApplicationFeature("t:test3", Set.of("t:test2")));
        features.add(new SimpleApplicationFeature("t:test4", Set.of("t:test9", "t:test2")));
        features.add(new SimpleApplicationFeature("t:test5", Set.of("t:test2")));
        features.add(new SimpleApplicationFeature("t:test6", Set.of("t:test4")));
        features.add(new SimpleApplicationFeature("t:test7", Set.of("t:test4")));

        ArrayList<ApplicationFeature> failFeatures = new ArrayList<>();
        failFeatures.add(new SimpleApplicationFeature("t:test11", Set.of("t:fail")));
        failFeatures.add(new SimpleApplicationFeature("t:test12", Set.of("t:test11")));
        failFeatures.add(new SimpleApplicationFeature("t:test13", Set.of("t:test12", "t:test2")));
        failFeatures.add(new SimpleApplicationFeature("t:test14", Set.of("t:test13")));

        Random rand = new Random(293840972);
        Collections.shuffle(features, rand);
        Collections.shuffle(failFeatures, rand);

        InitializationRegistry registry = new InitializationRegistry();
        features.forEach(registry::registerApplicationFeature);
        failFeatures.forEach(registry::registerApplicationFeature);
        try(VulkanTestInstance instance = new VulkanTestInstance(registry)) {

            assertDoesNotThrow(() -> {
                DeviceBuilder builder = new DeviceBuilder(instance.instance, registry);
                VulkanDevice device = builder.build();

                features.forEach((feature) ->
                    assertTrue(device.isFeatureEnabled(feature.name))
                );

                failFeatures.forEach((feature) ->
                    assertFalse(device.isFeatureEnabled(feature.name))
                );

                device.destroy();
            });
        }
    }
}
