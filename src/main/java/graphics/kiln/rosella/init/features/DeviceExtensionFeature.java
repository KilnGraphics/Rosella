package graphics.kiln.rosella.init.features;

import graphics.kiln.rosella.init.DeviceBuildConfigurator;
import graphics.kiln.rosella.init.DeviceBuildInformation;
import graphics.kiln.rosella.util.NamedID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

public class DeviceExtensionFeature extends SimpleApplicationFeature {

    public DeviceExtensionFeature(@NotNull String extensionName, @NotNull Collection<NamedID> dependencies) {
        this(extensionName, dependencies, null, null);
    }

    public DeviceExtensionFeature(@NotNull String extensionName, @Nullable Function<DeviceBuildInformation, Boolean> testFunc, @Nullable Consumer<DeviceBuildConfigurator> enableFunc) {
        this(extensionName, null, testFunc, enableFunc);
    }

    public DeviceExtensionFeature(@NotNull String extensionName, @Nullable Collection<NamedID> dependencies, @Nullable Function<DeviceBuildInformation, Boolean> testFunc, @Nullable Consumer<DeviceBuildConfigurator> enableFunc) {
        super(getIdFromExtensionName(extensionName), dependencies, getCombinedTestFunction(extensionName, testFunc), getCombinedEnableFunction(extensionName, enableFunc));
    }

    private static Function<DeviceBuildInformation, Boolean> getCombinedTestFunction(String extensionName, @Nullable Function<DeviceBuildInformation, Boolean> otherTestFunc) {
        if (otherTestFunc == null) {
            return meta -> meta.isExtensionAvailable(extensionName);
        } else {
            return meta -> meta.isExtensionAvailable(extensionName) && otherTestFunc.apply(meta);
        }
    }

    private static Consumer<DeviceBuildConfigurator> getCombinedEnableFunction(String extensionName, @Nullable Consumer<DeviceBuildConfigurator> otherEnableFunc) {
        if (otherEnableFunc == null) {
            return meta -> meta.enableExtension(extensionName);
        } else {
            return meta -> {
                meta.enableExtension(extensionName);
                otherEnableFunc.accept(meta);
            };
        }
    }

    public static NamedID getIdFromExtensionName(String extensionName) {
        return new NamedID("rosella:" + extensionName); // TODO: should this be in the rosella namespace?
    }
}
