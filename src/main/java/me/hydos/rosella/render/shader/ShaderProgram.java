package me.hydos.rosella.render.shader;

import me.hydos.rosella.Rosella;
import me.hydos.rosella.device.VulkanDevice;
import me.hydos.rosella.render.util.SpirV;
import me.hydos.rosella.render.util.SprirVUtilsKt;
import me.hydos.rosella.ubo.DescriptorManager;
import me.hydos.rosella.vkobjects.VkCommon;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import static me.hydos.rosella.util.VkUtils.ok;
import static org.lwjgl.vulkan.VK10.VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO;
import static org.lwjgl.vulkan.VK10.vkCreateShaderModule;

/**
 * Represents an ShaderProgram.
 */
public class ShaderProgram {

    private final VkCommon common;
    public final DescriptorManager descriptorManager;
    public final RawShaderProgram raw;
    public final int maxObjectCount;

    private boolean shadersCompiled;
    private SpirV vertexShaderSource;
    private SpirV fragmentShaderSource;
    private long pVertexShader;
    private long pFragmentShader;

    public ShaderProgram(RawShaderProgram raw, Rosella rosella, int maxObjectCount) {
        this.common = rosella.common;
        this.descriptorManager = new DescriptorManager(maxObjectCount, this, rosella.renderer.swapchain, rosella.common.device, rosella.common.memory);
        this.raw = raw;
        this.maxObjectCount = maxObjectCount;
    }

    /**
     * Compile all shaders.
     */
    public void compileShaders() {
        if (!shadersCompiled) {
            if (raw.getVertexShader() == null || raw.getFragmentShader() == null) {
                throw new RuntimeException("Tried to compile shaders when one or more shaders are null.");
            }
            this.vertexShaderSource = SprirVUtilsKt.compileSprirV(raw.getVertexShader(), ShaderType.VERTEX_SHADER);
            this.fragmentShaderSource = SprirVUtilsKt.compileSprirV(raw.getFragmentShader(), ShaderType.FRAGMENT_SHADER);
            this.pVertexShader = createShader(vertexShaderSource.bytecode(), common.device);
            this.pFragmentShader = createShader(fragmentShaderSource.bytecode(), common.device);
            this.shadersCompiled = true;
        }
    }

    /**
     * Create a Vulkan shader module. used during pipeline creation.
     */
    private long createShader(ByteBuffer spirVBytecode, VulkanDevice device) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            VkShaderModuleCreateInfo createInfo = VkShaderModuleCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
                    .pCode(spirVBytecode);
            LongBuffer pShaderModule = stack.mallocLong(1);
            ok(vkCreateShaderModule(
                    device.getRawDevice(),
                    createInfo,
                    null,
                    pShaderModule
            ), "Failed to create shader module");
            return pShaderModule.get(0);
        }
    }

    public void free() {
        if (shadersCompiled) {
            vertexShaderSource.free();
            fragmentShaderSource.free();
            // TODO: i just realised we dont free shader modules
            raw.free();
        }
    }

    public long getVertexShader() {
        if (!shadersCompiled) {
            throw new RuntimeException("You must compile shaders before you can retrieve shaders.");
        }
        return createShader(vertexShaderSource.bytecode(), common.device); // FIXME: WHAT THE FUCK. unless i recreate it everytime, this manages to cause a seg fault... FRWDESFGRGTRHBRF
    }

    public long getFragmentShader() {
        return createShader(fragmentShaderSource.bytecode(), common.device);
    }
}
