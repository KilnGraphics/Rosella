package graphics.kiln.rosella.render.graph.resources;

import graphics.kiln.rosella.util.ImageFormat;
import org.lwjgl.vulkan.VK10;

public record ImageSpec(ImageFormat format, int width, int height, int depth, int mipLevels, int arrayLayers, ImageSampleCount sampleCount) {

    public static ImageSpec create1D(ImageFormat format, int width) {
        return new ImageSpec(format, width, 0, 0, 1, 1, ImageSampleCount.SAMPLE_COUNT_1);
    }

    public static ImageSpec create1D(ImageFormat format, int width, int mipLevels, int arrayLayers, ImageSampleCount sampleCount) {
        return new ImageSpec(format, width, 0, 0, mipLevels, arrayLayers, sampleCount);
    }

    public static ImageSpec create2D(ImageFormat format, int width, int height) {
        return new ImageSpec(format, width, height, 0, 1, 1, ImageSampleCount.SAMPLE_COUNT_1);
    }

    public static ImageSpec create2D(ImageFormat format, int width, int height, ImageSampleCount sampleCount) {
        return new ImageSpec(format, width, height, 0, 1, 1, sampleCount);
    }

    public static ImageSpec create2D(ImageFormat format, int width, int height, int mipLevels, int arrayLayers, ImageSampleCount sampleCount) {
        return new ImageSpec(format, width, height, 0, mipLevels, arrayLayers, sampleCount);
    }

    public static ImageSpec create3D(ImageFormat format, int width, int height, int depth) {
        return new ImageSpec(format, width, height, depth, 1, 1, ImageSampleCount.SAMPLE_COUNT_1);
    }

    public static ImageSpec create3D(ImageFormat format, int width, int height, int depth, int mipLevels) {
        return new ImageSpec(format, width, height, depth, mipLevels, 1, ImageSampleCount.SAMPLE_COUNT_1);
    }

    public static ImageSpec create3D(ImageFormat format, int width, int height, int depth, int mipLevels, ImageSampleCount sampleCount) {
        return new ImageSpec(format, width, height, depth, mipLevels, 1, sampleCount);
    }

    public int getImageType() {
        if(depth != 0) {
            return VK10.VK_IMAGE_TYPE_3D;
        }
        if(height != 0) {
            return VK10.VK_IMAGE_TYPE_2D;
        }
        return VK10.VK_IMAGE_TYPE_1D;
    }
}
