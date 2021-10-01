package graphics.kiln.rosella.init.features;

import graphics.kiln.rosella.init.DeviceBuildConfigurator;
import graphics.kiln.rosella.init.DeviceBuildInformation;
import graphics.kiln.rosella.util.NamedID;

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
