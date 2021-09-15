package graphics.kiln.rosella.render.info;

import graphics.kiln.rosella.Rosella;
import graphics.kiln.rosella.render.descriptorsets.DescriptorSets;
import graphics.kiln.rosella.render.material.Material;
import graphics.kiln.rosella.render.swapchain.Swapchain;
import graphics.kiln.rosella.device.VulkanDevice;
import graphics.kiln.rosella.memory.Memory;
import graphics.kiln.rosella.memory.MemoryCloseable;
import graphics.kiln.rosella.ubo.Ubo;
import org.jetbrains.annotations.NotNull;

/**
 * Info such as the {@link Material} and {@link Ubo} for rendering objects
 */
public record InstanceInfo(Ubo ubo, Material material) implements MemoryCloseable {

    @Override
    public void free(VulkanDevice device, Memory memory) {
        ubo.free(device, memory);
        material.pipeline().getShaderProgram().getDescriptorManager().freeDescriptorSets(ubo.getDescriptors());
    }

    /**
     * Called when Command Buffers need to be refreshed.
     *
     * @param rosella the Rosella
     */
    public void rebuild(@NotNull Rosella rosella) {
        if (ubo.getUniformBuffers().size() == 0) {
            ubo.create(rosella.renderer.swapchain);
            material.pipeline().getShaderProgram().getDescriptorManager().createNewDescriptor(material.textures(), ubo);
        }
    }

    /**
     * Called when the {@link Swapchain} needs to be recreated. all {@link DescriptorSets}'s will need to be recreated
     *
     * @param rosella the Rosella
     */
    public void hardRebuild(@NotNull Rosella rosella) {
        material.pipeline().getShaderProgram().getDescriptorManager().clearDescriptorSets(ubo.getDescriptors());
        ubo.free(rosella.common.device, rosella.common.memory);

        if (ubo.getUniformBuffers().size() == 0) {
            ubo.create(rosella.renderer.swapchain);
        }

        material.pipeline().getShaderProgram().getDescriptorManager().createNewDescriptor(material.textures(), ubo);
    }
}
