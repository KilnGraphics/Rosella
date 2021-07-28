package me.hydos.rosella.init;

import me.hydos.rosella.Rosella;
import me.hydos.rosella.device.VulkanDevice;
import me.hydos.rosella.device.VulkanQueue;
import me.hydos.rosella.init.features.ApplicationFeature;
import me.hydos.rosella.util.VkUtils;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Used to build devices.
 *
 * This class will select and create the best device based on the criteria specified by the provided InitializationRegistry.
 */
public class DeviceBuilder {

    private final List<ApplicationFeature> applicationFeatures;
    private final Set<String> requiredFeatures;
    private final VulkanInstance instance;

    public DeviceBuilder(@NotNull VulkanInstance instance, @NotNull InitializationRegistry registry) {
        this.instance = instance;
        this.applicationFeatures = registry.getOrderedFeatures();
        this.requiredFeatures = registry.getRequiredApplicationFeatures();
    }

    /**
     * Enumerates all available devices and selects the best. If no compatible device can be found throws a
     * runtime error.
     *
     * @return The initialized vulkan device
     * @throws RuntimeException if no compatible device can be found or an error occurs
     */
    public VulkanDevice build() throws RuntimeException {
        List<DeviceMeta> devices = new ArrayList<>();
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer deviceCount = stack.mallocInt(1);
            VkUtils.ok(VK10.vkEnumeratePhysicalDevices(this.instance.getInstance(), deviceCount, null));

            PointerBuffer pPhysicalDevices = stack.mallocPointer(deviceCount.get(0));
            VkUtils.ok(VK10.vkEnumeratePhysicalDevices(this.instance.getInstance(), deviceCount, pPhysicalDevices));

            for(int i = 0; i < deviceCount.get(0); i++) {
                devices.add(new DeviceMeta(new VkPhysicalDevice(pPhysicalDevices.get(i), this.instance.getInstance()), stack));
            }

            devices.forEach(DeviceMeta::processSupport);
            devices.sort((a, b) -> { // This is sorting in descending order so that we can use the first device
                if(!a.isValid() || !b.isValid()) {
                    if(a.isValid()) {
                        return -1;
                    }
                    if(b.isValid()) {
                        return 1;
                    }
                    return 0;
                }
                if(a.getFeatureRanking() != b.getFeatureRanking()) {
                    return (int) (b.getFeatureRanking() - a.getFeatureRanking());
                }
                if(a.getPerformanceRanking() != b.getPerformanceRanking()) {
                    return b.getPerformanceRanking() - a.getPerformanceRanking();
                }
                return 0;
            });

            DeviceMeta selectedDevice = devices.get(0);
            if(!selectedDevice.isValid()) {
                throw new RuntimeException("Failed to find suitable device");
            }

            return selectedDevice.createDevice();
        }
    }

    public class DeviceMeta {
        private final MemoryStack stack;

        private final Set<String> unsatisfiedRequirements = new HashSet<>();
        private final Map<String, ApplicationFeature.Instance> features = new HashMap<>();
        private final ArrayList<ApplicationFeature.Instance> sortedFeatures = new ArrayList<>();

        public final VkPhysicalDevice physicalDevice;
        public final VkPhysicalDeviceProperties properties;
        public final VkPhysicalDeviceMemoryProperties memoryProperties;
        public final VkPhysicalDeviceFeatures availableFeatures;
        public final Map<String, VkExtensionProperties> extensionProperties;
        public final List<VkQueueFamilyProperties> queueFamilyProperties;

        private boolean isBuilding = false;
        private final List<QueueRequest> queueRequests = new ArrayList<>();
        private final Set<String> enabledExtensions = new HashSet<>();
        private final VkPhysicalDeviceFeatures enabledFeatures;

        private DeviceMeta(VkPhysicalDevice physicalDevice, MemoryStack stack) {
            this.stack = stack;
            this.physicalDevice = physicalDevice;
            this.unsatisfiedRequirements.addAll(requiredFeatures);
            applicationFeatures.forEach(feature -> sortedFeatures.add(feature.createInstance()));
            this.sortedFeatures.forEach(feature -> features.put(feature.getFeatureName(), feature));

            IntBuffer count = stack.mallocInt(1);

            this.properties = VkPhysicalDeviceProperties.mallocStack(stack);
            VK10.vkGetPhysicalDeviceProperties(physicalDevice, this.properties);

            this.memoryProperties = VkPhysicalDeviceMemoryProperties.mallocStack(stack);
            VK10.vkGetPhysicalDeviceMemoryProperties(physicalDevice, memoryProperties);

            this.availableFeatures = VkPhysicalDeviceFeatures.mallocStack(stack);
            VK10.vkGetPhysicalDeviceFeatures(physicalDevice, availableFeatures);

            VK10.vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, count, null);
            VkQueueFamilyProperties.Buffer queueFamilyPropertiesBuffer = VkQueueFamilyProperties.mallocStack(count.get(0), stack);
            VK10.vkGetPhysicalDeviceQueueFamilyProperties(physicalDevice, count, queueFamilyPropertiesBuffer);
            ArrayList<VkQueueFamilyProperties> queueFamilyPropertiesList = new ArrayList<>();
            for(int i = 0; i < count.get(0); i++) {
                queueFamilyPropertiesList.add(queueFamilyPropertiesBuffer.get(i));
            }
            this.queueFamilyProperties = Collections.unmodifiableList(queueFamilyPropertiesList);

            VkUtils.ok(VK10.vkEnumerateDeviceExtensionProperties(this.physicalDevice, (CharSequence) null, count, null));
            VkExtensionProperties.Buffer extensionPropertiesBuffer = VkExtensionProperties.mallocStack(count.get(0), stack);
            VkUtils.ok(VK10.vkEnumerateDeviceExtensionProperties(this.physicalDevice, (CharSequence) null, count, extensionPropertiesBuffer));
            Map<String, VkExtensionProperties> extensionPropertiesMap = new HashMap<>();
            for(int i = 0; i < count.get(0); i++) {
                VkExtensionProperties properties = extensionPropertiesBuffer.get(i);
                extensionPropertiesMap.put(properties.extensionNameString(), properties);
            }
            this.extensionProperties = Collections.unmodifiableMap(extensionPropertiesMap);

            this.enabledFeatures = VkPhysicalDeviceFeatures.callocStack(stack);
        }

        private void processSupport() {
            for(ApplicationFeature.Instance feature : this.sortedFeatures) {
                try {
                    feature.testFeatureSupport(this);
                    if(feature.isSupported()) {
                        this.unsatisfiedRequirements.remove(feature.getFeatureName());
                    }
                } catch (Exception ex) {
                    Rosella.LOGGER.warn("Exception during support test for feature \"" + feature.getFeatureName() + "\"", ex);
                }
            }
        }

        /**
         * @return true if all required features are met by this device.
         */
        private boolean isValid() {
            return unsatisfiedRequirements.isEmpty();
        }

        /**
         * @return A ranking based on what features are supported. The greater the better.
         */
        private long getFeatureRanking() {
            return this.sortedFeatures.stream().filter(ApplicationFeature.Instance::isSupported).count();
        }

        /**
         * @return A ranking based on what the expected performance of the device is. The greater the better.
         */
        private int getPerformanceRanking() {
            return switch (properties.deviceType()) {
                case VK10.VK_PHYSICAL_DEVICE_TYPE_CPU, VK10.VK_PHYSICAL_DEVICE_TYPE_OTHER -> 0;
                case VK10.VK_PHYSICAL_DEVICE_TYPE_VIRTUAL_GPU -> 1;
                case VK10.VK_PHYSICAL_DEVICE_TYPE_INTEGRATED_GPU -> 2;
                case VK10.VK_PHYSICAL_DEVICE_TYPE_DISCRETE_GPU -> 3;
                default -> throw new RuntimeException("Device type was not recognized!");
            };
        }

        private VulkanDevice createDevice() {
            assert(!this.isBuilding);
            this.isBuilding = true;

            Map<String, Object> enabledFeatures = new HashMap<>();

            for(ApplicationFeature.Instance feature : this.sortedFeatures) {
                try {
                    if(feature.isSupported()) {
                        Object metadata = feature.enableFeature(this);
                        enabledFeatures.put(feature.getFeatureName(), metadata);
                    }
                } catch (Exception ex) {
                    Rosella.LOGGER.warn("Exception while enabling feature \"" + feature.getFeatureName() + "\"", ex);
                }
            }

            VkDeviceCreateInfo deviceInfo = VkDeviceCreateInfo.callocStack(this.stack);
            deviceInfo.sType(VK10.VK_STRUCTURE_TYPE_DEVICE_CREATE_INFO);
            deviceInfo.pQueueCreateInfos(this.generateQueueMappings());
            deviceInfo.ppEnabledExtensionNames(this.generateEnabledExtensionNames());
            deviceInfo.pEnabledFeatures(this.enabledFeatures);

            PointerBuffer pDevice = this.stack.mallocPointer(1);
            VkUtils.ok(VK10.vkCreateDevice(this.physicalDevice, deviceInfo, null, pDevice));

            VkDevice device = new VkDevice(pDevice.get(0), this.physicalDevice, deviceInfo);

            this.fulfillQueueRequests(device);

            return new VulkanDevice(device, enabledFeatures);
        }

        private VkDeviceQueueCreateInfo.Buffer generateQueueMappings() {
            int[] nextQueueIndices = new int[this.queueFamilyProperties.size()];

            for(QueueRequest request : this.queueRequests) {
                int index = nextQueueIndices[request.requestedFamily]++;
                request.assignedIndex = index % this.queueFamilyProperties.get(request.requestedFamily).queueCount();
            }

            int familyCount = 0;
            for(int i : nextQueueIndices) {
                if(i != 0) {
                    familyCount++;
                }
            }

            VkDeviceQueueCreateInfo.Buffer queueCreateInfos = VkDeviceQueueCreateInfo.callocStack(familyCount, this.stack);

            for(int family = 0; family < nextQueueIndices.length; family++) {
                if(nextQueueIndices[family] == 0) {
                    continue;
                }


                FloatBuffer priorities = this.stack.mallocFloat(Math.min(nextQueueIndices[family], this.queueFamilyProperties.get(family).queueCount()));
                while(priorities.hasRemaining()) {
                    priorities.put(1.0f);
                }
                priorities.rewind();

                VkDeviceQueueCreateInfo info = queueCreateInfos.get();
                info.sType(VK10.VK_STRUCTURE_TYPE_DEVICE_QUEUE_CREATE_INFO);
                info.queueFamilyIndex(family);
                info.pQueuePriorities(priorities);
            }

            return queueCreateInfos.rewind();
        }

        private void fulfillQueueRequests(VkDevice device) {
            int queueFamilyCount = this.queueFamilyProperties.size();
            int maxQueueCount = this.queueFamilyProperties.stream().map(VkQueueFamilyProperties::queueCount).max(Comparator.comparingInt(a -> a)).orElse(0);

            VulkanQueue[][] requests = new VulkanQueue[queueFamilyCount][maxQueueCount];

            PointerBuffer pQueue = this.stack.mallocPointer(1);

            for(QueueRequest request : this.queueRequests) {
                int f = request.requestedFamily, i = request.assignedIndex;
                if(requests[f][i] == null) {
                    VK10.vkGetDeviceQueue(device, f, i, pQueue);
                    requests[f][i] = new VulkanQueue(new VkQueue(pQueue.get(0), device), f);
                }

                request.future.complete(requests[f][i]);
            }
        }

        private PointerBuffer generateEnabledExtensionNames() {
            if(this.enabledExtensions.isEmpty()) {
                return null;
            }

            PointerBuffer names = this.stack.mallocPointer(this.enabledExtensions.size());
            for(String extension : this.enabledExtensions) {
                names.put(this.stack.UTF8(extension));
            }

            return names.rewind();
        }

        public boolean isApplicationFeatureSupported(String name) {
            ApplicationFeature.Instance feature = this.features.getOrDefault(name, null);
            if(feature == null) {
                return false;
            }

            return feature.isSupported();
        }

        public ApplicationFeature.Instance getApplicationFeature(String name) {
            return this.features.getOrDefault(name, null);
        }

        /**
         * Checks if there exists a queue family that supports all specified flags.
         *
         * @param flags The flags the queue must support
         * @return true if a queue family exists supporting all specified flags
         */
        public boolean hasQueueWithFlags(int flags) {
            return this.queueFamilyProperties.stream().anyMatch(props -> (props.queueFlags() & flags) == flags);
        }

        /**
         * Checks if this device supports a extension.
         *
         * @param name The name of the extension.
         * @return True if the extension is supported, false otherwise.
         */
        public boolean isExtensionSupported(String name) {
            return this.extensionProperties.containsKey(name);
        }

        /**
         * Adds a new queue request. Must only be called during the device configuration process.
         *
         * @param family The family that is requested.
         * @return A QueueRequest instance that can be used to later retrieve the requested queue.
         */
        public Future<VulkanQueue> addQueueRequest(int family) {
            assert(this.isBuilding);

            QueueRequest request = new QueueRequest(family);
            this.queueRequests.add(request);
            return request.future;
        }

        /**
         * Adds a extension to the set of enabled extensions. This function does not validate if the extension
         * is actually supported.
         * Must only be called during the device configuration process.
         *
         * @param extension The name of the extension.
         */
        public void enableExtension(String extension) {
            assert(this.isBuilding);

            this.enabledExtensions.add(extension);
        }

        /**
         * Returns an instance that can be used to configure device features.
         */
        public VkPhysicalDeviceFeatures configureDeviceFeatures() {
            assert(this.isBuilding);

            return this.enabledFeatures;
        }

        private static class QueueRequest {
            private final int requestedFamily;
            private int assignedIndex;
            private CompletableFuture<VulkanQueue> future;

            private QueueRequest(int family) {
                this.requestedFamily = family;
                this.future = new CompletableFuture<>();
            }
        }
    }
}
