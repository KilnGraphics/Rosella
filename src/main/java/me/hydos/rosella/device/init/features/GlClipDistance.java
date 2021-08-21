package me.hydos.rosella.device.init.features;

import me.hydos.rosella.device.init.DeviceBuildConfigurator;
import me.hydos.rosella.device.init.DeviceBuildInformation;
import me.hydos.rosella.util.NamedID;

public class GlClipDistance extends SimpleApplicationFeature {

    public static final NamedID NAME = new NamedID("rosella:gl_clip_distance");

    public GlClipDistance() {
        super(NAME, GlClipDistance::canEnable, GlClipDistance::enable);
    }

    private static boolean canEnable(DeviceBuildInformation deviceBuildInformation) {
        return true; //FIXME: i dont really know a way of checking for this...
    }

    private static void enable(DeviceBuildConfigurator deviceBuildConfigurator) {
        deviceBuildConfigurator.configureDeviceFeatures().getVk10Features().shaderClipDistance(true);
    }
}
