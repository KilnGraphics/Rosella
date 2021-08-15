package me.hydos.rosella.render.texture

import me.hydos.rosella.Rosella
import me.hydos.rosella.render.resource.Resource
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer

class StbiImage(byteBuffer: ByteBuffer, private var format: ImageFormat) : UploadableImage {

    private var width: Int
    private var height: Int
    private var size: Int
    private var pixels: ByteBuffer

    constructor(resource: Resource, format: ImageFormat) : this(resource.readAllBytes(true), format)

    init {
        MemoryStack.stackPush().use { stack ->
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)
            val pChannels = stack.mallocInt(1)
            var pixels: ByteBuffer? =
                STBImage.stbi_load_from_memory(byteBuffer, pWidth, pHeight, pChannels, 4)
            format = ImageFormat.RGBA
            if (pixels == null) {
                Rosella.LOGGER.warn("Failed to load image properly! Falling back to loading raw pixel data.")
                pixels = byteBuffer
            }

            this.width = pWidth[0]
            this.height = pHeight[0]
            this.size = width * height * format.pixelSize
            this.pixels = pixels
        }
    }


    override fun getWidth(): Int {
        return width
    }

    override fun getHeight(): Int {
        return height
    }

    override fun getFormat(): ImageFormat {
        return format
    }

    override fun getSize(): Int {
        return size
    }

    override fun getPixels(): ByteBuffer {
        return pixels
    }
}
