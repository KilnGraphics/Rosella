package graphics.kiln.rosella.render.graph.resources;

import org.lwjgl.vulkan.VK10;

public enum ImageSampleCount {
    SAMPLE_COUNT_1(VK10.VK_SAMPLE_COUNT_1_BIT),
    SAMPLE_COUNT_2(VK10.VK_SAMPLE_COUNT_2_BIT),
    SAMPLE_COUNT_4(VK10.VK_SAMPLE_COUNT_4_BIT),
    SAMPLE_COUNT_8(VK10.VK_SAMPLE_COUNT_8_BIT),
    SAMPLE_COUNT_16(VK10.VK_SAMPLE_COUNT_16_BIT),
    SAMPLE_COUNT_32(VK10.VK_SAMPLE_COUNT_32_BIT),
    SAMPLE_COUNT_64(VK10.VK_SAMPLE_COUNT_64_BIT);

    public final int vulkan;

    ImageSampleCount(int vulkan) {
        this.vulkan = vulkan;
    }
}
