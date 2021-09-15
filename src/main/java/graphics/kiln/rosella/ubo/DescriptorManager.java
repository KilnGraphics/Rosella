package graphics.kiln.rosella.ubo;

import graphics.kiln.rosella.memory.Memory;
import graphics.kiln.rosella.render.descriptorsets.DescriptorSets;
import graphics.kiln.rosella.render.shader.ShaderProgram;
import graphics.kiln.rosella.render.swapchain.Swapchain;
import graphics.kiln.rosella.device.VulkanDevice;
import graphics.kiln.rosella.render.texture.TextureMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Manages Descriptor Sets. Allows them to be reused.
 */
public class DescriptorManager {

    private static final Logger LOGGER = LogManager.getLogger("DescriptorManager");
    private final ShaderProgram program;
    private final Swapchain swapchain;
    private final VulkanDevice device;
    private final Memory memory;
    private final int maxObjects;
    private int activeDescriptorCount;

    /**
     * Creates a new {@link DescriptorManager} object
     *
     * @param maxObjects the max amount of DescriptorSet's
     * @param program    the {@link ShaderProgram} to base it off
     */
    public DescriptorManager(int maxObjects, ShaderProgram program, Swapchain swapchain, VulkanDevice device, Memory memory) {
        this.maxObjects = maxObjects;
        this.program = program;
        this.swapchain = swapchain;
        this.device = device;
        this.memory = memory;
    }

    /**
     * Allocates a new {@link DescriptorSets}. This should only be called when no free {@link DescriptorSets}'s are available
     *
     * @param textures the {@link TextureMap[]} to use with the {@link DescriptorSets}
     * @param ubo      the {@link Ubo} to use with the {@link DescriptorSets}
     */
    public void createNewDescriptor(TextureMap textures, Ubo ubo) {
        activeDescriptorCount++;
        if (maxObjects <= activeDescriptorCount) {
            throw new RuntimeException("Too many Descriptor Sets are being used at once (max is " + activeDescriptorCount + ")");
        }
        program.getRaw().createDescriptorSets(swapchain, LOGGER, textures, ubo);
    }

    public void freeDescriptorSets(DescriptorSets set) {
        set.free(device, memory);
        activeDescriptorCount--;
    }

    public void clearDescriptorSets(DescriptorSets set) {
        set.clear();
        activeDescriptorCount--;
    }
}
