package me.hydos.rosella.init.features;

import me.hydos.rosella.device.VulkanQueue;
import me.hydos.rosella.init.DeviceBuilder;

import java.util.concurrent.Future;

/**
 * Tests for display capabilities and allocates a display queue.
 *
 * The returned meta object will be the queue request.
 */
public class DisplayGLFW extends ApplicationFeature {

    public static final String NAME = "rosella:display_glfw";

    public DisplayGLFW() {
        super(NAME);
    }

    @Override
    public DisplayGLFWInstance createInstance() {
        return new DisplayGLFWInstance();
    }

    public static Meta getMetaObject(Object o) {
        if(!(o instanceof Meta)) {
            throw new RuntimeException("Meta object could not be cast to QueueRequest");
        }
        return (Meta) o;
    }

    public class DisplayGLFWInstance extends ApplicationFeature.Instance {

        @Override
        public void testFeatureSupport(DeviceBuilder.DeviceMeta meta) {

        }

        @Override
        public Object enableFeature(DeviceBuilder.DeviceMeta meta) {
            return null;
        }
    }

    public record Meta(Future<VulkanQueue> queue) {
    }
}
