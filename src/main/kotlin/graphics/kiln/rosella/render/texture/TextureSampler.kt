package graphics.kiln.rosella.render.texture

import graphics.kiln.rosella.device.VulkanDevice
import graphics.kiln.rosella.memory.Memory
import graphics.kiln.rosella.memory.MemoryCloseable
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10
import org.lwjgl.vulkan.VkSamplerCreateInfo

/**
 * The creation info for creating a Texture Sampler
 */
class TextureSampler(private val createInfo: SamplerCreateInfo, device: VulkanDevice): MemoryCloseable {
    var pointer = 0L

    init {
        MemoryStack.stackPush().use { stack ->
            val samplerInfo = VkSamplerCreateInfo.calloc(stack)
                .sType(VK10.VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO)
                .magFilter(createInfo.filter.vkType)
                .minFilter(createInfo.filter.vkType)
                .addressModeU(createInfo.wrap.vkType)
                .addressModeV(createInfo.wrap.vkType)
                .addressModeW(createInfo.wrap.vkType)
                .anisotropyEnable(createInfo.filter == TextureFilter.LINEAR)
                .maxAnisotropy(16.0f)
                .borderColor(VK10.VK_BORDER_COLOR_INT_OPAQUE_BLACK)
                .unnormalizedCoordinates(false)
                .compareEnable(false)
                .compareOp(VK10.VK_COMPARE_OP_ALWAYS)
            if (createInfo.filter.vkType == VK10.VK_FILTER_LINEAR) {
                samplerInfo.mipmapMode(VK10.VK_SAMPLER_MIPMAP_MODE_LINEAR)
            } else {
                samplerInfo.mipmapMode(VK10.VK_SAMPLER_MIPMAP_MODE_NEAREST)
            }
            val pTextureSampler = stack.mallocLong(1)
            if (VK10.vkCreateSampler(device.rawDevice, samplerInfo, null, pTextureSampler) != VK10.VK_SUCCESS) {
                throw RuntimeException("Failed to create texture sampler")
            }
            pointer = pTextureSampler[0]
        }
    }

    override fun free(device: VulkanDevice?, memory: Memory?) {
        memory!!.freeSampler(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TextureSampler

        if (createInfo != other.createInfo) return false
        if (pointer != other.pointer) return false

        return true
    }

    override fun hashCode(): Int {
        var result = createInfo.hashCode()
        result = 31 * result + pointer.hashCode()
        return result
    }

}
